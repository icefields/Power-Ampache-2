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
package luci.sixsixsix.powerampache2.presentation.screens.albums

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.EmptyListView
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.screens.albums.components.AlbumItem
import luci.sixsixsix.powerampache2.presentation.screens.albums.components.AlbumsGridHeader
import kotlin.math.abs

const val GRID_ITEMS_ROW = 2
const val GRID_ITEMS_ROW_LAND = 5
const val GRID_ITEMS_ROW_MIN = 2
const val GRID_ITEMS_ROW_SQUARE = 4

@Destination
@Composable
fun AlbumsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    gridItemsRow: Int = GRID_ITEMS_ROW,
    minGridItemsRow: Int = GRID_ITEMS_ROW_MIN,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state
    val offlineModeState by viewModel.offlineModeStateFlow.collectAsState()

    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }.collect { orientation = it }
    }
    val cardsPerRow = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> GRID_ITEMS_ROW_LAND
        else -> {
            if (state.albums.size < 5) {
                minGridItemsRow
            } else {
                if (isSquare()) GRID_ITEMS_ROW_SQUARE else gridItemsRow
            }
        }
    }

    //val cardsPerRow = if (state.albums.size < 5) { minGridItemsRow } else { gridItemsRow }
    val albumCardSize = (LocalConfiguration.current.screenWidthDp / cardsPerRow).dp
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var sortDirectionExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberLazyGridState()
    val isHeaderVisible by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }

    Column(modifier = modifier) {
        AlbumsGridHeader(
            isHeaderVisible = (isHeaderVisible) || (state.isLoading.not() && state.albums.isEmpty()),
            isLoading = state.isLoading,
            currentSortSelection = state.sort,
            currentDirection = state.order,
            sortMenuExpanded = sortMenuExpanded,
            sortDirectionExpanded = sortDirectionExpanded,
            onSortSelection = { newSortOrder ->
                viewModel.onEvent(AlbumsEvent.OnSortOrder(newSortOrder))
            },
            onDirectionSelection = { newSortOrder ->
                viewModel.onEvent(AlbumsEvent.OnSortDirection(newSortOrder))
            },
            onSortExpandedChange = { sortMenuExpanded = it },
            onDirectionExpandedChange = { sortDirectionExpanded = it },
            onDismissMenu = {
                sortMenuExpanded = false
                sortDirectionExpanded = false
            }
        )

        if (state.albums.isEmpty()) {
            if (!offlineModeState && state.isLoading) {
                LoadingScreen()
            } else if (!offlineModeState) {
                EmptyListView(title = stringResource(id = R.string.albums_emptyView_warning))
            } else if (!state.isLoading) {
                EmptyListView(title = stringResource(id = R.string.offline_noData_warning))
            }
        } else {
            SwipeRefresh(
                modifier = Modifier.fillMaxWidth().weight(1f),
                state = swipeRefreshState,
                onRefresh = { viewModel.onEvent(AlbumsEvent.Refresh) }
            ) {
                LazyVerticalGrid(
                    state = scrollState,
                    columns = GridCells.Fixed(cardsPerRow)
                ) {
                    items(count = state.albums.size) { i ->
                        val album = state.albums[i]
                        AlbumItem(
                            album = album,
                            modifier = Modifier
                                .size(albumCardSize)
                                .clickable {
                                    navigator.navigate(AlbumDetailScreenDestination(album.id, album))
                                }.padding(
                                    horizontal = dimensionResource(id = R.dimen.albumItem_paddingHorizontal),
                                    vertical = dimensionResource(id = R.dimen.albumItem_paddingVertical)
                                )
                        )
                        // search queries are limited, do not fetch more in case of a search string
                        if (i == (state.albums.size - gridItemsRow) && state.searchQuery.isBlank()) {
                            // if last item, fetch more
                            viewModel.onEvent(AlbumsEvent.OnBottomListReached(i))
                        }
                    }
                }
            }
        }

        if (state.isFetchingMore) {
            LoadingView(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun isSquare(threshold: Float = 1.4f): Boolean =
    if (LocalConfiguration.current.screenHeightDp > 0 && LocalConfiguration.current.screenWidthDp > 0) {
        val screenRatio = abs(LocalConfiguration.current.screenHeightDp.toFloat() / LocalConfiguration.current.screenWidthDp.toFloat())
        screenRatio < threshold // ratio lower than the constant are considered square
    } else {
        // default to not square if cannot calculate screen size
        false
    }


@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .wrapContentSize()
    ) {
        Box(modifier = Modifier.wrapContentSize()) {
            CircularProgressIndicator(modifier = Modifier
                .size(22.dp)
                .align(Alignment.Center))
        }
    }
}
