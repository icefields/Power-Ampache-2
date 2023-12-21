package luci.sixsixsix.powerampache2.presentation.albums

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
class AlbumsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    var state by mutableStateOf(AlbumsState())
    private var searchJob: Job? = null
    private var isEndOfDataList: Boolean = false

    init {
        getAlbums()
    }

    fun onEvent(event: AlbumsEvent) {
        when(event) {
            is AlbumsEvent.Refresh -> {
                getAlbums(fetchRemote = true)
            }
            is AlbumsEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1500L)
                    getAlbums()
                }
            }
            is AlbumsEvent.OnBottomListReached -> {
                if (!state.isFetchingMore && !isEndOfDataList) {
                    Log.d("aaaa", "AlbumsEvent.OnBottomListReached")
                    state = state.copy(isFetchingMore = true)
                    getAlbums(fetchRemote = true, offset = state.albums.size)
                }
            }
        }
    }

    private fun getAlbums(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            repository
                .getAlbums(fetchRemote, query, offset)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(albums = albums)
                                Log.d("aaaa", "viewmodel.getAlbums size ${state.albums.size}")
                            }
                            isEndOfDataList = ( result.networkData?.isEmpty() == true && offset > 0 )
                            Log.d("aaaa", "viewmodel.getAlbums is bottom reached? $isEndOfDataList offset $offset, size of new array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isFetchingMore = false, isLoading = false)
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
