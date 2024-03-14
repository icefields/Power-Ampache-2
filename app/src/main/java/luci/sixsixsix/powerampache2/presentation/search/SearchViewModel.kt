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
package luci.sixsixsix.powerampache2.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val artistsRepository: ArtistsRepository,
    private val albumsRepository: AlbumsRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val songsRepository: SongsRepository,
    private val settingsRepository: SettingsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    var state by mutableStateOf(SearchScreenState())

    var offlineModeState by mutableStateOf(false)
    private var searchSongsDeferred: Deferred<Job>? = null
    private var searchAlbumsDeferred: Deferred<Job>? = null
    private var searchPlaylistsDeferred: Deferred<Job>? = null
    private var searchArtistsDeferred: Deferred<Job>? = null
    private var fetchByGenreJob: Job? = null

    private val offlineModeFlow = settingsRepository.settingsLiveData
        .distinctUntilChanged()
        .asFlow()
        .map { it?.isOfflineModeEnabled == true }

    init {
        fetchGenres()
        // TODO this code is a repetition of SettingsViewModel
        viewModelScope.launch {
            offlineModeFlow.collect {
                if (it != offlineModeState) {
                    offlineModeState = it
                }
                // re-fetch genres when changing offline mode
                fetchGenres()
            }
        }

        viewModelScope.launch {
            playlistManager.currentSearchQuery.collect { query ->
                L("SearchViewModel search query changed" , query)
                if (query.length >=3) {
                    onEvent(SearchViewEvent.OnSearchQueryChange(query))
                } else if (query.isBlank()) {
                    clearData() // return to genre screen
                }
            }
        }
    }

    private var fetchGenresJob: Job? = null

    private fun fetchGenres() {
        fetchGenresJob?.cancel()
        fetchGenresJob = viewModelScope.launch {
            if (!offlineModeState) {
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

    private suspend fun fetchGenresOffline() = songsRepository.getSongs().collect { result ->
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
        artistsRepository.getArtistsByGenre(genre).collect { result ->
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
        songsRepository.getSongsByGenre(genre).collect { result ->
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
        songsRepository.getSongs().collect { result ->
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
        if (!offlineModeState) {
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
        if (!offlineModeState) {
            searchAlbumsDeferred = async { searchAlbums() }
            searchPlaylistsDeferred = async { searchPlaylists() }
            searchArtistsDeferred = async { searchArtists() }
            searchAlbumsDeferred?.await()
            searchPlaylistsDeferred?.await()
            searchArtistsDeferred?.await()
        }
        searchSongsDeferred?.await()
    }

    private fun searchSongs() = viewModelScope.launch {
        songsRepository.getSongs(true, state.searchQuery).collect { result ->
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
        albumsRepository.getAlbums(true, state.searchQuery).collect { result ->
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
        artistsRepository.getArtists(true, state.searchQuery).collect { result ->
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
        playlistsRepository.getPlaylists(true, state.searchQuery).collect { result ->
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

    fun onEvent(event: SearchViewEvent) {
        when (event) {
            is SearchViewEvent.OnSearchQueryChange ->
                if (event.query.isBlank() && state.searchQuery.isBlank()) { } else {
                    state = state.copy(searchQuery = event.query)
                    search()
                }
            SearchViewEvent.Refresh ->
                fetchGenres()
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
                playlistManager.addToCurrentQueueUpdateTopSong(event.song, state.songs)
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
