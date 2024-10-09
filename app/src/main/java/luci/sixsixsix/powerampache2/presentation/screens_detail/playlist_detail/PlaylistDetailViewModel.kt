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
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import luci.sixsixsix.powerampache2.domain.models.SortMode
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.presentation.common.SongWrapper
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistDetailViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val songsRepository: SongsRepository,
    private val musicRepository: MusicRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val settingsRepository: SettingsRepository,
    private val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application = application) {
    var state by mutableStateOf(PlaylistDetailState())


    val playlistStateFlow: StateFlow<Playlist> =
        savedStateHandle.getStateFlow<Playlist?>("playlist", null)
            .filterNotNull()
            .map { playlist ->
                fetchPlaylistSongs(playlist)
                playlist
            }.combine(musicRepository.userLiveData.filterNotNull().distinctUntilChanged()) { playlist, user ->
                state = state.copy(
                    isNotStatPlaylist = PlaylistDetailState.isNotStatPlaylist(playlist),
                    isGeneratedOrSmartPlaylist = PlaylistDetailState.isGeneratedOrSmartPlaylist(playlist),
                    isUserOwner = user.username == playlist.owner
                )
                playlist
            }.flatMapConcat { playlist ->
                if (PlaylistDetailState.isNotStatPlaylist(playlist)) {
                    playlistsRepository.getPlaylist(playlist.id)
                } else {
                    flow { emit(playlist) }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Playlist.empty())

    init {
        viewModelScope.launch {
            settingsRepository.settingsLiveData.asFlow().collectLatest {
                it?.let { settings ->
                    if (settings.playlistSongsSorting != state.sortMode) {
                        L("found a different sort mode", settings.playlistSongsSorting)
                        // when getting a different sort mode, invert the list
                        state = state.copy(sortMode = settings.playlistSongsSorting, songs = state.songs.reversed())
                    }

                    if (settings.isGlobalShuffleEnabled != state.isGlobalShuffleOn) {
                        state = state.copy(isGlobalShuffleOn = settings.isGlobalShuffleEnabled)
                    }
                }
            }
        }
    }

    private fun fetchPlaylistSongs(playlist: Playlist) {
        when (playlist) {
            is HighestPlaylist -> getHighestSongs(fetchRemote = true)
            is RecentPlaylist -> getRecentSongs(fetchRemote = true)
            is FlaggedPlaylist -> getFlaggedSongs(fetchRemote = true)
            is FrequentPlaylist -> getFrequentSongs(fetchRemote = true)
            else -> getSongsFromPlaylist(playlistId = playlist.id, fetchRemote = true)
        }
    }

    fun onEvent(event: PlaylistDetailEvent) {
        when(event) {
            PlaylistDetailEvent.OnPlaylistNotReadyDownload -> {
                Toast.makeText(application,
                    R.string.warning_playlist_download,
                    Toast.LENGTH_LONG
                ).show()
            }
            is PlaylistDetailEvent.Fetch ->
                fetchPlaylistSongs(event.playlist)
            PlaylistDetailEvent.OnSharePlaylist ->
                sharePlaylist(playlistId = playlistStateFlow.value.id)
            is PlaylistDetailEvent.OnRemoveSong ->
                removeSongFromPlaylist(playlistId = playlistStateFlow.value.id, songId = event.song.mediaId)
            PlaylistDetailEvent.OnToggleSort -> viewModelScope.launch {
                if (state.isNotStatPlaylist) {
                    // if not a stat playlist change global sort
                    settingsRepository.changeSortMode( if (state.sortMode == SortMode.ASC) SortMode.DESC else SortMode.ASC )
                } else {
                    // otherwise just reverse
                    state = state.copy(songs = state.songs.reversed())
                }
            }
            PlaylistDetailEvent.OnShufflePlaylistToggle -> viewModelScope.launch {
                try {
                    state = state.copy(isGlobalShuffleOn = settingsRepository.toggleGlobalShuffle())
                } catch (e: Exception) {
                    playlistManager.updateErrorLogMessage(e.stackTraceToString())
                }
            }
            is PlaylistDetailEvent.OnRatePlaylist -> viewModelScope.launch {
                ratePlaylist(event.playlist, event.rating)
            }
            PlaylistDetailEvent.OnLikePlaylist -> likePlaylist()

            is PlaylistDetailEvent.Refresh -> { }
            is PlaylistDetailEvent.OnSongSelected -> { }
            PlaylistDetailEvent.OnPlayPlaylist -> { }
            PlaylistDetailEvent.OnShufflePlaylist -> { }
            PlaylistDetailEvent.OnRemoveSongDismiss -> { }
        }
    }

    private fun ratePlaylist(playlist: Playlist, rate: Int) = viewModelScope.launch {
        playlistsRepository.ratePlaylist(playlist.id, rate).collect { result ->
            when (result) {
                is Resource.Success -> { }
                is Resource.Error -> state = state.copy(isLoading = false)
                is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun likePlaylist(playlistId: String = playlistStateFlow.value.id) = viewModelScope.launch {
        playlistsRepository.likePlaylist(playlistId, (playlistStateFlow.value.flag != 1))
            .collect { result ->
                when (result) {
                    is Resource.Success -> { }
                    is Resource.Error -> state = state.copy(isLikeLoading = false)
                    is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
                }
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
            playlistsRepository
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
                                state = state.copy(
                                    songs = songWrapperList.apply {
                                        if (state.sortMode == SortMode.DESC) { reverse() } }
                                )
                            }
                        }
                        is Resource.Error ->
                            state = state.copy(isLoading = false)
                        is Resource.Loading ->
                            state = state.copy(isLoading = result.isLoading)
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
                        }
                    }
                    is Resource.Error ->
                        state = state.copy(isPlaylistRemoveLoading = false)
                    is Resource.Loading ->
                        state = state.copy(isPlaylistRemoveLoading = result.isLoading)
                }
            }
    }

    private fun getRecentSongs(fetchRemote: Boolean = true) = viewModelScope.launch {
        songsRepository.getRecentSongs().collect { result ->
            when(result) {
                is Resource.Success -> {
                    result.data?.let { songs ->
                        val songWrapperList = mutableListOf<SongWrapper>()
                        songs.forEach { song ->
                            songWrapperList.add(SongWrapper(song = song,
                                isOffline = songsRepository.isSongAvailableOffline(song)))
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
