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
package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.ui.theme.additionalColours
import java.net.MalformedURLException
import java.net.URL

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SongDetailQueueDragHandle(
    song: Song?,
    lyrics: String,
    scaffoldState: BottomSheetScaffoldState,
    selectedTabIndex: MutableIntState,
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()
    val barHeight = dimensionResource(id = R.dimen.queue_dragHandle_height)

    Box(modifier = Modifier
        .height(barHeight)
        .fillMaxWidth()
    ) {
        // show mini-player
        Box(modifier = Modifier
            .height(barHeight)
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                        scaffoldState.bottomSheetState.partialExpand() //only peek
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }
        ) {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                SongDetailQueueTopBar(
                    song = song,
                    lyrics = lyrics,
                    scaffoldState = scaffoldState,
                    pagerState = pagerState,
                    selectedTabIndex = selectedTabIndex
                )
            } else {
                SongDetailQueueTopBar(
                    song = song,
                    lyrics = lyrics,
                    scaffoldState = scaffoldState,
                    showCloseIcon = false,
                    pagerState = pagerState,
                    selectedTabIndex = selectedTabIndex
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailQueueTopBar(
    song: Song?,
    lyrics: String,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    scaffoldState: BottomSheetScaffoldState,
    selectedTabIndex: MutableIntState,
    showCloseIcon: Boolean = true
) {
    val arrowWidth = dimensionResource(id = R.dimen.songDetail_handle_arrow_width)
    Card(
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.additionalColours.queueHandle
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row {
            AnimatedVisibility(visible = showCloseIcon) {
                DragArrowIcon(modifier = modifier.width(arrowWidth), showCloseIcon)
            }
            AnimatedVisibility(visible = !showCloseIcon) {
                DragArrowIcon(modifier = modifier.width(arrowWidth), showCloseIcon)
            }

            SongHandleTabRow(
                modifier = Modifier.weight(1f),
                song = song,
                lyrics = lyrics,
                scaffoldState = scaffoldState,
                pagerState = pagerState,
                selectedTabIndex = selectedTabIndex
            )

            Spacer(Modifier.width(arrowWidth))
        }
    }
}

@Composable
fun DragArrowIcon(
    modifier: Modifier = Modifier,
    showCloseIcon: Boolean
) {
    val icon = if(showCloseIcon)Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp
    Icon(
        modifier = modifier
            .fillMaxHeight()
            .padding(dimensionResource(id = R.dimen.close_handle_icon_padding)),
        imageVector = icon,
        contentDescription = "open/close song queue screen",
        tint = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabbedSongDetailView(
    lyrics: String,
    pagerState: PagerState,
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    Column {
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .fillMaxWidth()
                .weight(1.0f)
        ) { index ->
            when(index) {
                1 -> {
                    if (isValidUrl(lyrics)) {
                        WebPageView(lyrics, modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()))
                    } else {
                        val spannedText = HtmlCompat.fromHtml(lyrics, 0)
                        Text(
                            fontSize = 20.sp,
                            text = spannedText.toString(),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
                else -> {
                    SongDetailQueueScreenContent(
                        mainScaffoldState = mainScaffoldState,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }
    }
}

private fun isValidUrl(url: String): Boolean = try {
    URL(url)
    true
} catch (e: MalformedURLException) { false }


@Composable
fun WebPageView(url: String, modifier: Modifier = Modifier) {
    val currentUrl by rememberUpdatedState(newValue = url)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK // Uses cache if available, else network
                settings.domStorageEnabled = true // Enables DOM storage API (optional but recommended)
                loadUrl(url)
            }
        },
        update = {
            if (currentUrl.isNotBlank() && it.url?.toString() != currentUrl) {
                it.loadUrl(currentUrl)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongHandleTabRow(
    modifier: Modifier = Modifier,
    song: Song?,
    lyrics: String,
    scaffoldState: BottomSheetScaffoldState,
    pagerState: PagerState,
    selectedTabIndex: MutableIntState
) {
    LaunchedEffect(selectedTabIndex.value) {
        pagerState.animateScrollToPage(selectedTabIndex.value)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex.value = pagerState.currentPage
        }
    }

    val scope = rememberCoroutineScope()
    val textColour = MaterialTheme.colorScheme.onSurface
    TabRow(
        indicator = {

        },
        modifier = modifier,
        selectedTabIndex = selectedTabIndex.value,
        contentColor = textColour,
        containerColor = Color.Transparent
    ) {
        Tab(
            unselectedContentColor = textColour.copy(alpha = 0.66f),
            selected = selectedTabIndex.value == 0,
            onClick = {
                scope.launch {
                    // if we're in the tab that is selected just close the drawer
                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded &&
                        selectedTabIndex.value == 0) {
                        scaffoldState.bottomSheetState.partialExpand()
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                    selectedTabIndex.value = 0
                }
            },
            text = {
                Text(
                    text = stringResource(id = R.string.player_queue_upNext),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
        )
        if (lyrics != "") {
            Tab(
                unselectedContentColor = textColour.copy(alpha = 0.66f),
                selected = selectedTabIndex.value == 1,
                onClick = {
                    scope.launch {
                        // if we're in the tab that is selected just close the drawer
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded &&
                            selectedTabIndex.value == 1
                        ) {
                            scaffoldState.bottomSheetState.partialExpand()
                        } else {
                            scaffoldState.bottomSheetState.expand()
                        }
                        selectedTabIndex.value = 1
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.player_queue_lyrics),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}
