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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.models.SongUI
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailContent
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailQueueDragHandle
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.TabbedSongDetailView
import androidx.compose.runtime.collectAsState

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    songDetailViewModel: SongDetailViewModel = hiltViewModel(),
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val song by viewModel.currentSongStateFlow().collectAsStateWithLifecycle()
    val lyricsTag by songDetailViewModel.lyrics.collectAsStateWithLifecycle()
    val pluginLyrics by songDetailViewModel.pluginLyrics.collectAsStateWithLifecycle()
    val pluginInfo by songDetailViewModel.pluginInfo.collectAsStateWithLifecycle()
    val isChromecastPluginInstalled by songDetailViewModel.isChromecastPluginInstalledStateFlow.collectAsStateWithLifecycle()

    var rememberSongId by remember { mutableStateOf(song?.id ?: "") }

    song?.let {
        val isNewSong = rememberSongId != it.id
        if (isNewSong) {
            rememberSongId = it.id
            songDetailViewModel.onNewSong(it)
        }
    }

    // if tagged lyrics are present they take priority over plugin lyrics
    val lyrics = if (lyricsTag.isNotBlank()) lyricsTag else pluginLyrics

    val scaffoldState = rememberBottomSheetScaffoldState()
    val pagerState = rememberPagerState(initialPage = 0) {
        if (lyrics != "") { 2 } else { 1 }
    }
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    val queuePosStr = getQueuePositionStr(
        // TODO: the line below was missing collectAsState(), which was an error. Change needs testing/confirmation
        currentQueue = viewModel.currentQueue().collectAsState().value,
        // currentQueuePosition is USER FACING: start from 1, not zero
        currentQueuePosition = viewModel.currentQueuePosition() + 1,
        isScreenOpen = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
        )
    val upNextText = "${stringResource(id = R.string.player_queue_upNext)} $queuePosStr"
    val barHeight = dimensionResource(id = R.dimen.queue_dragHandle_height)
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            TabbedSongDetailView(
                lyrics = lyrics,
                pagerState = pagerState,
                mainScaffoldState = mainScaffoldState,
                mainViewModel = viewModel
            )
        },
        sheetDragHandle = {
            SongDetailQueueDragHandle(
                song = song,
                lyrics = lyrics,
                scaffoldState = scaffoldState,
                selectedTabIndex = selectedTabIndex,
                upNextText = upNextText,
                pagerState = pagerState
            )
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = barHeight
    ) {
        SongDetailContent(
            mainScaffoldState = mainScaffoldState,
            modifier = Modifier.padding(paddingValues = it),
            mainViewModel = viewModel,
            pluginSong = pluginInfo,
            isChromecastPluginInstalled = isChromecastPluginInstalled,
            addToPlaylistOrQueueDialogViewModel = addToPlaylistOrQueueDialogViewModel
        )
    }
}

private fun getQueuePositionStr(currentQueue: List<SongUI>, currentQueuePosition: Int, isScreenOpen: Boolean) =
    if (
        currentQueuePosition > 0
        && currentQueue.isNotEmpty()
        && currentQueuePosition <= currentQueue.size
        && isScreenOpen
    ) "[$currentQueuePosition/${currentQueue.size}]"
    else ""
