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
package luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.presentation.common.SongWrapper
import luci.sixsixsix.powerampache2.presentation.main.MainState
import javax.inject.Inject

@HiltViewModel
@OptIn(SavedStateHandleSaveableApi::class)
class PlaylistDetailViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val songsRepository: SongsRepository,
    private val musicRepository: MusicRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application = application) {
    var state by mutableStateOf(PlaylistDetailState())
    //var state by savedStateHandle.saveable { mutableStateOf(PlaylistDetailState()) }

    init {
        savedStateHandle.get<Playlist>("playlist")?.let { playlist ->
            state = state.copy(playlist = playlist)
            onEvent(PlaylistDetailEvent.Fetch(playlist))

            musicRepository.userLiveData.observeForever {
                it?.let { user ->
                    state = state.copy(isUserOwner = user.username == playlist.owner)
                }
            }
        }
    }

    fun onEvent(event: PlaylistDetailEvent) {
        when(event) {
            is PlaylistDetailEvent.Refresh -> {
            }
            is PlaylistDetailEvent.Fetch -> {
                when (event.playlist) {
                    is HighestPlaylist -> getHighestSongs(fetchRemote = true)
                    is RecentPlaylist -> getRecentSongs(fetchRemote = true)
                    is FlaggedPlaylist -> getFlaggedSongs(fetchRemote = true)
                    is FrequentPlaylist -> getFrequentSongs(fetchRemote = true)
                    else -> getSongsFromPlaylist(playlistId = event.playlist.id, fetchRemote = true)
                }
            }
            is PlaylistDetailEvent.OnSongSelected -> {
                playlistManager.updateTopSong(state.songs[0].song)
                playlistManager.addToCurrentQueue(state.getSongList())
            }
            PlaylistDetailEvent.OnPlayPlaylist -> if (state.songs.isNotEmpty()) {
                playlistManager.updateCurrentSong(state.songs[0].song)
                playlistManager.addToCurrentQueueTop(state.getSongList())
            }
            PlaylistDetailEvent.OnSharePlaylist ->
                sharePlaylist(playlistId = state.playlist.id)
            PlaylistDetailEvent.OnShufflePlaylist -> if (state.songs.isNotEmpty()) {
                val shuffled = state.getSongList().shuffled()
                playlistManager.replaceCurrentQueue(shuffled)
                playlistManager.moveToSongInQueue(shuffled[0])
            }
            is PlaylistDetailEvent.OnRemoveSong ->
                removeSongFromPlaylist(playlistId = state.playlist.id, songId = event.song.mediaId)
            PlaylistDetailEvent.OnRemoveSongDismiss -> { }
//                viewModelScope.launch {
//                // TODO HACK FORCE refresh of list
//                val songs = state.songs.toMutableList()
//                state = state.copy(songs = listOf())
//                delay(100)
//                state = state.copy(songs = songs)
//            }

        }
    }

    private fun sharePlaylist(playlistId: String) = viewModelScope.launch {
        playlistsRepository.getPlaylistShareLink(playlistId).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    application.shareLink(it)
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }
    }

    fun generateBackgrounds(): Pair<String, String> =
        if (state.getSongList().isNotEmpty()) {
            val urls = state.getSongList().map { it.imageUrl }.toSet().shuffled()
            // try to have different images, get first and last
            val randomBackgroundTop = urls[0]
            val randomBackgroundBottom = urls[urls.size - 1]
            Pair(randomBackgroundTop, randomBackgroundBottom)
        } else Pair("", "")


    private fun getSongsFromPlaylist(playlistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            songsRepository
                .getSongsFromPlaylist(playlistId)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(
                                        SongWrapper(
                                        song = song,
                                        isOffline = songsRepository.isSongAvailableOffline(song)
                                    ))
                                }
                                state = state.copy(songs = songWrapperList)
                                L("PlaylistDetailViewModel.getSongsFromPlaylist size ${result.data?.size} network: ${result.networkData?.size}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }

    private fun removeSongFromPlaylist(playlistId: String, songId: String) = viewModelScope.launch {
        playlistsRepository
            .removeSongFromPlaylist(playlistId = playlistId, songId = songId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            getSongsFromPlaylist(playlistId, true)
                            // playlistManager.updateErrorMessage("Song removed from playlist")
                        }
                    }
                    is Resource.Error ->
                        state = state.copy(isPlaylistRemoveLoading = false)
                    is Resource.Loading ->
                        state = state.copy(isPlaylistRemoveLoading = result.isLoading)
                }
            }
    }

    private fun getRecentSongs(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            songsRepository
                .getRecentSongs()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(
                                        SongWrapper(
                                            song = song,
                                            isOffline = songsRepository.isSongAvailableOffline(song)
                                        ))
                                }
                                state = state.copy(songs = songWrapperList)
                                L("PlaylistDetailViewModel.getRecentSongs size ${state.songs.size}")
                            }
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR PlaylistDetailViewModel.getRecentSongs ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getFlaggedSongs(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            songsRepository
                .getFlaggedSongs()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(
                                        SongWrapper(
                                            song = song,
                                            isOffline = songsRepository.isSongAvailableOffline(song)
                                        ))
                                }
                                state = state.copy(songs = songWrapperList)
                                L("PlaylistDetailViewModel.getFlaggedSongs size ${state.songs.size}")
                            }
                            L( "PlaylistDetailViewModel.getFlaggedSongs size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR PlaylistDetailViewModel.getFlaggedSongs ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getFrequentSongs(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            songsRepository
                .getFrequentSongs()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(
                                        SongWrapper(
                                            song = song,
                                            isOffline = songsRepository.isSongAvailableOffline(song = song)
                                        ))
                                }
                                state = state.copy(songs = songWrapperList)
                                L("PlaylistDetailViewModel.getFrequentSongs size ${state.songs.size}")
                            }
                            L( "PlaylistDetailViewModel.getFrequentSongs size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR PlaylistDetailViewModel.getFrequentSongs ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getHighestSongs(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            songsRepository
                .getHighestSongs()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(
                                        SongWrapper(
                                            song = song,
                                            isOffline = songsRepository.isSongAvailableOffline(song)
                                        ))
                                }
                                state = state.copy(songs = songWrapperList)
                                L("PlaylistDetailViewModel.getHighestSongs size ${state.songs.size}")
                            }
                            L( "PlaylistDetailViewModel.getHighestSongs size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR PlaylistDetailViewModel.getHighestSongs ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}
