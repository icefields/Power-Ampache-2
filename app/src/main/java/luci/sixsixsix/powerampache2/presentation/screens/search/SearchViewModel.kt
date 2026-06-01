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
package luci.sixsixsix.powerampache2.presentation.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.usecase.albums.AlbumsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.ArtistsByGenreUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.ArtistsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.playlists.PlaylistsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.LocalSettingsFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.GetSongsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.OfflineSongsFlow
import luci.sixsixsix.powerampache2.domain.usecase.songs.SongsByGenreUseCase
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val getSongsUseCase: GetSongsUseCase,
    private val songsByGenreUseCase: SongsByGenreUseCase,
    private val artistsByGenreUseCase: ArtistsByGenreUseCase,
    private val artistsUseCase: ArtistsUseCase,
    private val albumsUseCase: AlbumsUseCase,
    private val playlistsUseCase: PlaylistsUseCase,
    settingsFlow: LocalSettingsFlowUseCase,
    offlineSongsFlow: OfflineSongsFlow,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    var state by mutableStateOf(SearchScreenState())
    private var searchSongsDeferred: Deferred<Job>? = null
    private var searchAlbumsDeferred: Deferred<Job>? = null
    private var searchPlaylistsDeferred: Deferred<Job>? = null
    private var searchArtistsDeferred: Deferred<Job>? = null
    private var fetchByGenreJob: Job? = null
    private var fetchGenresJob: Job? = null

    private val offlineModeState = settingsFlow()
        .map { it.isOfflineModeEnabled }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        viewModelScope.launch {
            offlineModeState
                .filterNotNull() // skips the synthetic null, only passes real booleans
                .collectLatest { isOffline ->
                    L("SearchViewModel fetchGenres $isOffline")
                    fetchGenres()
                }
        }

        viewModelScope.launch {
            playlistManager.currentSearchQuery.collect { query ->
                L("SearchViewModel search query changed" , query)
                if (query.length >=3) {
                    onSearchQueryChange(query)
                } else if (query.isBlank()) {
                    clearData() // return to genre screen
                }
            }
        }

        viewModelScope.launch {
            offlineSongsFlow()
                .map { offlineSongs ->
                    if (playlistManager.currentSearchQuery.value.length < 3) return@map emptyList()
                    val visibleIds = state.songs.map { it.id }.toSet()
                    offlineSongs.filter { it.id in visibleIds }
                }
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .collect {
                    L("SearchViewModel offline song state update, something in this list was updated")
                    searchSongs(fetchRemote = false)
                }
        }
    }

    private fun fetchGenres() {
        fetchGenresJob?.cancel()
        fetchGenresJob = viewModelScope.launch {
            if (!offlineModeState.value) {
                fetchGenresNetwork()
            } else {
                fetchGenresOffline()
            }
        }
    }

    private suspend fun fetchGenresNetwork() = musicRepository.getGenres(fetchRemote = true).collect { result ->
        when (result) {
            is Resource.Success -> result.data?.let { genres -> state = state.copy(genres = genres.sortedByDescending { genre ->
                genre.songs
            }) }
            is Resource.Error -> state = state.copy(isLoading = false)
            is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
        }
    }

    private suspend fun fetchGenresOffline() = getSongsUseCase().collect { result ->
        when (result) {
            is Resource.Success ->
                result.data?.let { songs ->
                    val genres: List<Genre> = HashSet<Genre>().apply {
                        songs.map { it.genre }.forEach { attributes ->
                            addAll(attributes.map { Genre(
                                id = it.id,
                                name = it.name,
                                0, 0, 0, 0)
                            })
                        }
                    }.toList()
                    state = state.copy(genres = genres)
                }
            is Resource.Error ->
                state = state.copy(isLoading = false)
            is Resource.Loading ->
                state = state.copy(isLoading = result.isLoading)
        }
    }

    private suspend fun fetchArtistsByGenre(genre: Genre) =
        artistsByGenreUseCase(genre).collect { result ->
            when (result) {
                is Resource.Success ->
                    result.data?.let { artists ->
                        state = state.copy(artists = artists)
                    }
                is Resource.Error ->
                    state = state.copy(isLoading = false)
                is Resource.Loading ->
                    state = state.copy(isLoading = result.isLoading)
            }
        }

    private suspend fun fetchSongsByGenre(genre: Genre) =
        songsByGenreUseCase(genre).collect { result ->
            when (result) {
                is Resource.Success ->
                    result.data?.let { songs ->
                        state = state.copy(songs = songs)
                    }
                is Resource.Error ->
                    state = state.copy(isLoading = false)
                is Resource.Loading ->
                    state = state.copy(isLoading = result.isLoading)
            }
        }

    private suspend fun searchOfflineSongsByGenre(genre: Genre) =
        getSongsUseCase().collect { result ->
            when (result) {
                is Resource.Success ->
                    result.data?.let { songs ->
                        val mapped = songs.filter {
                            it.genre.joinToString(", ").contains(genre.name)
                        }
                        state = state.copy(songs = mapped)
                    }
                is Resource.Error ->
                    state = state.copy(isLoading = false)
                is Resource.Loading ->
                    state = state.copy(isLoading = result.isLoading)
            }
        }

    private suspend fun fetchByGenre(genre: Genre) {
        if (!offlineModeState.value) {
            fetchSongsByGenre(genre)
            fetchArtistsByGenre(genre)
        } else {
            searchOfflineSongsByGenre(genre)
        }
    }

    private fun cancelJobs() {
        searchSongsDeferred?.cancel()
        searchAlbumsDeferred?.cancel()
        searchPlaylistsDeferred?.cancel()
        searchArtistsDeferred?.cancel()
        fetchByGenreJob?.cancel()
    }

    private fun search() = viewModelScope.launch {
        // the calls don't depend on each other's execution, they can be executed
        // asynchronously for better performance
        cancelJobs()

        searchSongsDeferred = async { searchSongs() }
        if (!offlineModeState.value) {
            searchPlaylistsDeferred = async { searchPlaylists() }
            searchArtistsDeferred = async { searchArtists() }
            searchAlbumsDeferred = async { searchAlbums() }
            searchPlaylistsDeferred?.await()
            searchAlbumsDeferred?.await()
            searchArtistsDeferred?.await()
        }
        searchSongsDeferred?.await()
    }

    private fun searchSongs(fetchRemote: Boolean = true) = viewModelScope.launch {
        getSongsUseCase(fetchRemote = fetchRemote, query = state.searchQuery).collect { result ->
            when (result) {
                is Resource.Success ->
                    result.data?.let { songs ->
                        state = state.copy(songs = songs)
                    }
                is Resource.Error ->
                    state = state.copy(isLoading = false)
                is Resource.Loading ->
                    state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun searchAlbums() = viewModelScope.launch {
        albumsUseCase(true, state.searchQuery).collect { result ->
            when (result) {
                is Resource.Success ->
                    result.data?.let { albums ->
                        state = state.copy(albums = albums)
                    }
                is Resource.Error ->
                    state = state.copy(isLoading = false)
                is Resource.Loading ->
                    state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun searchArtists() = viewModelScope.launch {
        artistsUseCase(fetchRemote = true, query = state.searchQuery).collect { result ->
            when (result) {
                is Resource.Success ->
                    result.data?.let { artists ->
                        state = state.copy(artists = artists)
                    }
                is Resource.Error ->
                    state = state.copy(isLoading = false)
                is Resource.Loading ->
                    state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun searchPlaylists() = viewModelScope.launch {
        playlistsUseCase(fetchRemote = true, query = state.searchQuery).collect { result ->
            when (result) {
                is Resource.Success ->
                    result.data?.let { playlists ->
                        state = state.copy(playlists = playlists)
                    }
                is Resource.Error ->
                    state = state.copy(isLoading = false)
                is Resource.Loading ->
                    state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun clearData() = SearchScreenState(genres = state.genres).also {
        cancelJobs()
        state = it
    }

    private fun onSearchQueryChange(query: String) {
        if (query.isBlank() && state.searchQuery.isBlank()) {
            // TODO: remove if empty branch?
        } else {
            state = state.copy(searchQuery = query)
            search()
        }
    }

    fun onEvent(event: SearchViewEvent) {
        when (event) {
            is SearchViewEvent.OnSearchQueryChange -> onSearchQueryChange(event.query)
            SearchViewEvent.Refresh -> fetchGenres()
            is SearchViewEvent.OnBottomListReached -> {}
            is SearchViewEvent.OnGenreSelected -> {
                fetchByGenreJob?.cancel()
                fetchByGenreJob = viewModelScope.launch {
                    state = state.copy(selectedGenre = event.genre)
                    event.genre?.let { genre ->
                        fetchByGenre(genre)
                    }
                }
            }
            SearchViewEvent.Clear ->
                clearData()
            is SearchViewEvent.OnSongSelected -> {
                //playlistManager.addToCurrentQueueUpdateTopSong(event.song, state.songs)
            }

            SearchViewEvent.FetchGenres ->
                fetchGenres()
        }
    }

    override fun onCleared() {
        cancelJobs()
        super.onCleared()
    }
}
