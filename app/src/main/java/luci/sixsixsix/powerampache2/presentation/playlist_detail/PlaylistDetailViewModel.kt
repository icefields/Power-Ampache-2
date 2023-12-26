package luci.sixsixsix.powerampache2.presentation.playlist_detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val repository: SongsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {

    var state by mutableStateOf(PlaylistDetailState())

    init {
        savedStateHandle.get<String>("playlistId")?.let {
            getSongsFromPlaylist(it)
        }
    }

    fun onEvent(event: PlaylistDetailEvent) {
        when(event) {
            is PlaylistDetailEvent.Refresh -> {
            }
            is PlaylistDetailEvent.Fetch -> {
                getSongsFromPlaylist(playlistId = event.playlistId ,fetchRemote = true)
            }

            is PlaylistDetailEvent.OnSongSelected -> { playlistManager.updateCurrentSong(event.song) }
        }
    }

    private fun getSongsFromPlaylist(playlistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            repository
                .getSongsFromPlaylist(playlistId)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            // USE NETWORK DATA FOR THIS CALL
                            result.networkData?.let { songs ->
                                state = state.copy(songs = songs)
                                L("viewmodel.getSongsFromPlaylist size ${result.data?.size} network: ${result.networkData?.size}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
