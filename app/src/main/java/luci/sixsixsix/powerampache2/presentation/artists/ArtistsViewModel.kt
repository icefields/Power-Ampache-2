package luci.sixsixsix.powerampache2.presentation.artists

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistEvent
import luci.sixsixsix.powerampache2.presentation.songs.SongsEvent
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    var state by mutableStateOf(ArtistsState())
    private var searchJob: Job? = null
    private var isBottomReached: Boolean = false

    init {
        getArtists()
    }

    fun onEvent(event: ArtistEvent) {
        when(event) {
            is ArtistEvent.Refresh -> {
                getArtists(fetchRemote = true)
            }
            is ArtistEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1500L)
                    getArtists()
                }
            }
            is ArtistEvent.OnBottomListReached -> {
                if (!state.isFetchingMore && !isBottomReached) {
                    Log.d("aaaa", "ArtistEvent.OnBottomListReached")
                    state = state.copy(isFetchingMore = true)
                    getArtists(fetchRemote = true, offset = state.artists.size)
                }
            }
        }
    }

    private fun getArtists(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            repository
                .getArtists(fetchRemote, query, offset)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { artists ->
                                state = state.copy(artists = artists)
                                Log.d("aaaa", "viewmodel.getArtists size ${state.artists.size}")
                            }
                            isBottomReached = ( result.networkData?.isEmpty() == true && offset > 0 )
                            Log.d("aaaa", "viewmodel.getArtists is bottom reached? $isBottomReached size of new array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isFetchingMore = false)
                            Log.d("aaaa", "ERROR AlbumsViewModel ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                            if(!result.isLoading) {
                                state = state.copy(isFetchingMore = false)
                            }
                        }
                    }
                }
        }
    }
}
