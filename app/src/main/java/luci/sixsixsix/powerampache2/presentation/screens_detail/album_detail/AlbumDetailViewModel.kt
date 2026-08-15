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
package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import luci.sixsixsix.powerampache2.domain.usecase.albums.AlbumFromIdUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.RecommendedArtistsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.AlbumDataFromPluginUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsInfoPluginInstalled
import luci.sixsixsix.powerampache2.domain.usecase.settings.LocalSettingsFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ToggleGlobalShuffleUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.IsSongAvailableOfflineUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.OfflineSongsFlow
import luci.sixsixsix.powerampache2.presentation.models.SongUI
import luci.sixsixsix.powerampache2.presentation.models.isAvailableOffline
import luci.sixsixsix.powerampache2.presentation.models.toSongUI
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previous view model, we
    // need this because we're passing the symbol around
    offlineModeFlowUseCase: OfflineModeFlowUseCase,
    localSettingsFlowUseCase: LocalSettingsFlowUseCase,
    private val isSongAvailableOfflineUseCase: IsSongAvailableOfflineUseCase,
    private val toggleGlobalShuffleUseCase: ToggleGlobalShuffleUseCase,
    private val recommendedArtistsUseCase: RecommendedArtistsUseCase,
    private val isInfoPluginInstalled: IsInfoPluginInstalled,
    private val getAlbumInfoPluginUseCase: AlbumDataFromPluginUseCase,
    private val albumFromIdUseCase: AlbumFromIdUseCase,
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val errorHandler: ErrorHandler,
    private val offlineSongsFlow: OfflineSongsFlow
) : ViewModel() {
    var state by mutableStateOf(AlbumDetailState())

    val offlineModeStateFlow = offlineModeFlowUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val globalShuffleStateFlow = localSettingsFlowUseCase()
        .map { it.isGlobalShuffleEnabled }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalSettings.SETTINGS_DEFAULTS_GLOBAL_SHUFFLE)

    @OptIn(ExperimentalCoroutinesApi::class)
    val albumStateFlow: StateFlow<Album> =
        savedStateHandle.getStateFlow<String?>("albumId", null)
            .filterNotNull()
            .flatMapConcat { albumId ->
                getSongsFromAlbum(albumId)
                albumFromIdUseCase(albumId)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Album())

    val infoPluginArtistStateFlow = albumStateFlow.map { album ->
        getAlbumInfoFromPlugin(album)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
//        viewModelScope.launch {
//            playlistManager.downloadedSongFlow.collectLatest { song ->
//                if(song != null) {
//                    L("RefreshFromCache")
//                    // if song belongs to this album, refresh
//                    if (state.songs.map { it.song }.map { it.mediaId }.contains(song.mediaId)) {
//                        refreshFromCache()
//                    }
//                }
//            }
//        }

        viewModelScope.launch {
            // why drop(1)? OfflineSongsFlow is a state flow, meaning it has an initial value,
            // we're only interested in changes post loading, in particular downloads and delete.
            offlineSongsFlow()//.map { songs -> songs.map { it.id }.toHashSet() }
                .drop(1)
                .map { songs -> albumStateFlow.value.id to songs}
                .filter { (albumId, _) -> albumId.isNotBlank() }
                .map { (albumId, songs) ->
                    // only get songs that belong to the album. Add the ids to a hashset for quick
                    // comparison for distinctUntilChanged.
                    songs.filter { it.album.id == albumId }.map { it.id }.toHashSet()
                }
                .distinctUntilChanged()
                //.filter { it.isNotEmpty() }
                .debounce(300) // avoids rapid-fire refreshes during quick changes in offline songs.
                .collectLatest { ids ->
                    L("aaaa RefreshFromCache ${ids.size}")
                    refreshFromCache()
            }
        }
    }

    fun onEvent(event: AlbumDetailEvent) {
        when (event) {
            is AlbumDetailEvent.Fetch -> {
                L("AlbumDetailEvent.Fetch", event.albumId)
                getSongsFromAlbum(albumId = event.albumId, fetchRemote = true)
            }
            is AlbumDetailEvent.OnSongSelected -> { }
            is AlbumDetailEvent.OnPlayAlbum -> { }
            AlbumDetailEvent.OnShareAlbum ->
                shareAlbum(albumStateFlow.value.id)
            AlbumDetailEvent.OnShuffleAlbum -> { }
            AlbumDetailEvent.OnFavouriteAlbum -> favouriteAlbum()
            AlbumDetailEvent.OnShufflePlaylistToggle -> viewModelScope.launch {
                try {
                    toggleGlobalShuffleUseCase()
                } catch (e: Exception) {
                    errorHandler.updateErrorLogMessage(e.stackTraceToString())
                }
            }

            is AlbumDetailEvent.OnNewRating -> rateAlbum(rating = event.rating)
        }
    }

    private fun refreshFromCache() {
        if (albumStateFlow.value.id.isNotBlank()) {
            L("AlbumDetailEvent.RefreshFromCache", albumStateFlow.value.id)
            getSongsFromAlbum(albumId = albumStateFlow.value.id, fetchRemote = false)
        }
    }

    private fun rateAlbum(albumId: String = albumStateFlow.value.id, rating: Int) = viewModelScope.launch {
        albumsRepository.rateAlbum(albumId, rating)
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh album
                            // state = state.copy(album = state.album.copy(rating = rating))
                        }
                    }
                    is Resource.Error -> state = state.copy( isLoading = false)
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                }
            }
    }


    private fun favouriteAlbum(albumId: String = albumStateFlow.value.id) = viewModelScope.launch {
        albumsRepository.likeAlbum(albumId, (albumStateFlow.value.flag != 1))
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh album
                            //state = state.copy(album = state.album.copy(flag = abs(state.album.flag - 1)))
                        }
                    }
                    is Resource.Error -> state = state.copy(isLikeLoading = false)
                    is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
                }
            }
    }

    private fun isAlbumDownloaded(songs: List<SongUI>): Boolean {
        songs.forEach {
            if (!it.isAvailableOffline()) return false
        }
        return true
    }

    private fun getSongsFromAlbum(albumId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            songsRepository
                .getSongsFromAlbum(albumId, fetchRemote)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songUIList = songs.toSongUI {
                                    isSongAvailableOfflineUseCase(it)
                                }
                                state = state.copy(
                                    songs = songUIList,
                                    isAlbumDownloaded = isAlbumDownloaded(songUIList)
                                )
                                L("AlbumDetailViewModel.getSongsFromAlbum size", result.data?.size, "network", result.networkData?.size)
                            }
                        }

                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }

    // TODO: this is never used
    private fun getRecommendedArtists(artistId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            recommendedArtistsUseCase(baseArtistId =  artistId, fetchRemote = fetchRemote)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { artists ->
                                state = state.copy(recommendedArtists = artists)
                                L("AlbumDetailViewModel.getRecommendedArtists size", result.data?.size, "network", result.networkData?.size)
                            }
                        }

                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }

    private fun shareAlbum(albumId: String) = viewModelScope.launch {
        albumsRepository.getAlbumShareLink(albumId).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    application.shareLink(it)
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }
    }

    private suspend fun getAlbumInfoFromPlugin(album: Album) =
        if (isInfoPluginInstalled() && (album.name.isNotBlank() || album.artist.name.isNotBlank())) {
        getAlbumInfoPluginUseCase(album)
    } else null
}
