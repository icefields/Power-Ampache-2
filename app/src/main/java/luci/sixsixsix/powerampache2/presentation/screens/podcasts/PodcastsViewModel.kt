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
package luci.sixsixsix.powerampache2.presentation.screens.podcasts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.PodcastRepository
import luci.sixsixsix.powerampache2.domain.errors.PodcastsDisabledException
import javax.inject.Inject

@HiltViewModel
class PodcastsViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
) : ViewModel() {
    var state by mutableStateOf(PodcastsState())
        private set

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        podcastRepository.getPodcasts(fetchRemote = true).collect { result ->
            state = when (result) {
                is Resource.Success -> state.copy(
                    podcasts = result.data ?: listOf(),
                    isDisabledOnServer = false
                )
                is Resource.Error -> {
                    val isDisabledOnServer = result.exception is PodcastsDisabledException
                    state.copy(
                        isLoading = false,
                        isDisabledOnServer = isDisabledOnServer,
                        // the disabled message must not draw over a stale list
                        podcasts = if (isDisabledOnServer) listOf() else state.podcasts
                    )
                }
                is Resource.Loading -> state.copy(isLoading = result.isLoading)
            }
        }
    }
}
