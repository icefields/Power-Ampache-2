package luci.sixsixsix.powerampache2.presentation.screens.albums

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.NETWORK_REQUEST_LIMIT_DEBUG
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    private var fetchMoreJob: Job? = null
    var state by mutableStateOf(AlbumsState())
    private var isEndOfDataList: Boolean = false

    init {
        getAlbums()
        viewModelScope.launch {
            playlistManager.currentSearchQuery.collect { query ->
                L("AlbumsViewModel collect", query)
                onEvent(AlbumsEvent.OnSearchQueryChange(query))
            }
        }
    }

    fun onEvent(event: AlbumsEvent) {
        when (event) {
            is AlbumsEvent.Refresh -> {
                getAlbums(fetchRemote = true)
            }

            is AlbumsEvent.OnSearchQueryChange -> {
                if (event.query.isBlank() && state.searchQuery.isBlank()) {

                } else {
                    state = state.copy(searchQuery = event.query)
                    getAlbums()
                }
            }

            is AlbumsEvent.OnBottomListReached -> {
                if (!state.isFetchingMore && !isEndOfDataList) {
                    fetchMoreJob?.cancel()
                    fetchMoreJob = viewModelScope.launch {
                        delay(500)
                        L("AlbumsEvent.OnBottomListReached")
                        state = state.copy(isFetchingMore = true)
                        getAlbums(fetchRemote = true, offset = state.albums.size)
                    }
                }
            }
        }
    }

    private fun getAlbums(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        offset: Int = 0,
        limit: Int = NETWORK_REQUEST_LIMIT_DEBUG
    ) {
        viewModelScope.launch {
            repository
                .getAlbums(
                    fetchRemote = fetchRemote,
                    query = query,
                    offset = offset,
                    limit = limit
                )
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(albums = albums)
                                L("viewmodel.getAlbums size ${state.albums.size}")
                            }
                            isEndOfDataList = ( ((result.networkData?.size ?: 0) < limit) && offset > 0)
                        }

                        is Resource.Error -> {
                            // TODO set end of data list otherwise keeps fetching? do for other screens too
                            isEndOfDataList = true
                            state = state.copy(isFetchingMore = false, isLoading = false)
                            L("ERROR AlbumsViewModel ${result.exception}")
                        }

                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                            if (!result.isLoading) {
                                state = state.copy(isFetchingMore = false)
                            }
                        }
                    }
                }
        }
    }
}
