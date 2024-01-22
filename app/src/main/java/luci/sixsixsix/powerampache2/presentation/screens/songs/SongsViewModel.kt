package luci.sixsixsix.powerampache2.presentation.screens.songs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val repository: SongsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    var state by mutableStateOf(SongsState())

    init {
        L("SongsListScreen")
        getSongs()
        viewModelScope.launch {
            playlistManager.currentSearchQuery.collect { query ->
                L(query)
                onEvent(SongsEvent.OnSearchQueryChange(query))
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
                playlistManager.updateTopSong(event.song) }
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
                                state = state.copy(songs = songs)
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
