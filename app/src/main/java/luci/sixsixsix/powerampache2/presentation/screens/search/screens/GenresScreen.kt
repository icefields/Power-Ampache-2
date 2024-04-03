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
package luci.sixsixsix.powerampache2.presentation.screens.search.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.screens.albums.LoadingView
import luci.sixsixsix.powerampache2.presentation.screens.search.GRID_ITEMS_ROW
import luci.sixsixsix.powerampache2.presentation.screens.search.GRID_ITEMS_ROW_LAND
import luci.sixsixsix.powerampache2.presentation.screens.search.GRID_ITEMS_ROW_MIN
import luci.sixsixsix.powerampache2.presentation.screens.search.SearchViewEvent
import luci.sixsixsix.powerampache2.presentation.screens.search.SearchViewModel
import luci.sixsixsix.powerampache2.presentation.screens.search.components.GenreListItem

@Composable
fun GenresScreen(
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {
    val searchState = searchViewModel.state
    GenresScreen(
        genres = searchState.genres,
        isLoading = searchState.isLoading,
        isFetchingMore = searchState.isFetchingMore,
        searchQuery = searchState.searchQuery,
        modifier = modifier,
        onEvent = searchViewModel::onEvent
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GenresScreen(
    genres: List<Genre>,
    isLoading: Boolean,
    isFetchingMore: Boolean,
    searchQuery: String,
    gridItemsRow: Int = GRID_ITEMS_ROW,
    minGridItemsRow: Int = GRID_ITEMS_ROW_MIN,
    modifier: Modifier = Modifier,
    onEvent: (SearchViewEvent) -> Unit
) {
    val localFocusManager = LocalFocusManager.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isFetchingMore)
    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
    val controller = LocalSoftwareKeyboardController.current

    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }.collect { orientation = it }
    }

    val cardsPerRow = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> { GRID_ITEMS_ROW_LAND }
        else -> if (genres.size < 5) { minGridItemsRow } else { gridItemsRow }
    }

    if (isLoading && genres.isEmpty()) {
        LoadingScreen()
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { localFocusManager.clearFocus() })
            }
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                onEvent(SearchViewEvent.Refresh)
            }
        ) {
            LazyVerticalGrid(
                modifier = Modifier.padding(horizontal = 10.dp),
                columns = GridCells.Fixed(cardsPerRow)
            ) {
                items(
                    count = genres.size,
                    //key = { i -> genres[i] }
                )
                { i ->
                    val genre = genres[i]
                    GenreListItem(
                        modifier = Modifier
                            .heightIn(max = 100.dp)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        genre = genre
                    ) {
                        controller?.hide()
                        onEvent(SearchViewEvent.OnGenreSelected(it))
                    }
                    // search queries are limited, do not fetch more in case of a search string
                    if (i == (genres.size - 8) && searchQuery.isBlank()) {
                        // if last item, fetch more
                        onEvent(SearchViewEvent.OnBottomListReached(i))
                    }
                }
            }
        }
        if (isFetchingMore) {
            LoadingView(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Preview
@Composable
fun PreviewGenresScreen() {
    GenresScreen(
        genres = listOf(),
        isLoading = false,
        isFetchingMore = false,
        searchQuery = "",
        onEvent = {},
        modifier = Modifier
    )
}