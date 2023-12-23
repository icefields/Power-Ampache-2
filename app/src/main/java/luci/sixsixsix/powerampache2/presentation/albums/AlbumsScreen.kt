package luci.sixsixsix.powerampache2.presentation.albums

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.navigation.AlbumsNavGraph

@Destination
@AlbumsNavGraph(start = true)
@Composable
fun AlbumsScreen(
    navigator: DestinationsNavigator,
    viewModel: AlbumsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(AlbumsEvent.Refresh) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.albums.size) { i ->
                    val album = state.albums[i]
                    AlbumItem(
                        album = album,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(AlbumDetailScreenDestination(album.id))
                            }
                            .padding(16.dp)
                    )

                    if(i < state.albums.size - 1) {
                        // if not last item add a divider
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    } else if(i == state.albums.size - 1) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .alpha( if (state.isFetchingMore) { 1.0f } else { 0.0f } )
                            )
                        }
                    }

                    // search queries are limited, do not fetch more in case of a search string
                    if(i == (state.albums.size - 1) && state.searchQuery.isNullOrBlank()) {
                        // if last item, fetch more
                        viewModel.onEvent(AlbumsEvent.OnBottomListReached(i))
                    }
                }
            }
        }
    }
}
