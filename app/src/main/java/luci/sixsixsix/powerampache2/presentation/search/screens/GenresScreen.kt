package luci.sixsixsix.powerampache2.presentation.search.screens

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import luci.sixsixsix.powerampache2.presentation.search.GRID_ITEMS_ROW
import luci.sixsixsix.powerampache2.presentation.search.GRID_ITEMS_ROW_LAND
import luci.sixsixsix.powerampache2.presentation.search.GRID_ITEMS_ROW_MIN
import luci.sixsixsix.powerampache2.presentation.search.SearchViewEvent
import luci.sixsixsix.powerampache2.presentation.search.components.GenreListItem

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

    if (isLoading) {
        LoadingScreen()
    }

    Column(modifier = modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { localFocusManager.clearFocus() })
    }) {
        Spacer(modifier = Modifier.height(8.dp))
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                onEvent(SearchViewEvent.Refresh)
            }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(cardsPerRow)
            ) {
                items(
                    count = genres.size,
                    key = { i -> genres[i] })
                { i ->
                    val genre = genres[i]
                    GenreListItem(genre = genre) {
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