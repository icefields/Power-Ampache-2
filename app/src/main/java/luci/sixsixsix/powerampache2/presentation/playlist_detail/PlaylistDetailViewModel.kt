package luci.sixsixsix.powerampache2.presentation.playlist_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val songsRepository: SongsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {

    var state by mutableStateOf(PlaylistDetailState())

    init {
        savedStateHandle.get<Playlist>("playlist")?.let { playlist ->
            onEvent(PlaylistDetailEvent.Fetch(playlist))
        }
    }

    /*
     is AlbumDetailEvent.OnSongSelected -> {
                // play the selected song and add the rest of the album to the queue
                playlistManager.updateTopSong(event.song)
            }

            is AlbumDetailEvent.OnAddAlbumToQueue -> playlistManager.addToCurrentQueue(state.songs)

            is AlbumDetailEvent.OnPlayAlbum -> {
                L("AlbumDetailViewModel.AlbumDetailEvent.OnPlayAlbum")
                playlistManager.updateCurrentSong(state.songs[0])
                playlistManager.addToCurrentQueueTop(state.songs)
            }
     */

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

            is PlaylistDetailEvent.OnSongSelected -> playlistManager.updateTopSong(event.song)
            PlaylistDetailEvent.OnDownloadPlaylist -> TODO()
            PlaylistDetailEvent.OnPlayPlaylist -> {
                L("OnPlayPlaylist")
                playlistManager.updateCurrentSong(state.songs[0])
                playlistManager.addToCurrentQueueTop(state.songs)
            }
            PlaylistDetailEvent.OnSharePlaylist -> TODO()
            PlaylistDetailEvent.OnShufflePlaylist -> TODO()
        }
    }

    fun generateBackgrounds(): Pair<String, String> =
        if (state.songs.isNotEmpty()) {
            val urls = state.songs.map { it.imageUrl }.toSet().shuffled()
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
                            // USE NETWORK DATA FOR THIS CALL
                            result.networkData?.let { songs ->
                                state = state.copy(songs = songs)
                                L("PlaylistDetailViewModel.getSongsFromPlaylist size ${result.data?.size} network: ${result.networkData?.size}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
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
                                state = state.copy(songs = songs)
                                L("PlaylistDetailViewModel.getRecentSongs size ${state.songs.size}")
                            }
                            L( "PlaylistDetailViewModel.getRecentSongs size of network array ${result.networkData?.size}")
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
                                state = state.copy(songs = songs)
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
                                state = state.copy(songs = songs)
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
                                state = state.copy(songs = songs)
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
