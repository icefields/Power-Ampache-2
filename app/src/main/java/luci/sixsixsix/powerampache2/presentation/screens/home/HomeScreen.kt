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

import androidx.annotation.StringRes
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
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.common.EmptyListView
import luci.sixsixsix.powerampache2.presentation.screens.home.components.HomeScreenSection

sealed class HomeScreenRowItems(@StringRes val title: Int, val items: List<AmpacheModel> = listOf()) {
    data class Playlists(val list: List<Playlist>): HomeScreenRowItems(title = R.string.home_section_title_playlists, items = list)
    data class Recent(val list: List<AmpacheModel>): HomeScreenRowItems(title = R.string.home_section_title_recent, items = list)
    data class Favourite(val list: List<AmpacheModel>): HomeScreenRowItems(title = R.string.home_section_title_flagged, items = list)
    data class Frequent(val list: List<AmpacheModel>): HomeScreenRowItems(title = R.string.home_section_title_frequent, items = list)
    data class Highest(val list: List<AmpacheModel>): HomeScreenRowItems(title = R.string.home_section_title_highest, items = list)
    data class Newest(val list: List<AmpacheModel>): HomeScreenRowItems(title = R.string.home_section_title_newest, items = list)
    data class More(val list: List<AmpacheModel>): HomeScreenRowItems(title = R.string.home_section_title_moreAlbums, items = list)

    // used to indicate loading
    data class Nothing(val isLoading: Boolean): HomeScreenRowItems(title = R.string.home_section_title_loading)

    fun isNotEmpty() = !items.isNullOrEmpty()
}


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

    val homeScreenRowItems = listOf(
        HomeScreenRowItems.Recent(state.recentAlbums),
        HomeScreenRowItems.Playlists(state.playlists),
        HomeScreenRowItems.Favourite(state.flaggedAlbums),
        HomeScreenRowItems.Frequent(state.frequentAlbums),
        HomeScreenRowItems.Highest(state.highestAlbums),
        HomeScreenRowItems.Newest(state.newestAlbums),
        HomeScreenRowItems.More(state.randomAlbums),
        HomeScreenRowItems.Nothing(isLoadingData(state))
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
                    items(homeScreenRowItems) {
                        HomeScreenSection(
                            navigator = navigator,
                            itemsRow = it,
                            text = stringResource(it.title)
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
