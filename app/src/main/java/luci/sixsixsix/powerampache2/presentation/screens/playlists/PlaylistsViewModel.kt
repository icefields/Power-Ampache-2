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
package luci.sixsixsix.powerampache2.presentation.screens.playlists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.common.Constants.ALWAYS_FETCH_ALL_PLAYLISTS
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.usecase.UserFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val repository: PlaylistsRepository,
    offlineModeFlow: OfflineModeFlowUseCase,
    userFlowUseCase: UserFlowUseCase
) : ViewModel() {
    var state by mutableStateOf(PlaylistsState())
    private var isEndOfDataReached: Boolean = false
    private lateinit var currentUsername: String

    val playlistsStateFlow: StateFlow<List<Playlist>> =
        offlineModeFlow()
            .flatMapLatest { repository.playlistsFlow }
            .filterNotNull()
            .distinctUntilChanged()
            .combine(userFlowUseCase().filterNotNull().distinctUntilChanged()) { playlists, user ->
                currentUsername = user.username
                playlists
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun isCurrentUserOwner(playlist: Playlist) =
        currentUsername.lowercase() == playlist.owner?.lowercase()

    fun onEvent(event: PlaylistEvent) {
        when (event) {
            is PlaylistEvent.Refresh ->
                getPlaylists(fetchRemote = true)
            is PlaylistEvent.OnSearchQueryChange -> if (event.query.isBlank() && state.searchQuery.isBlank()) {
                } else {
                    state = state.copy(searchQuery = event.query)
                    getPlaylists()
                }

            is PlaylistEvent.OnBottomListReached ->
                // right now we are fetching all the playlists available on the server
                if (!ALWAYS_FETCH_ALL_PLAYLISTS && !state.isFetchingMore && !isEndOfDataReached) {
                    L("PlaylistEvent.OnBottomListReached")
                    state = state.copy(isFetchingMore = true)
                    getPlaylists(fetchRemote = true, offset = playlistsStateFlow.value.size)
                }
            is PlaylistEvent.OnPlaylistDelete -> deletePlaylist(event.playlist.id)
            PlaylistEvent.OnRemovePlaylistDismiss -> viewModelScope.launch {
//                // TODO HACK FORCE refresh of list
//                val playlists = state.playlists.toMutableList()
//                state = state.copy(playlists = listOf())
//                delay(100)
//                state = state.copy(playlists = playlists)
            }
        }
    }

    private fun getPlaylists(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            repository
                .getPlaylists(fetchRemote, query, offset)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { playlists ->
                                // playlist updated automatically through live data from database
//                                state = state.copy(playlists = playlists)
//                                L("viewmodel.getPlaylists size", state.playlists.size)
                            }
                            isEndOfDataReached =
                                (result.networkData?.isEmpty() == true && offset > 0)
                            L("viewmodel.getPlaylists is bottom reached?", isEndOfDataReached, "offset", offset, "size of new array", result.networkData?.size)
                        }
                        is Resource.Error -> {
                            state = state.copy(isFetchingMore = false, isLoading = false)
                            L("ERROR PlaylistsViewModel", result.exception)
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                            if (!result.isLoading) {
                                state = state.copy(isFetchingMore = false)
                            }
                        }
                    }
                }
        }
    }

    private fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            repository
                .deletePlaylist(playlistId)
                .collect { result ->
                    when (result) {
                        is Resource.Success ->
                            result.data?.let { _ -> getPlaylists(fetchRemote = true) }
                        is Resource.Error ->
                            state = state.copy(isDeletePlaylistLoading = false)
                        is Resource.Loading ->
                            state = state.copy(isDeletePlaylistLoading = result.isLoading)

                    }
                }
        }
    }
}
