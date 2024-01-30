/**
 * Copyright (C) 2024  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.User
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val settingsRepository: SettingsRepository,
    private val musicRepository: MusicRepository,
) : AndroidViewModel(application) {
    //var state by mutableStateOf(LocalSettings.defaultSettings())
    var state by savedStateHandle.saveable {
        mutableStateOf(LocalSettings.defaultSettings())
    }

    var userState by savedStateHandle.saveable {
        mutableStateOf<User>(User.emptyUser())
    }

    var serverInfoState by mutableStateOf<ServerInfo?>(null)

    init {
        getServerInfo()
        observeSettings()

        viewModelScope.launch {
            musicRepository.userLiveData.observeForever {
                it?.let { user ->
                    userState = user
                }
            }
        }
    }

    private fun observeSettings() {
        settingsRepository.settingsLiveData.observeForever { localSettings ->
            localSettings?.let { updatedSettings ->
                if (updatedSettings != state)
                    state = updatedSettings
            }
        }
    }

    fun setTheme(theme: PowerAmpTheme) {
        viewModelScope.launch {
            settingsRepository.saveLocalSettings(
                settingsRepository.getLocalSettings(userState?.username).copy(theme = theme)
            )
        }
    }

    private fun getServerInfo() {
        viewModelScope.launch {
            serverInfoState = musicRepository.ping().data?.first
        }
    }
}
