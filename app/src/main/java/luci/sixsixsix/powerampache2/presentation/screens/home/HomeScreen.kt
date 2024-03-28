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
package luci.sixsixsix.powerampache2.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.EmptyListView
import luci.sixsixsix.powerampache2.presentation.screens.home.components.HOME_LOADING_VIEW_IDENTIFIER
import luci.sixsixsix.powerampache2.presentation.screens.home.components.HomeScreenSection

@Composable
@Destination(start = false)
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    val offlineModeState by viewModel.offlineModeStateFlow.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    var emptyViewVisible by remember { mutableStateOf(false) }

    // to add sections to the home screen just add Title and Array of Albums, Playlists or Songs
    val homeScreenItems = mapOf(
        Pair(stringResource(id = R.string.home_section_title_recent), state.recentAlbums),
        Pair(stringResource(id = R.string.home_section_title_playlists), state.playlists),
        Pair(stringResource(id = R.string.home_section_title_flagged), state.flaggedAlbums),
        Pair(stringResource(id = R.string.home_section_title_frequent), state.frequentAlbums),
        Pair(stringResource(id = R.string.home_section_title_highest), state.highestAlbums),
        Pair(stringResource(id = R.string.home_section_title_newest), state.newestAlbums),
        Pair(stringResource(id = R.string.home_section_title_moreAlbums), state.randomAlbums),
        // TODO this is a hack, passing a const string as identifier to visualize a loading progress
        //  at the bottom while data is loading. A null list in this case means isLoading = true,
        //  and empty list means isLoading = false.
        //  Do this properly!
        Pair(HOME_LOADING_VIEW_IDENTIFIER, if(isLoadingData(state)) null else listOf()),
    )

    val showEmptyView = isNoData(state) && offlineModeState
    LaunchedEffect(showEmptyView) {
        // wait 1.2 seconds before showing the switch
        delay(1200)
        emptyViewVisible = showEmptyView
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(HomeScreenEvent.Refresh) }
        ) {
            if (showEmptyView) {
                AnimatedVisibility(emptyViewVisible,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)),
                    exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
                ) {
                    EmptyListView(title = stringResource(id = R.string.offline_noData_warning))
                }
            } else {
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
}

fun isLoadingData(state: HomeScreenState) = (
        state.isLoading ||
        (
            state.isRecentAlbumsLoading ||
            state.isRandomAlbumsLoading ||
            state.isNewestAlbumsLoading||
            state.isFrequentAlbumsLoading
        ) ||
        (   state.frequentAlbums.isNullOrEmpty() &&
            state.recentAlbums.isNullOrEmpty() &&
            state.randomAlbums.isNullOrEmpty() &&
            state.newestAlbums.isNullOrEmpty() &&
            state.playlists.isNullOrEmpty()
        )
)

fun isNoData(state: HomeScreenState) =
    state.frequentAlbums.isNullOrEmpty() &&
    state.recentAlbums.isNullOrEmpty() &&
    state.randomAlbums.isNullOrEmpty() &&
    state.newestAlbums.isNullOrEmpty() &&
    state.playlists.isNullOrEmpty()

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
            CircularProgressIndicator(modifier = Modifier
                .size(44.dp)
                .align(Alignment.Center))
        }
    }
}
