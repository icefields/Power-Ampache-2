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
package luci.sixsixsix.powerampache2.presentation.screens.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.EraseConfirmDialog
import luci.sixsixsix.powerampache2.presentation.screens.playlists.components.PlaylistItem

@Composable
@Destination(start = false)
fun PlaylistsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val playlistsState by viewModel.playlistsStateFlow.collectAsState()
    val state = viewModel.state

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    var showDeletePlaylistDialogData by remember { mutableStateOf<Playlist?>(null) }

    showDeletePlaylistDialogData?.let { playlistToRemove ->
        EraseConfirmDialog(
            onDismissRequest = {
                showDeletePlaylistDialogData = null
                // The song is dismissed automatically even when dismissing the dialog,
                // refresh the song list automatically
                viewModel.onEvent(PlaylistEvent.OnRemovePlaylistDismiss)
            },
            onConfirmation = {
                showDeletePlaylistDialogData = null
                viewModel.onEvent(PlaylistEvent.OnPlaylistDelete(playlistToRemove))
            },
            // "DELETE PLAYLIST",
            dialogTitle = stringResource(id = R.string.dialog_deletePlaylist_title),
            //"Delete ${playlistToRemove.name}?"
            dialogText = stringResource(id = R.string.dialog_deletePlaylist_text, playlistToRemove.name)
        )
    }

    Box(modifier = modifier) {
        if (state.isLoading && playlistsState.isEmpty()) {
            LoadingScreen()
        } else {
            Column {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = { viewModel.onEvent(PlaylistEvent.Refresh) }
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(
                            items = playlistsState,
                            //key = { _, item: Playlist -> item }
                        ) { i, playlist ->
                            PlaylistItem(
                                playlistInfo = playlist,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigator.navigate(
                                            PlaylistDetailScreenDestination(playlist = playlist)
                                        )
                                    },
                                enableSwipeToRemove = viewModel.isCurrentUserOwner(playlist),
                                onRemove = {
                                    showDeletePlaylistDialogData = it
                                }
                            )

//                            if (i == state.playlists.size - 1 && state.isLoading) {
//                                Column(modifier = Modifier.fillMaxWidth()) {
//                                    CircularProgressIndicator(
//                                        modifier = Modifier
//                                            .align(Alignment.CenterHorizontally)
//                                            .alpha(if (state.isFetchingMore) { 1.0f } else { 0.0f })
//                                            .padding(dimensionResource(id = R.dimen.divider_padding))
//                                    )
//                                }
//                            }

                            // search queries are limited, do not fetch more in case of a search string
                            if (i == (playlistsState.size - 1) && state.searchQuery.isBlank()) {
                                // if last item, fetch more
                                viewModel.onEvent(PlaylistEvent.OnBottomListReached(i))
                            }
                        }
                    }
                }
            }
        }
    }
}
