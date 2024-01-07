package luci.sixsixsix.powerampache2.presentation.album_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.presentation.home.HomeScreenState
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val playlistManager: MusicPlaylistManager,
) : ViewModel() {
    //var state by mutableStateOf(AlbumDetailState())
    var state by savedStateHandle.saveable {
        mutableStateOf(AlbumDetailState())
    }

    init {
        val album = savedStateHandle.get<Album>("album")?.also {
            state = state.copy(album = it)
        }

        savedStateHandle.get<String>("albumId")?.let {
            getSongsFromAlbum(it)
            if (album == null) {
                getAlbumInfo(it)
            }
        }
    }

    fun onEvent(event: AlbumDetailEvent) {
        when (event) {
            is AlbumDetailEvent.Fetch -> {
                getSongsFromAlbum(albumId = event.albumId, fetchRemote = true)
            }

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
            AlbumDetailEvent.OnDownloadAlbum -> {}
            AlbumDetailEvent.OnShareAlbum -> {}
            AlbumDetailEvent.OnShuffleAlbum -> {
                val shuffled = state.songs.shuffled()
                playlistManager.addToCurrentQueueNext(shuffled)
                playlistManager.updateTopSong(shuffled[0])
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
                .getSongsFromAlbum(albumId)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                state = state.copy(songs = songs)
                                L("AlbumDetailViewModel.getSongsFromAlbum size", result.data?.size, "network", result.networkData?.size)
                            }
                        }

                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
