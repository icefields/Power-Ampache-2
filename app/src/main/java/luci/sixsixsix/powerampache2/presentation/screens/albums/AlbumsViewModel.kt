/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.screens.albums

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.common.Constants.REQUEST_LIMIT_ALBUMS
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder
import luci.sixsixsix.powerampache2.domain.models.SortOrder
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumsRepository,
    offlineModeFlowUseCase: OfflineModeFlowUseCase
) : ViewModel() {
    private var fetchMoreJob: Job? = null
    private var fetchJob: Job? = null
    var state by mutableStateOf(AlbumsState())
    private var isEndOfDataList: Boolean = false

    val offlineModeStateFlow = offlineModeFlowUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        viewModelScope.launch {
            // playlists can change or be edited, make sure to always listen to the latest version
            offlineModeStateFlow.collectLatest { isOfflineMode ->
                fetchJob?.cancel()
                fetchJob = getAlbums()
            }
        }
    }

    fun onEvent(event: AlbumsEvent) {
        when (event) {
            is AlbumsEvent.Refresh -> {
                fetchJob?.cancel()
                fetchJob = getAlbums(fetchRemote = true)
            }
            is AlbumsEvent.OnSortDirection -> {
                state = state.copy(order = event.sortDirection, isLoading = false)
                fetchJob?.cancel()
                fetchJob = getAlbums()
            }
            is AlbumsEvent.OnSortOrder -> {
                state = state.copy(sort = event.sortOrder, isLoading = false)
                fetchJob?.cancel()
                fetchJob = getAlbums()
            }
            is AlbumsEvent.OnBottomListReached -> {
                if (!state.isFetchingMore && !isEndOfDataList && fetchJob?.isActive == false) {
                    fetchMoreJob?.cancel()
                    fetchMoreJob = viewModelScope.launch {
                        delay(500)
                        L("AlbumsEvent.OnBottomListReached")
                        state = state.copy(isFetchingMore = true)
                        fetchJob?.cancel()
                        fetchJob = getAlbums(fetchRemote = true, offset = state.albums.size)
                    }
                }
            }
            is AlbumsEvent.OnSearchQueryChange -> {
                if (event.query.isBlank() && state.searchQuery.isBlank()) {

                } else {
                    state = state.copy(searchQuery = event.query)
                    getAlbums()
                }
            }
        }
    }

    private fun getAlbums(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = true,
        offset: Int = 0,
        limit: Int = REQUEST_LIMIT_ALBUMS,
        sort: AlbumSortOrder = state.sort,
        order: SortOrder = state.order
    ) = viewModelScope.launch {
            repository.getAlbums(
            fetchRemote = fetchRemote,
            query = query,
            offset = offset,
            limit = limit,
            sort = sort,
            order = order
        ).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { albums ->
                        L("viewmodel.getAlbums pre filter size ${albums.size}")
                        state = when (sort) {
                            AlbumSortOrder.RATING -> state.copy(albums = albums.filter { it.rating > 0 })
                            AlbumSortOrder.AVERAGE_RATING -> state.copy(albums = albums.filter { it.averageRating > 0 })
                            else -> state.copy(albums = albums)
                        }
                        L("viewmodel.getAlbums size ${state.albums.size}")
                    }
                    isEndOfDataList = ( ((result.networkData?.size ?: 0) < limit) && offset > 0)
                }

                is Resource.Error -> {
                    // TODO set end of data list otherwise keeps fetching? do for other screens too
                    isEndOfDataList = true
                    state = state.copy(isFetchingMore = false, isLoading = false)
                    L("ERROR AlbumsViewModel ${result.exception}")
                }

                is Resource.Loading -> {
                    state = state.copy(isLoading = result.isLoading)
                    if (!result.isLoading) {
                        state = state.copy(isFetchingMore = false)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
        fetchJob = null
        fetchMoreJob?.cancel()
        fetchMoreJob = null
    }
}
