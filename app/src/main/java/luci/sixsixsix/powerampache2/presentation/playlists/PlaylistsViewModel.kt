package luci.sixsixsix.powerampache2.presentation.playlists

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
import javax.inject.Inject


@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    var state by mutableStateOf(PlaylistsState())
    private var searchJob: Job? = null
    private var isEndOfDataReached: Boolean = false

    init {
        getPlaylists()
    }

    fun onEvent(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.Refresh -> {
                getPlaylists(fetchRemote = true)
            }
            is PlaylistEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1500L)
                    getPlaylists()
                }
            }
            is PlaylistEvent.OnBottomListReached -> {
                if (!state.isFetchingMore && !isEndOfDataReached) {
                    Log.d("aaaa", "PlaylistEvent.OnBottomListReached")
                    state = state.copy(isFetchingMore = true)
                    getPlaylists(fetchRemote = true, offset = state.playlists.size)
                }
            }
        }
    }

    private fun getPlaylists(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            repository
                .getPlaylists(fetchRemote, query, offset)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { playlists ->
                                state = state.copy(playlists = playlists)
                                Log.d("aaaa", "viewmodel.getPlaylists size ${state.playlists.size}")
                            }
                            isEndOfDataReached = ( result.networkData?.isEmpty() == true && offset > 0 ) ?: run { false }
                            Log.d("aaaa", "viewmodel.getPlaylists is bottom reached? $isEndOfDataReached  offset $offset size of new array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isFetchingMore = false)
                            Log.d("aaaa", "ERROR PlaylistsViewModel ${result.exception}")
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
