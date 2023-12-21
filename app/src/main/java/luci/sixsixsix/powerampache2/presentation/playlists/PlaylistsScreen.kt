package luci.sixsixsix.powerampache2.presentation.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import luci.sixsixsix.powerampache2.presentation.artists.ArtistItem
import luci.sixsixsix.powerampache2.presentation.artists.ArtistsViewModel
import luci.sixsixsix.powerampache2.presentation.songs.SongsEvent


@Composable
@Destination(start = false)
fun PlaylistsScreen(
//    navigator: DestinationsNavigator,
    viewModel: PlaylistsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = {
                viewModel.onEvent(PlaylistEvent.OnSearchQueryChange(it))
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Search ...")
            },
            maxLines = 1,
            singleLine = true
        )

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(PlaylistEvent.Refresh) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.playlists.size) { i ->
                    val playlist = state.playlists[i]
                    PlaylistItem(
                        playlist = playlist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // TODO navigate
                                //navigator.navigate(CompanyInfoScreenDestination(company.symbol))
                            }
                            .padding(16.dp)
                    )

                    // if not last item add a divider
                    if(i < state.playlists.size) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}
