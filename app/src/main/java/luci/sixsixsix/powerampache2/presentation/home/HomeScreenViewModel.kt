package luci.sixsixsix.powerampache2.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val playlistsRepository: PlaylistsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //var state2 by mutableStateOf(HomeScreenState())
    //private val state = savedStateHandle.getLiveData<HomeScreenState>("itemsKey").value
    var state by savedStateHandle.saveable { mutableStateOf(HomeScreenState()) }

    init {
        L()
        fetchAllAsync()
        // playlists can change or be edited, make sure to always listen to the latest version
        playlistsRepository.playlistsLiveData.observeForever { playlists ->
            L("viewmodel.getPlaylists observed playlist change", state.playlists.size)
            updatePlaylistsState(playlists)
        }
    }

    private fun fetchAllAsync() = viewModelScope.launch {
        async { getPlaylists() }
        async { getFlagged() }
        async { getFrequent() }
        async { getHighest() }
        async { getNewest() }
        async { getRecent() }
        async { getRandom() }
    }

    private suspend fun fetchAll() = viewModelScope.launch {
        getPlaylists()
        getFlagged()
        getFrequent()
        getHighest()
        getNewest()
        getRecent()
        getRandom()
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.Refresh -> {
                fetchAllAsync()
            }

            is HomeScreenEvent.OnSearchQueryChange -> {}
        }
    }

    private fun updatePlaylistsState(playlists: List<Playlist>) {
        // inject generated playlists
        val playlistList = mutableListOf<Playlist>(
            HighestPlaylist(),
            RecentPlaylist(),
            FrequentPlaylist(),
            FlaggedPlaylist()
        )
        playlistList.addAll(playlists)
        if (state.playlists != playlistList) {
            L("viewmodel.getPlaylists playlists are different, update")
            state = state.copy(playlists = playlistList)
        }
    }

    private suspend fun getPlaylists(fetchRemote: Boolean = true) {
        playlistsRepository
            .getPlaylists(fetchRemote)
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { playlists ->
                            updatePlaylistsState(playlists)
                            L("HomeScreenViewModel.getPlaylists size ${state.playlists.size}")
                        }
                        L("HomeScreenViewModel.getPlaylists size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        L("ERROR HomeScreenViewModel.getPlaylists", result.exception)
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                }
            }
    }

    private suspend fun replaceWithRandomIfEmpty(
        albums: List<Album>,
        callback: (albums: List<Album>) -> Unit
    ) {
        if (albums.isNullOrEmpty()) {
            getRandom(fetchRemote = true) { albums ->
                callback(albums)
            }
        }
    }

    private suspend fun getRecent(fetchRemote: Boolean = true) {
        albumsRepository
            .getRecentAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            state = state.copy(recentAlbums = albums)
                            L("size", state.playlists.size)
                        }
                        replaceWithRandomIfEmpty(state.recentAlbums) {
                            state = state.copy(recentAlbums = it)
                        }
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        replaceWithRandomIfEmpty(state.recentAlbums) {
                            state = state.copy(recentAlbums = it)
                        }
                        L("ERROR HomeScreenViewModel.getRecent ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                }
            }
    }

    private suspend fun getFlagged(fetchRemote: Boolean = true) {
        albumsRepository
            .getFlaggedAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            state = state.copy(flaggedAlbums = albums)
                            L("HomeScreenViewModel.getFlagged size ${state.playlists.size}")
                        }
                        L("HomeScreenViewModel.getFlagged size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        L("ERROR HomeScreenViewModel.getFlagged ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                }
            }

    }

    private suspend fun getNewest(fetchRemote: Boolean = true) {
        albumsRepository
            .getNewestAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            state = state.copy(newestAlbums = albums)
                            L("HomeScreenViewModel.getNewest size ${state.playlists.size}")
                        }
                        replaceWithRandomIfEmpty(state.newestAlbums) {
                            state = state.copy(newestAlbums = it)
                        }
                        L("HomeScreenViewModel.getNewest size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        replaceWithRandomIfEmpty(state.newestAlbums) {
                            state = state.copy(newestAlbums = it)
                        }
                        L("ERROR HomeScreenViewModel.getNewest ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                }
            }
    }

    private suspend fun getHighest(fetchRemote: Boolean = true) {
        albumsRepository
            .getHighestAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            state = state.copy(highestAlbums = albums)
                            L("HomeScreenViewModel.getHighest size ${state.playlists.size}")
                        }
                        L("HomeScreenViewModel.getHighest size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        L("ERROR HomeScreenViewModel.getHighest ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                }
            }
    }


    private suspend fun getFrequent(fetchRemote: Boolean = true) {
        albumsRepository
            .getFrequentAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            state = state.copy(frequentAlbums = albums)
                            L("HomeScreenViewModel.getFrequent size ${state.playlists.size}")
                        }
                        L("HomeScreenViewModel.getFrequent size of network array ${result.networkData?.size}")
                        replaceWithRandomIfEmpty(state.frequentAlbums) {
                            state = state.copy(frequentAlbums = it)
                        }
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        replaceWithRandomIfEmpty(state.frequentAlbums) {
                            state = state.copy(frequentAlbums = it)
                        }
                        L("ERROR HomeScreenViewModel.getFrequent ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                }
            }
    }

    private suspend fun getRandom(fetchRemote: Boolean = true) {
        getRandom(fetchRemote = fetchRemote) { albums ->
            state = state.copy(randomAlbums = albums)
            L("HomeScreenViewModel.getRandom size ${state.playlists.size}")
        }
    }

    private suspend fun getRandom(
        fetchRemote: Boolean = true,
        callback: (albums: List<Album>) -> Unit
    ) {
        albumsRepository
            .getRandomAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            callback(albums)
                        }
                        L("HomeScreenViewModel.getRandom size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        L("ERROR HomeScreenViewModel.getRandom ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                }
            }
    }
}
