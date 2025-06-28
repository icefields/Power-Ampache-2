/**
 * Copyright (C) 2025  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AmpachePreferencesRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreference
import luci.sixsixsix.powerampache2.domain.usecase.UserFlowUseCase
import javax.inject.Inject

@HiltViewModel
class AmpacheUserPreferencesViewModel @Inject constructor(
    private val repository: AmpachePreferencesRepository,
    userFlowUseCase: UserFlowUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    var state by mutableStateOf(AmpachePreferencesState())

    private var previousUserPreferences: Map<String, AmpachePreference> = mapOf()
    private var previousSystemPreferences: Map<String, AmpachePreference> = mapOf()

    private var userPreferenceJob: Job? = null
    private var systemPreferenceJob: Job? = null

    init {
        viewModelScope.launch {
            userFlowUseCase().filterNotNull().first().let { user ->
                getUserPreferences()
                getSystemPreferences()
            }
        }
    }

    fun onEvent(event: AmpacheUserPreferencesEvent) {
        when(event) {
            is AmpacheUserPreferencesEvent.GetUserPreferences -> getUserPreferences()
            is AmpacheUserPreferencesEvent.GetSystemPreferences -> getSystemPreferences()
            is AmpacheUserPreferencesEvent.UpdatePreference -> {
                userPreferenceJob?.cancel()
                state = state.copy(isLoading = false)
                userPreferenceJob = updatePreference(event.ampachePreference, event.newValue, false)
            }
            is AmpacheUserPreferencesEvent.UpdateSystemPreference -> {
                systemPreferenceJob?.cancel()
                state = state.copy(isLoading = false)
                systemPreferenceJob = updatePreference(event.ampachePreference, event.newValue, true)
            }
            is AmpacheUserPreferencesEvent.Undo -> {
                // TODO: make batch network request to restore old state.
                //  keep track of changes when made.
                //  check edge cases.
                // state = state.copy(userPreferences = previousUserPreferences, systemPreferences = previousSystemPreferences)
            }
        }
    }

    private fun updatePreference(ampachePreference: AmpachePreference, newValue: String, isSystem: Boolean) = viewModelScope.launch {
        repository.updateAmpachePreference(
            filter = ampachePreference.name,
            value = newValue,
            applyToAll = isSystem
        ).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        var updated = false
                        if (isSystem) {
                            if (state.systemPreferences.containsKey(data.name)) {
                                updated = true
                                state = state.copy(
                                    systemPreferences = state.systemPreferences.toMutableMap()
                                        .apply {
                                            this[data.name] = data
                                        }
                                )
                            }
                        }
                        // user preference needs to be updated both when isSystem is true or false
                        if (state.userPreferences.containsKey(data.name)) {
                            updated = true
                            state = state.copy(
                                userPreferences = state.userPreferences.toMutableMap().apply {
                                    this[data.name] = data
                                }
                            )
                        }
                        if (updated)
                            Toast.makeText(context, R.string.ampachePreferences_updated_toast, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> state = state.copy(isLoadingEdit = false)
                is Resource.Loading -> state = state.copy(isLoadingEdit = result.isLoading)
            }
        }
    }

    private fun getSystemPreferences() = viewModelScope.launch {
        repository.getAmpacheSystemPreferences().collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        val preferencesMap = data.associateBy { it.name }
                        previousSystemPreferences = preferencesMap
                        state = state.copy(systemPreferences = preferencesMap)
                    }
                }
                is Resource.Error -> state = state.copy(isLoading = false)
                is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun getUserPreferences() = viewModelScope.launch {
        repository.getAmpacheUserPreferences().collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        val preferencesMap = data.associateBy { it.name }
                        previousUserPreferences = preferencesMap
                        state = state.copy(userPreferences = preferencesMap)
                    }
                }
                is Resource.Error -> state = state.copy(isLoading = false)
                is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userPreferenceJob?.cancel()
        userPreferenceJob = null
        systemPreferenceJob?.cancel()
        systemPreferenceJob = null
    }
}
