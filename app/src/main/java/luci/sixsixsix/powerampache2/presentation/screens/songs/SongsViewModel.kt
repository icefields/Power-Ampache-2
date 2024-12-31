package luci.sixsixsix.powerampache2.presentation.screens.songs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongWrapper
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val repository: SongsRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    var state by mutableStateOf(SongsState())

    val offlineModeStateFlow = settingsRepository.offlineModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        L("SongsListScreen")
        getSongs()
        viewModelScope.launch {
            offlineModeStateFlow.collectLatest {
                getSongs()
            }
        }
    }

    fun onEvent(event: SongsEvent) {
        when(event) {
            is SongsEvent.Refresh -> {
                L("Refresh")
                getSongs(fetchRemote = true, refresh = true)
            }
            is SongsEvent.OnSearchQueryChange -> {
                L("SongsEvent.OnSearchQueryChange")
                // force refresh when deleting the search query
                // start a search only if the new query is different from the previous
                if (event.query.isBlank() && state.searchQuery.isBlank()) {

                } else {
                    state = state.copy(searchQuery = event.query)
                    getSongs(refresh = event.query.isNullOrEmpty())
                }
            }

            is SongsEvent.OnSongSelected -> {
                L("SongsEvent.OnSongSelected", event.song)
                //playlistManager.addToCurrentQueueUpdateTopSong(event.song, state.getSongList())
            }
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
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(
                                        SongWrapper(
                                        song = song,
                                        isOffline = repository.isSongAvailableOffline(song)
                                    )
                                    )
                                }
                                state = state.copy(songs = songWrapperList)
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
}
