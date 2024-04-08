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
package luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.screens.albums.components.AlbumItem
import luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail.components.ArtistDetailTopBar
import luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail.components.ArtistInfoSection
import luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail.components.ArtistInfoViewEvents

private const val GRID_ITEMS_ROW = 2
private const val GRID_ITEMS_ROW_LAND = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun ArtistDetailScreen(
    navigator: DestinationsNavigator,
    artistId: String,
    artist: Artist? = null,
    modifier: Modifier = Modifier,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val summaryOpen = remember { mutableStateOf(false) }
    var cardsPerRow by remember {
        mutableIntStateOf(if (state.albums.size < 2) { 1 } else { GRID_ITEMS_ROW })
    }
    // val albumCardSize = (LocalConfiguration.current.screenWidthDp / cardsPerRow).dp
    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }
    val isLandscape = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            cardsPerRow = GRID_ITEMS_ROW_LAND
            true
        }
        else -> {
            cardsPerRow = GRID_ITEMS_ROW
            false
        }
    }

    val placeholder = painterResource(id = R.drawable.img_album_detail_placeholder)
    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = state.artist.artUrl,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
//            error = painterResource(id = R.drawable.ic_image),
            contentDescription = state.artist.name
        )
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = state.artist.artUrl,
            contentScale = ContentScale.FillWidth,
            placeholder = placeholder,
//            error = painterResource(id = R.drawable.ic_image),
            contentDescription = state.artist.name,
        )
        // full screen view to add a transparent black layer on top
        // of the images for readability
        Box(modifier = Modifier
            .fillMaxSize()
            .alpha(0.4f)
            .background(brush = screenBackgroundGradient))

        if (state.isLoading) {
            LoadingScreen()
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                ArtistDetailTopBar(
                    navigator = navigator,
                    artist = state.artist,
                    isLoading = state.isLoading,
                    scrollBehavior = scrollBehavior
                ) { summaryOpen.value = !summaryOpen.value }
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(it)
                    .padding(top = dimensionResource(id = R.dimen.albumDetailScreen_top_padding))
                    .background(brush = albumBackgroundGradient),
                color = Color.Transparent
            ) {
                Column {
                    ArtistInfoSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 666.dp) // any big number
                            .padding(dimensionResource(R.dimen.albumDetailScreen_infoSection_padding)),
                        artist = state.artist,
                        summaryOpen = summaryOpen,
                        eventListener = { event ->
                            when(event) {
                                ArtistInfoViewEvents.SHARE_ARTIST -> { }
                            }
                        }
                    )

                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { viewModel.onEvent(ArtistDetailEvent.Fetch(state.artist.id)) }
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxSize(),
                            columns = GridCells.Fixed(cardsPerRow)
                        ) {
                            items(state.albums.size) { i ->
                                val album = state.albums[i]
                                AlbumItem(
                                    album = album,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navigator.navigate(
                                                AlbumDetailScreenDestination(album.id, album)
                                            )
                                        }
                                        .padding(10.dp)
                                )
                            }
                        }
                        if (state.isLoading && state.albums.isEmpty()) {
                            LoadingScreen()
                        }
                    }
                }
            }
        }
    }
}

private val albumBackgroundGradient
    @Composable
    get() =
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.75f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.65f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.62f),
            )

        )

private val screenBackgroundGradient
    @Composable
    get() =
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background
            )

        )


@Destination
@Composable
fun ArtistDetailScreen2(
    navigator: DestinationsNavigator,
    artistId: String,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(ArtistDetailEvent.Refresh) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.albums.size) { i ->
                    val album = state.albums[i]
                    AlbumItem(
                        album = album,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(AlbumDetailScreenDestination(album.id, album))
                            }
                            .padding(16.dp)
                    )

                    if (i < state.albums.size - 1) {
                        // if not last item add a divider
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}
