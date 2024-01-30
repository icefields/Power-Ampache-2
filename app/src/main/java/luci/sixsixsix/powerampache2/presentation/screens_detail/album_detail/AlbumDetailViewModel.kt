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
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.presentation.common.SongWrapper
import javax.inject.Inject
import kotlin.math.abs

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val playlistManager: MusicPlaylistManager,
) : AndroidViewModel(application) {
    //var state by mutableStateOf(AlbumDetailState())
    var state by savedStateHandle.saveable {
        mutableStateOf(AlbumDetailState())
    }

    init {
        val album = savedStateHandle.get<Album>("album")?.also {
            state = state.copy(album = it)
        }

        savedStateHandle.get<String>("albumId")?.let {
            L("albumId", it)
            if (!it.isNullOrBlank()) {
                getSongsFromAlbum(it)
            }
            if (album == null) {
                getAlbumInfo(it)
            }
        }

        viewModelScope.launch {
            playlistManager.downloadedSongFlow.collect {
                if(it != null) {
                    L("RefreshFromCache")
                    onEvent(AlbumDetailEvent.RefreshFromCache)
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
            is AlbumDetailEvent.OnSongSelected -> {
                // play the selected song and add the rest of the album to the queue
                playlistManager.updateTopSong(event.song)
                playlistManager.addToCurrentQueue(state.getSongList())
            }
            is AlbumDetailEvent.OnPlayAlbum -> {
                L("AlbumDetailViewModel.AlbumDetailEvent.OnPlayAlbum")
                playlistManager.updateCurrentSong(state.songs[0].song)
                playlistManager.addToCurrentQueueTop(state.getSongList())
            }
            AlbumDetailEvent.OnShareAlbum ->
                shareAlbum(state.album.id)
            AlbumDetailEvent.OnShuffleAlbum -> {
                val shuffled = state.getSongList().shuffled()
                playlistManager.addToCurrentQueueNext(shuffled)
                playlistManager.moveToSongInQueue(shuffled[0])
            }
            AlbumDetailEvent.OnFavouriteAlbum ->
                favouriteAlbum()

            AlbumDetailEvent.RefreshFromCache -> {
                L("AlbumDetailEvent.RefreshFromCache", state.album.id)
                if (!state.album.id.isNullOrBlank()) {
                    getSongsFromAlbum(albumId = state.album.id, fetchRemote = false)
                }
            }
        }
    }

    private fun favouriteAlbum(albumId: String = state.album.id) = viewModelScope.launch {
        playlistsRepository.likeAlbum(albumId, (state.album.flag != 1))
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh album
                            state = state.copy(
                                album = state.album.copy(flag = abs(state.album.flag - 1))
                            )
                        }
                    }
                    is Resource.Error -> state = state.copy(isLikeLoading = false)
                    is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
                }
            }
    }


    private fun getAlbumInfo(albumId: String, fetchRemote: Boolean = true) {
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
                                    songWrapperList.add(SongWrapper(
                                        song = song,
                                        isOffline = songsRepository.isSongAvailableOffline(song)
                                    ))
                                }
                                state = state.copy(songs = songWrapperList)
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
