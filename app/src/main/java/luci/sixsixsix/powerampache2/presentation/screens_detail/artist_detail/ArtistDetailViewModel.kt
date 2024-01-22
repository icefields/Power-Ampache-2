package luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail

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
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.models.Artist
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val repository: AlbumsRepository,
    private val artistsRepository: ArtistsRepository,
) : ViewModel() {

    var state by mutableStateOf(ArtistDetailState())

    init {
        savedStateHandle.get<String>("artistId")?.let {id ->
            getAlbumsFromArtist(id)

            savedStateHandle.get<Artist>("artist")?.let { artist ->
                state = state.copy(artist = artist)
            } ?: getArtist(id)
        }
    }

    fun onEvent(event: ArtistDetailEvent) {
        when(event) {
            is ArtistDetailEvent.Refresh -> {
                state.artist?.let {
                    getAlbumsFromArtist(artistId = it.id, fetchRemote = true)
                }
            }
            is ArtistDetailEvent.Fetch -> {
                getAlbumsFromArtist(artistId = event.albumId, fetchRemote = true)
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
                            result.data?.let { albums ->
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

    private fun getArtist(artistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            artistsRepository
                .getArtist(artistId)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            // TODO why am I using network data here? please comment
                            result.networkData?.let { artist ->
                                state = state.copy(artist = artist)
                                L("viewmodel.getArtist size ${result.data} network: ${result.networkData}")
                            }
                        }

                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
