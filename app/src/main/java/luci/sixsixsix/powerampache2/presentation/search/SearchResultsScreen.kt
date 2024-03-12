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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.search.screens.GenresScreen
import luci.sixsixsix.powerampache2.presentation.search.screens.ResultsListView

const val GRID_ITEMS_ROW = 2
const val GRID_ITEMS_ROW_LAND = 5
const val GRID_ITEMS_ROW_MIN = 2

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    searchViewModel: SearchViewModel,
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val searchState = searchViewModel.state
    val controller = LocalSoftwareKeyboardController.current

    BackHandler {
        mainViewModel.onEvent(MainEvent.OnSearchQueryChange(""))
        searchViewModel.onEvent(SearchViewEvent.Clear)
        controller?.hide()
    }

    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }
    if (playlistsDialogOpen.isOpen) {
        if (playlistsDialogOpen.songs.isNotEmpty()) {
            AddToPlaylistOrQueueDialog(
                songs = playlistsDialogOpen.songs,
                onDismissRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                },
                mainViewModel = mainViewModel,
                viewModel = addToPlaylistOrQueueDialogViewModel,
                onCreatePlaylistRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                }
            )
        }
    }

    if (searchState.isNoSearch) {
        AnimatedVisibility(visible = searchState.selectedGenre == null) {
            GenresScreen(
                genres = searchState.genres,
                isLoading = searchState.isLoading,
                isFetchingMore = searchState.isFetchingMore,
                searchQuery = searchState.searchQuery,
                modifier = modifier,
                onEvent = searchViewModel::onEvent
            )
        }

    } else if (searchState.isNoResults) {
        // show no results screen (search query present but no results)
        showHideEmptyResultsView(searchState.isLoading, searchState.isFetchingMore, searchState.isNoResults)
    } else {
        // show search results
        ResultsListView(
            songs = searchState.songs,
            albums = searchState.albums,
            artists = searchState.artists,
            playlists = searchState.playlists,
            swipeToRefreshEnabled = false,
            isLoading = searchState.isLoading,
            isRefreshing = searchState.isFetchingMore,
            onEvent = searchViewModel::onEvent,
            onSongEvent = mainViewModel::onEvent,
            onSongSelected = { song ->
                searchViewModel.onEvent(SearchViewEvent.OnSongSelected(song))
                mainViewModel.onEvent(MainEvent.Play(song))
            },
            onAlbumSelected = { albumId, album ->
                navigator.navigate(
                    AlbumDetailScreenDestination(albumId = albumId, album = album))
            },
            onArtistSelected = { artistId, artist ->
                navigator.navigate(
                    ArtistDetailScreenDestination(artistId = artistId, artist = artist))
            },
            onPlaylistSelected = {
                navigator.navigate(PlaylistDetailScreenDestination(playlist = it))
            },
            onOpenPlaylistDialog = {
                playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, it)
            }
        )
    }
}

@Composable
private fun showHideEmptyResultsView(isLoading: Boolean, isRefreshing: Boolean, isNoResults: Boolean) {
    if (!isLoading && !isRefreshing && isNoResults){
        Card(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No results from your search query")
            }
        }
    }
}