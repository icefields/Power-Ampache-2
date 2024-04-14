/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
import luci.sixsixsix.powerampache2.domain.models.Artist
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

    private val offsetFrequent = (0..2).random()
    private val offsetNewest = (0..2).random()
    private val offsetRandom = (0..2).random()

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

    private var playlistsJob: Job? = null
    private var flaggedJob: Job? = null
    private var frequentJob: Job? = null
    private var highestJob: Job? = null
    private var newestJob: Job? = null
    private var recentJob: Job? = null
    private var randomJob: Job? = null

    private fun cancelJobs() {
        playlistsJob?.cancel()
        flaggedJob?.cancel()
        frequentJob?.cancel()
        highestJob?.cancel()
        newestJob?.cancel()
        recentJob?.cancel()
        randomJob?.cancel()
    }

    private fun fetchAllAsync(){
        cancelJobs()

        playlistsJob = viewModelScope.launch {
            async { getPlaylists() }
        }
        recentJob = viewModelScope.launch {
            async { getRecent() }
        }
        flaggedJob = viewModelScope.launch {
            async { getFlagged() }
        }
        frequentJob = viewModelScope.launch {
            async { getFrequent() }
        }
        highestJob = viewModelScope.launch {
            async { getHighest() }
        }
        newestJob = viewModelScope.launch {
            async { getNewest() }
        }
        randomJob = viewModelScope.launch {
            async { getRandom() }
        }
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

// ---- PLAYLISTS
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

// ---- RECENT
    private suspend fun getRecent(fetchRemote: Boolean = true) {
        albumsRepository
            .getRecentAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // TODO use network data for recent right now, add lastPlayed field to
                        //  every entity to start using db in conjunction
                        result.networkData?.let { albums ->
                            state = state.copy(recentAlbums = injectArtists(albums, offsetFrequent))

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

// ---- FAVOURITES
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

// ---- NEWEST
    private suspend fun getNewest(fetchRemote: Boolean = true) {
        albumsRepository
            .getNewestAlbums()
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            state = state.copy(newestAlbums = injectArtists(albums, offsetNewest))
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

// ---- HIGHEST
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


// ---- FREQUENT
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
                            state = state.copy(frequentAlbums = albums)
                        }
                    }

                    is Resource.Error -> {
                        state = state.copy(isFrequentAlbumsLoading = false, isLoading = false)
                        replaceWithRandomIfEmpty(state.frequentAlbums) { albums ->
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


// ---- RANDOM
    private suspend fun getRandom(fetchRemote: Boolean = true) {
        getRandom(fetchRemote = fetchRemote, injectArtists = true) { albums ->
            state = state.copy(randomAlbums = albums)
        }
    }

    private suspend fun getRandom(
        fetchRemote: Boolean = true,
        injectArtists: Boolean = false,
        callback: (albums: List<AmpacheModel>) -> Unit
    ) {
        albumsRepository
            .getRandomAlbums(fetchRemote = fetchRemote)
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { albums ->
                            callback(if (injectArtists) injectArtists(albums, offsetRandom) else albums)
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

    private suspend fun replaceWithRandomIfEmpty(
        albums: List<AmpacheModel>,
        fetchRemote: Boolean = false,
        callback: (albums: List<AmpacheModel>) -> Unit
    ) {
        if (albums.isNullOrEmpty()) {
            getRandom(fetchRemote = fetchRemote, injectArtists = true) { albums ->
                callback(albums)
            }
        }
    }


// ---- ARTISTS INJECTION
    private fun addArtistsToAlbumList(
        albums: List<AmpacheModel>,
        artists: List<Artist>,
        resultList: MutableList<AmpacheModel>,
        offset: Int = 2,
        frequency: Int = 3
    ) {
        if (frequency < 1) return

        artists.forEachIndexed { index, artist ->
            val indexToAdd = (index * frequency) + offset
            if (indexToAdd < albums.size) {
                resultList.add(indexToAdd, artist)
            } else {
                // do not add too many artists
                if (index < (albums.size/(frequency-1) )) {
                    resultList.add(artist)
                }
            }
        }
    }

    private fun injectArtists(albums: List<AmpacheModel>, offset: Int): List<AmpacheModel> =
        albums.toMutableList<AmpacheModel>().apply {
            val generateArtistsList = HashMap<String, Artist>()
            forEach { album ->
                if (album is Album) {
                    generateArtistsList[album.artist.id] = Artist(
                        id = album.artist.id,
                        name = album.artist.name,
                        artUrl = album.artUrl
                    )
                }
            }

            addArtistsToAlbumList(albums,
                generateArtistsList.values.toList(),
                resultList = this,
                offset = offset)
        }

    private suspend fun mergeFrequentItems(albums: List<AmpacheModel>): List<AmpacheModel> =
        albums.toMutableList<AmpacheModel>().apply {
            val artistsMostPlayed = artistsRepository.getMostPlayedArtists()
            if (artistsMostPlayed.isNotEmpty()) {
                addArtistsToAlbumList(
                    albums,
                    artistsMostPlayed,
                    resultList = this,
                    offset = offsetFrequent
                )
            }
            //else {
            //    injectArtists(albums)
            //}

//            artistsRepository.getMostPlayedArtists().forEachIndexed { index, artist ->
//                val indexToAdd = (index * 3) + 2
//                if (indexToAdd < albums.size) {
//                    add(indexToAdd, artist)
//                }
//            }
        }

    override fun onCleared() {
        cancelJobs()
        super.onCleared()
    }
}
