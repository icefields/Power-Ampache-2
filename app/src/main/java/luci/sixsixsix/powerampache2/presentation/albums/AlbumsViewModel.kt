package luci.sixsixsix.powerampache2.presentation.albums

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.exoplayer.MusicService
import luci.sixsixsix.powerampache2.exoplayer.MusicServiceConnection
import luci.sixsixsix.powerampache2.exoplayer.currentPlaybackPosition
import luci.sixsixsix.powerampache2.presentation.songs.SongsEvent
import luci.sixsixsix.powerampache2.presentation.songs.SongsState
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {
    var state by mutableStateOf(AlbumsState())
    private var searchJob: Job? = null

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
        }
    }

    private fun getAlbums(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true
    ) {
        viewModelScope.launch {
            repository
                .getAlbums(fetchRemote, query)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                Log.d("aaaa", "${albums.size}")
                                state = state.copy(albums = albums)
                            }
                        }
                        is Resource.Error -> {
                            Log.d("aaaa", "ERROR AlbumsViewModel ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}
