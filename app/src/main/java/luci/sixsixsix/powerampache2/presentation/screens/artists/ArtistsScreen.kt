/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.screens.artists

import android.content.res.Configuration
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.screens.artists.components.ArtistItem
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs

const val GRID_ITEMS_ROW = 3
const val GRID_ITEMS_ROW_LAND = 6

@Destination
@Composable
fun ArtistsScreen(
    navigator: DestinationsNavigator,
    gridPerRow: Int = GRID_ITEMS_ROW,
    viewModel: ArtistsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    ArtistsScreenContent(
        artists = viewModel.state.artists,
        isLoading = viewModel.state.isLoading,
        isRefreshing = viewModel.state.isRefreshing,
        onEvent = viewModel::onEvent,
        navigateToArtist = { id, artist ->
            Ampache2NavGraphs.navigateToArtist(navigator, artistId = id, artist = artist)
        }
    )
}

@Destination
@Composable
fun ArtistsScreenContent(
    artists: List<Artist>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    gridPerRow: Int = GRID_ITEMS_ROW,
    modifier: Modifier = Modifier,
    swipeToRefreshEnabled: Boolean = true,
    onEvent: (ArtistEvent) -> Unit,
    navigateToArtist: (artistId: String, artist: Artist) -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }
    val gridItemsRow = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            GRID_ITEMS_ROW_LAND
        }
        else -> gridPerRow
    }

    if (isLoading && artists.isEmpty()) {
        LoadingScreen()
    }

    Column(
        modifier = modifier
    ) {
        SwipeRefresh(
            swipeEnabled = swipeToRefreshEnabled,
            state = swipeRefreshState,
            onRefresh = { onEvent(ArtistEvent.Refresh) }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridItemsRow),
                modifier = Modifier.fillMaxSize()) {
                items(artists.size) { i ->
                    val artist = artists[i]
                    ArtistItem(
                        artist = artist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigateToArtist(artist.id, artist)
                            }
                            .padding(12.dp)
                    )

                    // search queries are limited, do not fetch more in case of a search string
                    if(i == (artists.size - 1)/* && searchQuery.isNullOrBlank()*/) {
                        // if last item, fetch more
                        onEvent(ArtistEvent.OnBottomListReached(i))
                    }
                }
            }
        }
        if(isLoading) {
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