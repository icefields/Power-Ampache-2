package luci.sixsixsix.powerampache2.presentation.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
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
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.playlists.components.PlaylistItem

@Composable
@Destination(start = false)
fun PlaylistsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

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
                        items(state.playlists.size) { i ->
                            val playlist = state.playlists[i]
                            PlaylistItem(
                                playlistInfo = playlist,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigator.navigate(
                                            PlaylistDetailScreenDestination(playlist = playlist))
                                    }
                            )

                            if (i == state.playlists.size - 1) {
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
