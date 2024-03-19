package luci.sixsixsix.powerampache2.presentation.screens.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.dialogs.EraseConfirmDialog
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.screens.playlists.components.PlaylistItem

@Composable
@Destination(start = false)
fun PlaylistsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state
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
            dialogTitle = "DELETE PLAYLIST",
            dialogText = "Delete ${playlistToRemove.name}?"
        )
    }

    Box(modifier = modifier) {
        if (state.isLoading && state.playlists.isEmpty()) {
            LoadingScreen()
        } else {
            Column {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = { viewModel.onEvent(PlaylistEvent.Refresh) }
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(
                            items = state.playlists,
                            key = { _, item: Playlist -> item }
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

                            if (i == state.playlists.size - 1 && state.isLoading) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .alpha(if (state.isFetchingMore) { 1.0f } else { 0.0f })
                                            .padding(dimensionResource(id = R.dimen.divider_padding))
                                    )
                                }
                            }

                            // search queries are limited, do not fetch more in case of a search string
                            if (i == (state.playlists.size - 1) && state.searchQuery.isBlank()) {
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
