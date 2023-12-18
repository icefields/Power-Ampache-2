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
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1500L)
                    getSongs()
                }
            }
        }
    }

    private fun getSongs(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true
    ) {
        viewModelScope.launch {
            repository
                .getSongs(fetchRemote, query)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                Log.d("aaaa", "${songs[0].title}")
                                state = state.copy(songs = songs)
                            }
                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
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
