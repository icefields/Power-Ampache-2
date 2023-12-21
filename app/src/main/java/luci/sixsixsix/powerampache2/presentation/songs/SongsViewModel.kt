package luci.sixsixsix.powerampache2.presentation.songs

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
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection,
    private val repository: MusicRepository
) : ViewModel() {

    var state by mutableStateOf(SongsState())
    private var searchJob: Job? = null
    private var isEndOfDataReached: Boolean = false

    init {
        getSongs()

        // TODO original code
        // updateCurrentPlayerPosition()
    }

    fun onEvent(event: SongsEvent) {
        when(event) {
            is SongsEvent.Refresh -> {
                getSongs(fetchRemote = true)
            }
            is SongsEvent.OnSearchQueryChange -> {
                Log.d("aaaa", "SongsEvent.OnSearchQueryChange")
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1500L)
                    getSongs()
                }
            }
            is SongsEvent.OnBottomListReached -> {
                if (!state.isFetchingMore && !isEndOfDataReached) {
                    Log.d("aaaa", "SongsEvent.OnBottomListReached")
                    state = state.copy(isFetchingMore = true)
                    getSongs(fetchRemote = true, offset = state.songs.size)
                }
            }
        }
    }

    private fun getSongs(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        offset: Int = 0
    ) {
        Log.d("aaaa", "viewmodel.getSongs")
        viewModelScope.launch {
            repository
                .getSongs(fetchRemote, query, offset)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                state = state.copy(songs = songs)
                                Log.d("aaaa", "viewmodel.getSongs SONGS size${state.songs.size}")
                            }
                            isEndOfDataReached = ( result.networkData?.isEmpty() == true && offset > 0 ) ?: run { false }
                            Log.d("aaaa", "viewmodel.getSongs is bottom reached? $isEndOfDataReached ")
                        }

                        is Resource.Error -> {
                            state = state.copy(isFetchingMore = false)
                            Log.d("aaaa", "ERROR SongsViewModel.getSongs ${result.exception}")
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

    // TODO original code

    private val playbackState = musicServiceConnection.playbackState

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while(true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }
}
