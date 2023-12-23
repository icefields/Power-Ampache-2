package luci.sixsixsix.powerampache2.presentation.artists

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
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.navigation.ArtistsNavGraph

@Destination
@ArtistsNavGraph(start = true)
@Composable
fun ArtistsScreen(
    navigator: DestinationsNavigator,
    viewModel: ArtistsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
//        OutlinedTextField(
//            value = state.searchQuery,
//            onValueChange = {
//               // viewModel.onEvent(ArtistEvent.OnSearchQueryChange(it))
//            },
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            placeholder = {
//                Text(text = "Search ...")
//            },
//            maxLines = 1,
//            singleLine = true
//        )

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(ArtistEvent.Refresh) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.artists.size) { i ->
                    val artist = state.artists[i]
                    ArtistItem(
                        artist = artist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(ArtistDetailScreenDestination(artist.id))
                            }
                            .padding(16.dp)
                    )

                    if(i < state.artists.size - 1) {
                        // if not last item add a divider
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    } else if(i == state.artists.size - 1) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .alpha( if (state.isFetchingMore) { 1.0f } else { 0.0f } )
                            )
                        }
                    }

                    // search queries are limited, do not fetch more in case of a search string
                    if(i == (state.artists.size - 1) && state.searchQuery.isNullOrBlank()) {
                        // if last item, fetch more
                        viewModel.onEvent(ArtistEvent.OnBottomListReached(i))
                    }
                }
            }
        }
    }
}
