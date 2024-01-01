package luci.sixsixsix.powerampache2.presentation.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.artists.components.ArtistItem
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination

const val GRID_ITEMS_ROW = 3

@Destination
@Composable
fun ArtistsScreen(
    navigator: DestinationsNavigator,
    viewModel: ArtistsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    if (state.isLoading && state.artists.isEmpty()) {
        LoadingScreen()
    }

    Column(
        modifier = modifier
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(ArtistEvent.Refresh) }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(GRID_ITEMS_ROW),
                modifier = Modifier.fillMaxSize()) {
                items(state.artists.size) { i ->
                    val artist = state.artists[i]
                    ArtistItem(
                        artist = artist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(ArtistDetailScreenDestination(artistId = artist.id, artist = artist))
                            }
                            .padding(12.dp)
                    )

                    if(i < state.artists.size - 1) {
                        // if not last item add a divider
                        //Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    } else if(i == state.artists.size - 1) {

                    }

                    // search queries are limited, do not fetch more in case of a search string
                    if(i == (state.artists.size - 1) && state.searchQuery.isNullOrBlank()) {
                        // if last item, fetch more
                        viewModel.onEvent(ArtistEvent.OnBottomListReached(i))
                    }
                }
            }
        }
        if(viewModel.state.isLoading) {
            L( "ArtistsScreen isLoading ")

            Column(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
