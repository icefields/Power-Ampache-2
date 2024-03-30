package luci.sixsixsix.powerampache2.presentation.screens.artists

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
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val artistsRepository: ArtistsRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    var state by mutableStateOf(ArtistsState())
    private var isEndOfDataReached: Boolean = false

    val offlineModeStateFlow = settingsRepository.offlineModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        viewModelScope.launch {
            offlineModeStateFlow.collectLatest {
                getArtists()
            }
        }
    }

    fun onEvent(event: ArtistEvent) {
        when(event) {
            is ArtistEvent.Refresh -> {
                getArtists(fetchRemote = true)
            }
            is ArtistEvent.OnSearchQueryChange -> {
                if (event.query.isBlank() && state.searchQuery.isBlank()) {

                } else {
                    state = state.copy(searchQuery = event.query)
                    getArtists()
                }
            }
            is ArtistEvent.OnBottomListReached -> {
                if (!state.isFetchingMore && !isEndOfDataReached) {
                    L( "ArtistEvent.OnBottomListReached")
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
            artistsRepository
                .getArtists(fetchRemote, query, offset)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { artists ->
                                state = state.copy(artists = artists)
                                L( "viewmodel.getArtists size ${state.artists.size}")
                            }
                            isEndOfDataReached = (result.networkData.isNullOrEmpty() && offset > 0) || offlineModeStateFlow.value
                            L("viewmodel.getArtists is bottom reached? $isEndOfDataReached size of new network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            // TODO set end of data list otherwise keeps fetching? do for other screens too
                            isEndOfDataReached = true
                            state = state.copy(isFetchingMore = false, isLoading = false)
                            L( "ERROR AlbumsViewModel ${result.exception}")
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
