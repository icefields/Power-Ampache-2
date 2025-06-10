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
package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import luci.sixsixsix.powerampache2.domain.usecase.settings.LocalSettingsFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ToggleGlobalShuffleUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.IsSongAvailableOfflineUseCase
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongWrapper
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    offlineModeFlowUseCase: OfflineModeFlowUseCase,
    localSettingsFlowUseCase: LocalSettingsFlowUseCase,
    private val isSongAvailableOfflineUseCase: IsSongAvailableOfflineUseCase,
    private val toggleGlobalShuffleUseCase: ToggleGlobalShuffleUseCase,
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val playlistManager: MusicPlaylistManager,
) : AndroidViewModel(application) {
    var state by mutableStateOf(AlbumDetailState())

    val offlineModeStateFlow = offlineModeFlowUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val globalShuffleStateFlow = localSettingsFlowUseCase()
        .map { it.isGlobalShuffleEnabled }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalSettings.SETTINGS_DEFAULTS_GLOBAL_SHUFFLE)

    @OptIn(ExperimentalCoroutinesApi::class)
    val albumStateFlow: StateFlow<Album> =
        savedStateHandle.getStateFlow<String?>("albumId", null)
            .filterNotNull()
            .flatMapConcat { albumId ->
                getSongsFromAlbum(albumId)
                albumsRepository
                    .getAlbum(albumId)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Album())

    init {
        viewModelScope.launch {
            playlistManager.downloadedSongFlow.collect { song ->
                if(song != null) {
                    L("RefreshFromCache")
                    // if song belongs to this album, refresh
                    if (state.songs.map { it.song }.map { it.mediaId }.contains(song.mediaId)) {
                        onEvent(AlbumDetailEvent.RefreshFromCache)
                    }
                }
            }
        }
    }

    fun onEvent(event: AlbumDetailEvent) {
        when (event) {
            is AlbumDetailEvent.Fetch -> {
                L("AlbumDetailEvent.Fetch", event.albumId)
                getSongsFromAlbum(albumId = event.albumId, fetchRemote = true)
            }
            is AlbumDetailEvent.OnSongSelected -> { }
            is AlbumDetailEvent.OnPlayAlbum -> { }
            AlbumDetailEvent.OnShareAlbum ->
                shareAlbum(albumStateFlow.value.id)
            AlbumDetailEvent.OnShuffleAlbum -> { }
            AlbumDetailEvent.OnFavouriteAlbum ->
                favouriteAlbum()
            AlbumDetailEvent.RefreshFromCache -> {
                L("AlbumDetailEvent.RefreshFromCache", albumStateFlow.value.id)
                if (!albumStateFlow.value.id.isNullOrBlank()) {
                    getSongsFromAlbum(albumId = albumStateFlow.value.id, fetchRemote = false)
                }
            }
            AlbumDetailEvent.OnShufflePlaylistToggle -> viewModelScope.launch {
                try {
                    toggleGlobalShuffleUseCase()
                } catch (e: Exception) {
                    playlistManager.updateErrorLogMessage(e.stackTraceToString())
                }
            }

            is AlbumDetailEvent.OnNewRating -> rateAlbum(rating = event.rating)
        }
    }

    private fun rateAlbum(albumId: String = albumStateFlow.value.id, rating: Int) = viewModelScope.launch {
        albumsRepository.rateAlbum(albumId, rating)
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh album
                            // state = state.copy(album = state.album.copy(rating = rating))
                        }
                    }
                    is Resource.Error -> state = state.copy( isLoading = false)
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                }
            }
    }


    private fun favouriteAlbum(albumId: String = albumStateFlow.value.id) = viewModelScope.launch {
        albumsRepository.likeAlbum(albumId, (albumStateFlow.value.flag != 1))
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh album
                            //state = state.copy(album = state.album.copy(flag = abs(state.album.flag - 1)))
                        }
                    }
                    is Resource.Error -> state = state.copy(isLikeLoading = false)
                    is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
                }
            }
    }

   /* private fun getAlbumInfo(albumId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            albumsRepository
                .getAlbum(albumId, fetchRemote)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            // TODO why am I using network data here? please comment
                            result.networkData?.let { album ->
                                state = state.copy(album = album)
                                L("AlbumDetailViewModel.getAlbumInfo size ${result.data} network: ${result.networkData}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }*/

    private fun isAlbumDownloaded(songs: List<SongWrapper>): Boolean {
        songs.forEach {
            if (!it.isOffline) return false
        }
        return true
    }

    private fun getSongsFromAlbum(albumId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            songsRepository
                .getSongsFromAlbum(albumId, fetchRemote)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(
                                        SongWrapper(
                                        song = song,
                                        isOffline = isSongAvailableOfflineUseCase(song)
                                    )
                                    )
                                }
                                state = state.copy(songs = songWrapperList, isAlbumDownloaded = isAlbumDownloaded(songWrapperList))
                                L("AlbumDetailViewModel.getSongsFromAlbum size", result.data?.size, "network", result.networkData?.size)
                            }
                        }

                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }

    private fun shareAlbum(albumId: String) = viewModelScope.launch {
        albumsRepository.getAlbumShareLink(albumId).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    application.shareLink(it)
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }
    }
}
