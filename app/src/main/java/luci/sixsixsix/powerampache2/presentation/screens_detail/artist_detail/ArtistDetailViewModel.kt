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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.delegates.FetchArtistSongsHandler
import luci.sixsixsix.powerampache2.common.delegates.FetchArtistSongsHandlerImpl
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.getAvailableLyrics
import luci.sixsixsix.powerampache2.domain.usecase.albums.AlbumsFromArtistUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.ArtistUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.LikeArtistUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.SongsFromArtistUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.ArtistDataFromPluginUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsInfoPluginInstalled
import luci.sixsixsix.powerampache2.domain.usecase.settings.LocalSettingsFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ToggleGlobalShuffleUseCase
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val albumsFromArtistUseCase: AlbumsFromArtistUseCase,
    private val likeArtistUseCase: LikeArtistUseCase,
    settingsFlow: LocalSettingsFlowUseCase,
    val offlineModeFlowUseCase: OfflineModeFlowUseCase,
    songsFromArtistUseCase: SongsFromArtistUseCase,
    private val artistUseCase: ArtistUseCase,
    private val toggleGlobalShuffle: ToggleGlobalShuffleUseCase,
    private val isInfoPluginInstalled: IsInfoPluginInstalled,
    private val artistDataFromPluginUseCase: ArtistDataFromPluginUseCase,
    private val playlistManager: MusicPlaylistManager
) : ViewModel(), FetchArtistSongsHandler by FetchArtistSongsHandlerImpl(songsFromArtistUseCase) {

    var state by mutableStateOf(ArtistDetailState())
//    private val isOfflineModeState = offlineModeFlowUseCase()
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    val globalShuffleStateFlow = settingsFlow()
        .map { it.isGlobalShuffleEnabled }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalSettings.SETTINGS_DEFAULTS_GLOBAL_SHUFFLE)

    init {
        savedStateHandle.get<String>("artistId")?.let { id ->
            getAlbumsFromArtist(id)

            savedStateHandle.get<Artist>("artist")?.let { artist ->
                // if artist provided, first check if there's an entry in the db, if not use the provided
                // as fallback. This is important, because there is not certainty that the album is
                // in the internal db
                state = state.copy(artist = artist)

//                viewModelScope.launch {
//                    getArtistInfoFromPlugin(artist)
//                }

                getArtist(id, fetchRemote = false)
            } ?: getArtist(id, fetchRemote = true)
        }
    }

    fun onEvent(event: ArtistDetailEvent) {
        when(event) {
            is ArtistDetailEvent.Refresh ->
                getAlbumsFromArtist(artistId = state.artist.id, fetchRemote = true)
            is ArtistDetailEvent.Fetch ->
                getAlbumsFromArtist(artistId = event.albumId, fetchRemote = true)
            ArtistDetailEvent.OnFavouriteArtist ->
                favouriteArtist()
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
        likeArtistUseCase(artistId, (state.artist.flag != 1))
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
            albumsFromArtistUseCase(artistId, fetchRemote = fetchRemote)
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
            isOfflineMode = offlineModeFlowUseCase().first(),
            songsCallback = songsCallback,
            loadingCallback = { state = state.copy(isLoading = it) },
            errorCallback = { state.copy(isLoading = false) }
        )
    }

    private fun getArtist(artistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            artistUseCase(artistId, fetchRemote = fetchRemote)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { artist ->
                                state = state.copy(artist = artist)
                                L("viewmodel.getArtist size ${result.data} network: ${result.networkData}")
                                getArtistInfoFromPlugin(artist)
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }

    private suspend fun getArtistInfoFromPlugin(artist: Artist) {
        // only fetch if no lyrics already present
        if (isInfoPluginInstalled() && (state.infoPluginArtist == null || state.infoPluginArtist?.id != artist.id)) {
            artistDataFromPluginUseCase(artist = artist)?.let { pluginData ->
                println("aaaa getArtistInfoFromPlugin ${pluginData.description}")
                state = state.copy(infoPluginArtist = pluginData)
            }
        }
    }
}
