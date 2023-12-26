package luci.sixsixsix.powerampache2.presentation.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
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
        Pair(stringResource(id = R.string.home_section_title_recent), state.recentAlbums),
        Pair(stringResource(id = R.string.home_section_title_flagged), state.flaggedAlbums),
        Pair(stringResource(id = R.string.home_section_title_frequent), state.frequentAlbums),
        Pair(stringResource(id = R.string.home_section_title_highest), state.highestAlbums),
        Pair(stringResource(id = R.string.home_section_title_newest), state.newestAlbums),
        Pair(stringResource(id = R.string.home_section_title_moreAlbums), state.randomAlbums),
        Pair(stringResource(id = R.string.home_section_title_playlists), state.playlists)
    )

    LazyColumn(modifier = modifier) {
        items(homeScreenItems.keys.toList()) { title ->
            HomeScreenSection(
                navigator = navigator,
                albumsRow = homeScreenItems[title],
                text = title
            )
        }
    }
}
