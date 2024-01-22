package luci.sixsixsix.powerampache2.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.screens.albums.AlbumsEvent
import luci.sixsixsix.powerampache2.presentation.screens.home.components.HomeScreenSection

@Composable
@Destination(start = false)
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)

    // to add sections to the home screen just add Title and Array of Albums, Playlists or Songs
    val homeScreenItems = mapOf(
        Pair(stringResource(id = R.string.home_section_title_recent), state.recentAlbums),
        Pair(stringResource(id = R.string.home_section_title_playlists), state.playlists),
        Pair(stringResource(id = R.string.home_section_title_flagged), state.flaggedAlbums),
        Pair(stringResource(id = R.string.home_section_title_frequent), state.frequentAlbums),
        Pair(stringResource(id = R.string.home_section_title_highest), state.highestAlbums),
        Pair(stringResource(id = R.string.home_section_title_newest), state.newestAlbums),
        Pair(stringResource(id = R.string.home_section_title_moreAlbums), state.randomAlbums),
        // TODO this is a hack, passing "loading" as identifier to visualize a loading progress
        //  at the bottom while data is loading. A null list in this case means isLoading = true,
        //  and empty list means isLoading = false.
        //  Do this properly!
        Pair("loading", if(isLoadingData(state)) null else listOf()),
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(HomeScreenEvent.Refresh) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(homeScreenItems.keys.toList()) { title ->
                    HomeScreenSection(
                        navigator = navigator,
                        albumsRow = homeScreenItems[title],
                        text = title
                    )
                }
            }
        }
    }
}

fun isLoadingData(state: HomeScreenState) = (state.isLoading ||
        (state.recentAlbums.isNullOrEmpty() ||
                state.randomAlbums.isNullOrEmpty() ||
                state.newestAlbums.isNullOrEmpty() ||
                state.frequentAlbums.isNullOrEmpty()
                )
        )

@Composable
fun LoadingView() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator(modifier = Modifier.size(44.dp).align(Alignment.Center))
        }
    }
}
