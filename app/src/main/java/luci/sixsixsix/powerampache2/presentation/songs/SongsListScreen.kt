package luci.sixsixsix.powerampache2.presentation.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.domain.models.key
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem

@Composable
@Destination(start = false)
fun SongsListScreen(
    navigator: DestinationsNavigator,
    viewModel: SongsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Box(modifier = modifier) {
        if (state.isLoading && state.songs.isNullOrEmpty()) {
            LoadingScreen()
        }
        Column {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = { viewModel.onEvent(SongsEvent.Refresh) }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),) {
                        items(
                            state.songs.size,
                            // TODO conflicts, why? key = { i -> state.songs[i].key() }
                        ) { i ->
                            val song = state.songs[i]
                            SongItem(
                                song = song,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onEvent(SongsEvent.OnSongSelected(song))
                                        mainViewModel.onEvent(MainEvent.Play(song))
                                    }
                            )
                            // search queries are limited, do not fetch more in case of a search string
                            if (i == (state.songs.size - 1) && state.searchQuery.isNullOrBlank()) {
                                // if last item, fetch more
                                viewModel.onEvent(SongsEvent.OnBottomListReached(i))
                            }
                            notSureYet(i = i, state = state)
                        }
                    }
                }
        }
    }
}

@Composable
fun notSureYet(i: Int, state: SongsState) {
    if(i < state.songs.size - 1) {
        // if not last item add a divider
        // TODO: do I want a divider? Divider(modifier = Modifier.padding(horizontal = 16.dp))
    } else if(i == state.songs.size - 1) {
        // TODO should this screen be allowed to load more ?
//                        Column(modifier = Modifier.fillMaxWidth()) {
//                            CircularProgressIndicator(
//                                modifier = Modifier
//                                    .align(Alignment.CenterHorizontally)
//                                    .alpha(
//                                        if (state.isFetchingMore) {
//                                            1.0f
//                                        } else {
//                                            0.0f
//                                        }
//                                    )
//                            )
//                        }
    }
}