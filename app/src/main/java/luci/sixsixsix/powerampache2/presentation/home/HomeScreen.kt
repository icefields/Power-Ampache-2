package luci.sixsixsix.powerampache2.presentation.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.home.components.HomeScreenSection

@Composable
@Destination(start = false)
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    // to add sections to the home screen just add Title and Array of Albums, Playlists or Songs
    val homeScreenItems = mapOf(
        Pair("Recent", state.recentAlbums),
        Pair("Flagged", state.flaggedAlbums),
        Pair("Frequent", state.frequentAlbums),
        Pair("Highest", state.highestAlbums),
        Pair("Newest", state.newestAlbums),
        Pair("More Albums", state.randomAlbums),
        Pair("Playlists", state.playlists),)

    LazyColumn(modifier = modifier) {
        items(homeScreenItems.keys.toList()) { title ->
            val elem = homeScreenItems[title]
            HomeScreenSection(navigator = navigator, albumsRow = elem, text = title)
        }
    }
}
