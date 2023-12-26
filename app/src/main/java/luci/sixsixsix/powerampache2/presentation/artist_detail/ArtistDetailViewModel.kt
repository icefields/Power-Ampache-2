package luci.sixsixsix.powerampache2.presentation.artist_detail

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
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val repository: AlbumsRepository
) : ViewModel() {

    var state by mutableStateOf(ArtistDetailState())

    init {
        savedStateHandle.get<String>("artistId")?.let {
            getAlbumsFromArtist(it)
        }
    }

    fun onEvent(event: ArtistDetailEvent) {
        when(event) {
            is ArtistDetailEvent.Refresh -> {
            }
            is ArtistDetailEvent.Fetch -> {
                getAlbumsFromArtist(artistId = event.albumId ,fetchRemote = true)
            }
        }
    }

    private fun getAlbumsFromArtist(artistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            repository
                .getAlbumsFromArtist(artistId)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.networkData?.let { albums ->
                                state = state.copy(albums = albums)
                                L("viewmodel.getAlbumsFromArtist size ${result.data?.size} network: ${result.networkData?.size}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
