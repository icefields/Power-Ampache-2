package luci.sixsixsix.powerampache2.presentation.songs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.exoplayer.MusicService
import luci.sixsixsix.powerampache2.exoplayer.MusicServiceConnection
import luci.sixsixsix.powerampache2.exoplayer.currentPlaybackPosition
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection,
    private val repository: SongsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    var state by mutableStateOf(SongsState())

    init {
        getSongs()
        viewModelScope.launch {
            playlistManager.currentSearchQuery.collect { query ->
                L("AlbumsViewModel collect ${query}")
                onEvent(SongsEvent.OnSearchQueryChange(query))
            }
        }
        // TODO original code
        // updateCurrentPlayerPosition()
    }

    fun onEvent(event: SongsEvent) {
        when(event) {
            is SongsEvent.Refresh -> {
                getSongs(fetchRemote = true, refresh = true)
            }
            is SongsEvent.OnSearchQueryChange -> {
                L("SongsEvent.OnSearchQueryChange")
                // force refresh when deleting the search query
                state = state.copy(searchQuery = event.query)
                getSongs(refresh = event.query.isNullOrEmpty())
            }

            is SongsEvent.OnSongSelected -> {
                L("SongsEvent.OnSongSelected", event.song)
                playlistManager.updateCurrentSong(event.song) }
        }
    }

    private fun getSongs(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        refresh: Boolean = false,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            repository
                .getSongs(fetchRemote, query, offset)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val oldSongs = if(!refresh) {
                                    state.songs
                                } else {
                                    L("viewmodel.getSongs REFRESH")
                                    listOf()
                                }

                                if (query.isBlank()) {
                                    val hashSet = LinkedHashSet<Song>(oldSongs).apply {
                                        addAll(songs)
                                    }
                                    state = state.copy(songs = hashSet.toList())
                                } else {
                                    state = state.copy(songs = ArrayList(songs))
                                }
                                L("viewmodel.getSongs SONGS size at the end", state.songs.size)
                            }
                        }

                        is Resource.Error -> {
                            state = state.copy(isFetchingMore = false, isLoading = false)
                            L("ERROR SongsViewModel.getSongs ${result.exception}")
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
