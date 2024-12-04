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
package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.hasLyrics
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailContent
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailQueueDragHandle
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.TabbedSongDetailView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val song by viewModel.currentSongStateFlow().collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val pagerState = rememberPagerState(initialPage = 0) {
        if (song?.hasLyrics() == true) { 2 } else { 1 }
    }
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    val barHeight = dimensionResource(id = R.dimen.queue_dragHandle_height)
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            TabbedSongDetailView(
                song = song,
                pagerState = pagerState,
                mainScaffoldState = mainScaffoldState,
                mainViewModel = viewModel
            )
        },
        sheetDragHandle = {
            SongDetailQueueDragHandle(
                song = song,
                scaffoldState = scaffoldState,
                selectedTabIndex = selectedTabIndex,
                pagerState = pagerState)
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = barHeight
    ) {
        SongDetailContent(
            mainScaffoldState = mainScaffoldState,
            modifier = Modifier.padding(paddingValues = it),
            mainViewModel = viewModel,
            addToPlaylistOrQueueDialogViewModel = addToPlaylistOrQueueDialogViewModel
        )
    }
}
