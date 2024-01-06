package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

@HiltViewModel
class AddToPlaylistOrQueueDialogViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val repository: MusicRepository
) : ViewModel() {
    var state by mutableStateOf(AddToPlaylistOrQueueDialogState())
    private var isEndOfDataReached: Boolean = false

    init {
        getPlaylists()
    }

    fun onEvent(event: AddToPlaylistOrQueueDialogEvent) {
        when (event) {
            is AddToPlaylistOrQueueDialogEvent.AddToPlaylist -> TODO()
        }
    }

    private fun getPlaylists(
        fetchRemote: Boolean = true,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            repository
                .getPlaylists(fetchRemote, "", offset)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { playlists ->
                                state = state.copy(playlists = playlists)
                                L("viewmodel.getPlaylists size", state.playlists.size)
                            }
                            isEndOfDataReached = (result.networkData?.isEmpty() == true && offset > 0)
                            L("viewmodel.getPlaylists is bottom reached?", isEndOfDataReached, "offset", offset, "size of new array", result.networkData?.size)
                        }
                        is Resource.Error -> {
                            L("ERROR PlaylistsViewModel", result.exception)
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}

data class AddToPlaylistOrQueueDialogState (
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
)

sealed class AddToPlaylistOrQueueDialogEvent {
    data class AddToPlaylist(val song: Song, val playlistId: String): AddToPlaylistOrQueueDialogEvent()
}
