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

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.settings.SortMode
import luci.sixsixsix.powerampache2.domain.usecase.UserFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.playlists.PlaylistFlow
import luci.sixsixsix.powerampache2.domain.usecase.playlists.SongsFromPlaylistUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ChangeSortModeUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.LocalSettingsFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ToggleGlobalShuffleUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.IsSongAvailableOfflineUseCase
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongWrapper
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val savedStateHandle: SavedStateHandle,
    private val toggleGlobalShuffle: ToggleGlobalShuffleUseCase,
    localSettingsFlowUseCase: LocalSettingsFlowUseCase,
    playlistFlow: PlaylistFlow,
    private val isSongAvailableOfflineUseCase: IsSongAvailableOfflineUseCase,
    private val songsRepository: SongsRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val songsFromPlaylistUseCase: SongsFromPlaylistUseCase,
    private val changeSortModeUseCase: ChangeSortModeUseCase,
    private val playlistManager: MusicPlaylistManager,
    userFlowUseCase: UserFlowUseCase
) : ViewModel() {
    var state by mutableStateOf(PlaylistDetailState())
    var editState by mutableStateOf(PlaylistEditState(listOf()))

//    val isNextcloudState = musicRepository.serverInfoStateFlow.filterNotNull().map { serverInfo ->
//        serverInfo.isNextcloud
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val playlistStateFlow: StateFlow<Playlist> =
        savedStateHandle.getStateFlow<Playlist?>("playlist", null)
            .filterNotNull()
            .map { playlist ->
                fetchPlaylistSongs(playlist)
                playlist
            }.combine(userFlowUseCase().filterNotNull().distinctUntilChanged()) { playlist, user ->
                state = state.copy(
                    isNotStatPlaylist = PlaylistDetailState.isNotStatPlaylist(playlist),
                    isGeneratedOrSmartPlaylist = PlaylistDetailState.isGeneratedOrSmartPlaylist(playlist),
                    isUserOwner = user.username.lowercase() == playlist.owner?.lowercase()
                )
                playlist
            }.flatMapConcat { playlist ->
                if (PlaylistDetailState.isNotStatPlaylist(playlist)) {
                    playlistFlow(playlist.id)
                } else {
                    flow { emit(playlist) }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Playlist.empty())

    init {
        viewModelScope.launch {
            localSettingsFlowUseCase().collectLatest { settings ->
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

    private fun fetchPlaylistSongs(playlist: Playlist) {
        when (playlist) {
            is HighestPlaylist -> getHighestSongs(fetchRemote = true)
            is RecentPlaylist -> getRecentSongs(fetchRemote = true)
            is FlaggedPlaylist -> getFlaggedSongs(fetchRemote = true)
            is FrequentPlaylist -> getFrequentSongs(fetchRemote = true)
            else -> getSongsFromPlaylist(playlistId = playlist, fetchRemote = true)
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
            PlaylistDetailEvent.OnToggleSort -> viewModelScope.launch {
                if (state.isNotStatPlaylist) {
                    // if not a stat playlist change global sort
                    changeSortModeUseCase( if (state.sortMode == SortMode.ASC) SortMode.DESC else SortMode.ASC )
                } else {
                    // otherwise just reverse
                    state = state.copy(songs = state.songs.reversed())
                }
            }
            PlaylistDetailEvent.OnShufflePlaylistToggle -> viewModelScope.launch {
                try {
                    state = state.copy(isGlobalShuffleOn = toggleGlobalShuffle())
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
        }
    }

    fun onEditEvent(event: PlaylistDetailsEditEvent) {
        when(event) {
            PlaylistDetailsEditEvent.OnDeleteSelectedSongs -> {
                val newList = state.getSongList().toMutableList().apply {
                    removeAll(editState.selectedSongs)
                }.toList()
                editPlaylist(newList = newList)
            }
            is PlaylistDetailsEditEvent.OnMoveDownSong -> {
                val newList = state.getSongList().toMutableList().apply {
                    indexOf(event.song).takeIf { it < (size - 1) }?.let { i ->
                        remove(event.song)
                        add(i+1, event.song)
                    }
                }.toList()
                editPlaylist(newList = newList)
            }
            is PlaylistDetailsEditEvent.OnMoveUpSong -> {
                val newList = state.getSongList().toMutableList().apply {
                    indexOf(event.song).takeIf { it > 0 }?.let { i ->
                        remove(event.song)
                        add(i-1, event.song)
                    }
                }.toList()
                editPlaylist(newList = newList)
            }
            is PlaylistDetailsEditEvent.OnRemoveSong ->
                removeSongFromPlaylist(playlist = playlistStateFlow.value, songId = event.song.mediaId)
            PlaylistDetailsEditEvent.OnRemoveSongDismiss -> { }
            is PlaylistDetailsEditEvent.OnSongSelected -> {
                editState = editState.copy(selectedSongs = editState.selectedSongs.toMutableList().apply {
                    if (event.isSelected) { add(event.song) } else { remove(event.song) }
                })
            }
            PlaylistDetailsEditEvent.OnConfirmEdit -> {

            }
        }
    }

    fun isEditSongSelected(song: Song): Boolean = editState.selectedSongs.contains(song)

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


    private fun getSongsFromPlaylist(playlistId: Playlist, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            songsFromPlaylistUseCase(playlistId, fetchRemote)
                .collect { result ->
                    when(result) {
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

    private fun editPlaylist(
        playlist: Playlist = playlistStateFlow.value,
        newList: List<Song> = state.getSongList()
    ) = viewModelScope.launch {
        playlistsRepository
            .editPlaylist(
                playlistId = playlist.id,
                playlistName = playlist.name,
                items = newList,
                owner = playlist.owner,
                playlistType = playlist.type ?: PlaylistType.private
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // fetch songs without network request
                            getSongsFromPlaylist(playlist, false)
                        }
                    }
                    is Resource.Error ->
                        state = state.copy(isPlaylistRemoveLoading = false)
                    is Resource.Loading ->
                        state = state.copy(isPlaylistRemoveLoading = result.isLoading)
                }
            }
    }

    private fun removeSongFromPlaylist(playlist: Playlist, songId: String) = viewModelScope.launch {
        playlistsRepository
            .removeSongFromPlaylist(playlistId = playlist.id, songId = songId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            getSongsFromPlaylist(playlist, true)
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
                            songWrapperList.add(
                                SongWrapper(song = song,
                                isOffline = isSongAvailableOfflineUseCase(song))
                            )
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


    private fun getFlaggedSongs(fetchRemote: Boolean = true) {
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
                                            isOffline = isSongAvailableOfflineUseCase(song)
                                        )
                                    )
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
                                            isOffline = isSongAvailableOfflineUseCase(song = song)
                                        )
                                    )
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
                                            isOffline = isSongAvailableOfflineUseCase(song)
                                        )
                                    )
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
