package luci.sixsixsix.powerampache2.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager

import javax.inject.Inject
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Playlist

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    var state by mutableStateOf(HomeScreenState())

    init {
        getPlaylists()
        getFlagged()
        getFrequent()
        getHighest()
        getNewest()
        getRecent()
        getNewest()
        getRandom()
    }

    fun onEvent(event: HomeScreenEvent) {
        when(event) {
            is HomeScreenEvent.Refresh -> {
                getPlaylists(fetchRemote = true)
            }
            else -> {}
        }
    }

    private fun getPlaylists(fetchRemote: Boolean = true) {
        viewModelScope.launch {
            repository
                .getPlaylists(fetchRemote)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { playlists ->
                                state = state.copy(playlists = playlists)
                                L("HomeScreenViewModel.getPlaylists size ${state.playlists.size}")
                            }
                            L( "HomeScreenViewModel.getPlaylists size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR HomeScreenViewModel.getPlaylists ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getRecent(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            repository
                .getRecentAlbums()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(recentAlbums = albums)
                                L("HomeScreenViewModel.getRecent size ${state.playlists.size}")
                            }
                            L( "HomeScreenViewModel.getRecent size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR HomeScreenViewModel.getRecent ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getFlagged(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            repository
                .getFlaggedAlbums()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(flaggedAlbums = albums)
                                L("HomeScreenViewModel.getFlagged size ${state.playlists.size}")
                            }
                            L( "HomeScreenViewModel.getFlagged size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR HomeScreenViewModel.getFlagged ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getNewest(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            repository
                .getNewestAlbums()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(newestAlbums = albums)
                                L("HomeScreenViewModel.getNewest size ${state.playlists.size}")
                            }
                            L( "HomeScreenViewModel.getNewest size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR HomeScreenViewModel.getNewest ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getHighest(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            repository
                .getHighestAlbums()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(highestAlbums = albums)
                                L("HomeScreenViewModel.getHighest size ${state.playlists.size}")
                            }
                            L( "HomeScreenViewModel.getHighest size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR HomeScreenViewModel.getHighest ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getFrequent(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            repository
                .getFrequentAlbums()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(frequentAlbums = albums)
                                L("HomeScreenViewModel.getFrequent size ${state.playlists.size}")
                            }
                            L( "HomeScreenViewModel.getFrequent size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR HomeScreenViewModel.getFrequent ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun getRandom(fetchRemote: Boolean = true, ) {
        viewModelScope.launch {
            repository
                .getRandomAlbums()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { albums ->
                                state = state.copy(randomAlbums = albums)
                                L("HomeScreenViewModel.getRandom size ${state.playlists.size}")
                            }
                            L( "HomeScreenViewModel.getRandom size of network array ${result.networkData?.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            L( "ERROR HomeScreenViewModel.getRandom ${result.exception}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}


sealed class HomeScreenEvent {
    data object Refresh: HomeScreenEvent()
    data class OnSearchQueryChange(val query: String): HomeScreenEvent()
}

data class HomeScreenState (///, , , recent, forgotten, ,
    val playlists: List<Playlist> = emptyList(),
    val recentAlbums: List<Album> = emptyList(),
    val newestAlbums: List<Album> = emptyList(),
    val highestAlbums: List<Album> = emptyList(),
    val frequentAlbums: List<Album> = emptyList(),
    val flaggedAlbums: List<Album> = emptyList(),
    val randomAlbums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
)
