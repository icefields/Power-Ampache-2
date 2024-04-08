package luci.sixsixsix.powerampache2.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
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
    private val artistsRepository: ArtistsRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    var state by mutableStateOf(HomeScreenState())
    //var state by savedStateHandle.saveable { mutableStateOf(HomeScreenState()) }

    val offlineModeStateFlow = settingsRepository.offlineModeFlow.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        L()

        viewModelScope.launch {
            // playlists can change or be edited, make sure to always listen to the latest version
            playlistsRepository.playlistsFlow.distinctUntilChanged().collect { playlists ->
                L("viewmodel.getPlaylists observed playlist change", state.playlists.size)
                updatePlaylistsState(playlists)
            }
        }

        viewModelScope.launch {
            // playlists can change or be edited, make sure to always listen to the latest version
            offlineModeStateFlow.collectLatest { isOfflineMode ->
                if (isOfflineMode) {
                    state = state.copy(playlists = listOf(), recentAlbums = listOf())
                }
                fetchAllAsync()
            }
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
        if (/*playlists.isNotEmpty() && */ state.playlists != playlistList) {
            state = state.copy(playlists = playlistList)
        }
    }

    private suspend fun getPlaylists(fetchRemote: Boolean = true) {
        playlistsRepository
            .getPlaylists(fetchRemote)
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        updatePlaylistsState(result.data ?: listOf())
                        L("HomeScreenViewModel.getPlaylists size of network array ${result.networkData?.size}")
                    }
                    is Resource.Error -> {
                        state = state.copy(isPlaylistsLoading = false, isLoading = false)
                        L("ERROR HomeScreenViewModel.getPlaylists", result.exception)
                    }
                    is Resource.Loading -> {
                        state = state.copy(isPlaylistsLoading = result.isLoading, isLoading = result.isLoading)
                    }
                }
            }
    }

    private suspend fun replaceWithRandomIfEmpty(
        albums: List<AmpacheModel>,
        fetchRemote: Boolean = false,
        callback: (albums: List<Album>) -> Unit
    ) {
        if (albums.isNullOrEmpty()) {
            getRandom(fetchRemote = fetchRemote) { albums ->
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
                        // TODO use network data for recent right now, add lastPlayed field to
                        //  every entity to start using db in conjunction
                        result.networkData?.let { albums ->
                            state = state.copy(recentAlbums = albums)

                            if (!offlineModeStateFlow.value) {
                                replaceWithRandomIfEmpty(state.recentAlbums, fetchRemote = true) {
                                    state = state.copy(recentAlbums = it)
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        state = state.copy(isRecentAlbumsLoading = false, isLoading = false)
                        if (!offlineModeStateFlow.value) {
                            replaceWithRandomIfEmpty(state.recentAlbums) {
                                state = state.copy(recentAlbums = it)
                            }
                        }
                        L("ERROR HomeScreenViewModel.getRecent ${result.exception}")
                    }
                    is Resource.Loading -> {
                        state = state.copy(isRecentAlbumsLoading = result.isLoading, isLoading = result.isLoading)
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
                        }
                        L("HomeScreenViewModel.getFlagged size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isFlaggedAlbumsLoading = false, isLoading = false)
                        L("ERROR HomeScreenViewModel.getFlagged ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isFlaggedAlbumsLoading = result.isLoading, isLoading = result.isLoading)
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
                        }
                        replaceWithRandomIfEmpty(state.newestAlbums) {
                            state = state.copy(newestAlbums = it)
                        }
                        L("HomeScreenViewModel.getNewest size of network array ${result.networkData?.size}")
                    }
                    is Resource.Error -> {
                        state = state.copy(isNewestAlbumsLoading = false, isLoading = false)
                        replaceWithRandomIfEmpty(state.newestAlbums) {
                            state = state.copy(newestAlbums = it)
                        }
                        L("ERROR HomeScreenViewModel.getNewest ${result.exception}")
                    }
                    is Resource.Loading -> {
                        state = state.copy(isNewestAlbumsLoading = result.isLoading, isLoading = result.isLoading)
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
                        }
                        L("HomeScreenViewModel.getHighest size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isHighestAlbumsLoading = false, isLoading = false)
                        L("ERROR HomeScreenViewModel.getHighest ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isHighestAlbumsLoading = result.isLoading, isLoading = result.isLoading)
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
                            state = state.copy(frequentAlbums = mergeFrequentItems(albums))
                        }
                        L("HomeScreenViewModel.getFrequent size of network array ${result.networkData?.size}")
                        replaceWithRandomIfEmpty(state.frequentAlbums) { albums ->
                            L("aaaa", "replace with random data null")
                            state = state.copy(frequentAlbums = albums)
                        }
                    }

                    is Resource.Error -> {
                        state = state.copy(isFrequentAlbumsLoading = false, isLoading = false)
                        replaceWithRandomIfEmpty(state.frequentAlbums) { albums ->
                            L("aaaa", "replace with random error")

                            state = state.copy(frequentAlbums = albums)
                        }
                        L("ERROR HomeScreenViewModel.getFrequent ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isFrequentAlbumsLoading = result.isLoading, isLoading = result.isLoading)
                    }
                }
            }
    }

    private suspend fun mergeFrequentItems(albums: List<AmpacheModel>): List<AmpacheModel> =
        albums.toMutableList<AmpacheModel>().apply {
            artistsRepository.getMostPlayedArtists().forEachIndexed { index, artist ->
                val indexToAdd = (index * 3) + 2
                if (indexToAdd < albums.size) {
                    add(indexToAdd, artist)
                }
            }
        }

    private suspend fun getRandom(fetchRemote: Boolean = true) {
        getRandom(fetchRemote = fetchRemote) { albums ->
            state = state.copy(randomAlbums = albums)
        }
    }

    private suspend fun getRandom(
        fetchRemote: Boolean = true,
        callback: (albums: List<Album>) -> Unit
    ) {
        albumsRepository
            .getRandomAlbums(fetchRemote = fetchRemote)
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            callback(albums)
                        }
                        L("HomeScreenViewModel.getRandom size of network array ${result.networkData?.size}")
                    }

                    is Resource.Error -> {
                        state = state.copy(isRandomAlbumsLoading = false, isLoading = false)
                        L("ERROR HomeScreenViewModel.getRandom ${result.exception}")
                    }

                    is Resource.Loading -> {
                        state = state.copy(isRandomAlbumsLoading = result.isLoading, isLoading = result.isLoading)
                    }
                }
            }
    }
}
