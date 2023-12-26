package luci.sixsixsix.powerampache2.presentation.artist_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.albums.components.AlbumItem
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination

@Destination
@Composable
fun ArtistDetailScreen(
    navigator: DestinationsNavigator,
    artistId: String,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(ArtistDetailEvent.Refresh) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.albums.size) { i ->
                    val album = state.albums[i]
                    AlbumItem(
                        album = album,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(AlbumDetailScreenDestination(album.id, album))
                            }
                            .padding(16.dp)
                    )

                    if (i < state.albums.size - 1) {
                        // if not last item add a divider
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}
