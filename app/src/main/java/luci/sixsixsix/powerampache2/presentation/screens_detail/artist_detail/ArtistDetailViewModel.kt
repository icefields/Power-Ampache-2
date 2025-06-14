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
package luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.delegates.FetchArtistSongsHandler
import luci.sixsixsix.powerampache2.common.delegates.FetchArtistSongsHandlerImpl
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import luci.sixsixsix.powerampache2.domain.usecase.artists.SongsFromArtistUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.LocalSettingsFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ToggleGlobalShuffleUseCase
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AlbumsRepository,
    private val artistsRepository: ArtistsRepository,
    settingsFlow: LocalSettingsFlowUseCase,
    offlineModeFlowUseCase: OfflineModeFlowUseCase,
    songsFromArtistUseCase: SongsFromArtistUseCase,
    private val toggleGlobalShuffle: ToggleGlobalShuffleUseCase,
    private val playlistManager: MusicPlaylistManager
) : ViewModel(), FetchArtistSongsHandler by FetchArtistSongsHandlerImpl(songsFromArtistUseCase) {

    var state by mutableStateOf(ArtistDetailState())
    //private var isOfflineModeState by mutableStateOf(false)
    val isOfflineModeState = offlineModeFlowUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)


    val globalShuffleStateFlow = settingsFlow()
        .map { it.isGlobalShuffleEnabled }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalSettings.SETTINGS_DEFAULTS_GLOBAL_SHUFFLE)

    init {
        savedStateHandle.get<String>("artistId")?.let {id ->
            getAlbumsFromArtist(id)

            savedStateHandle.get<Artist>("artist")?.let { artist ->
                state = state.copy(artist = artist)
            } ?: getArtist(id)
        }

//        viewModelScope.launch {
//            offlineModeFlowUseCase().collectLatest {
//                isOfflineModeState = it
//            }
//        }
    }

    fun onEvent(event: ArtistDetailEvent) {
        when(event) {
            is ArtistDetailEvent.Refresh -> {
                state.artist?.let {
                    getAlbumsFromArtist(artistId = it.id, fetchRemote = true)
                }
            }
            is ArtistDetailEvent.Fetch -> {
                getAlbumsFromArtist(artistId = event.albumId, fetchRemote = true)
            }

            ArtistDetailEvent.OnFavouriteArtist -> favouriteArtist()
            ArtistDetailEvent.OnShufflePlaylistToggle -> viewModelScope.launch {
                try {
                    toggleGlobalShuffle()
                } catch (e: Exception) {
                    playlistManager.updateErrorLogMessage(e.stackTraceToString())
                }
            }
        }
    }

    private fun favouriteArtist(artistId: String = state.artist.id) = viewModelScope.launch {
        artistsRepository.likeArtist(artistId, (state.artist.flag != 1))
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh artist
                            state = state.copy(artist = state.artist.copy(flag = abs(state.artist.flag - 1)))
                        }
                    }
                    is Resource.Error -> state = state.copy(isLikeLoading = false)
                    is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
                }
            }
    }

    private fun getAlbumsFromArtist(artistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            repository
                .getAlbumsFromArtist(artistId, fetchRemote = fetchRemote)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(albums = albums)
                                L("viewmodel.getAlbumsFromArtist size ${result.data?.size} network: ${result.networkData?.size}")
                            }
                        }

                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }

    fun fetchSongsFromArtist(
        artistId: String = state.artist.id,
        fetchRemote: Boolean = true,
        songsCallback: (List<Song>) -> Unit
    ) = viewModelScope.launch {
        getSongsFromArtist(
            artistId = artistId, fetchRemote = fetchRemote,
            isOfflineMode = isOfflineModeState.value,
            songsCallback = songsCallback,
            loadingCallback = { state = state.copy(isLoading = it) },
            errorCallback = { state.copy(isLoading = false) }
        )
    }

//    fun getSongsFromArtist(
//        artistId: String = state.artist.id,
//        fetchRemote: Boolean = true,
//        songsCallback: (List<Song>) -> Unit
//    ) {
//        viewModelScope.launch {
//            artistsRepository
//                .getSongsFromArtist(artistId, fetchRemote = fetchRemote)
//                .collect { result ->
//                    when(result) {
//                        is Resource.Success -> {
//                            //L("viewmodel.getSongsFromArtist size ${isOfflineModeState} ${result.data?.size} network: ${result.networkData?.size}")
//
//                            if (result.networkData != null || isOfflineModeState) {
//                                // only get the data when a network response is returned
//                                // check against network data but use db data.
//                                // OR if in offline mode
//                                result.data?.let { songs ->
//                                    songsCallback(songs)
//                                }
//                            }
//                        }
//                        is Resource.Error -> state = state.copy(isLoading = false)
//                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
//                    }
//                }
//        }
//    }

    private fun getArtist(artistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            artistsRepository
                .getArtist(artistId, fetchRemote = fetchRemote)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { artist ->
                                state = state.copy(artist = artist)
                                L("viewmodel.getArtist size ${result.data} network: ${result.networkData}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
