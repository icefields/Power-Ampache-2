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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailContent
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailQueueDragHandle
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.TabbedSongDetailView

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
    val song by viewModel.currentSongStateFlow().collectAsState()
    val lyricsTag by songDetailViewModel.lyrics.collectAsStateWithLifecycle()
    val pluginLyrics by songDetailViewModel.pluginLyrics.collectAsStateWithLifecycle()
    val pluginInfo by songDetailViewModel.pluginInfo.collectAsStateWithLifecycle()

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
            pluginSong = pluginInfo,
            addToPlaylistOrQueueDialogViewModel = addToPlaylistOrQueueDialogViewModel
        )
    }
}
