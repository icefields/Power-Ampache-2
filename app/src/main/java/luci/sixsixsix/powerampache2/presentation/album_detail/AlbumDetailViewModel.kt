package luci.sixsixsix.powerampache2.presentation.album_detail

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
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import luci.sixsixsix.powerampache2.presentation.playlist_detail.PlaylistDetailEvent
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val repository: MusicRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {

    var state by mutableStateOf(AlbumDetailState())

    init {
        savedStateHandle.get<String>("albumId")?.let {
            getSongsFromAlbum(it)
        }
    }

    fun onEvent(event: AlbumDetailEvent) {
        when(event) {
            is AlbumDetailEvent.Refresh -> {
            }
            is AlbumDetailEvent.Fetch -> {
                getSongsFromAlbum(albumId = event.albumId ,fetchRemote = true)
            }
            is AlbumDetailEvent.OnSongSelected -> {
                playlistManager.updateCurrentSong(event.song)
            }
        }
    }

    private fun getSongsFromAlbum(albumId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            repository
                .getSongsFromAlbum(albumId)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            // USE NETWORK DATA FOR THIS CALL (why???? )
                            result.data?.let { songs ->
                                state = state.copy(songs = songs)
                                L("viewmodel.getSongsFromAlbum size ${result.data?.size} network: ${result.networkData?.size}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
