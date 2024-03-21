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
package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.MiniPlayer
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailTopBar

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SheetDragHandle(
    scaffoldState: BottomSheetScaffoldState,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val barHeight = dimensionResource(id = R.dimen.miniPlayer_height)

    Box(modifier = Modifier
        .height(dimensionResource(id = R.dimen.miniPlayer_height))
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
            AnimatedVisibility(visible = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded,
                enter = slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
            ) {
                SongDetailTopBar(mainViewModel = mainViewModel)
            }
            AnimatedVisibility(
                visible = scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded,
                enter = slideInVertically(initialOffsetY = { it / 2 },
                    animationSpec = spring(stiffness = Spring.StiffnessHigh)
                ),
                exit = fadeOut(spring(stiffness = Spring.StiffnessHigh)) + slideOutVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium))
            ) {
                MiniPlayer(mainViewModel = mainViewModel)
            }

//            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
//                SongDetailTopBar(mainViewModel = mainViewModel)
//            } else {
//                MiniPlayer(mainViewModel = mainViewModel)
//            }
        }
    }
}
