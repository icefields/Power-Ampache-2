package luci.sixsixsix.powerampache2.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val mainRepository: MusicRepository,
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
            mainRepository
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
            albumsRepository
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
            albumsRepository
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
            albumsRepository
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
            albumsRepository
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
            albumsRepository
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
            albumsRepository
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
