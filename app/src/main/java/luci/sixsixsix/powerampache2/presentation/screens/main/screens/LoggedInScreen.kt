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
package luci.sixsixsix.powerampache2.presentation.screens.main.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.capitalizeWords
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.NavGraphs
import luci.sixsixsix.powerampache2.presentation.dialogs.IntroDialog
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthViewModel

import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.SheetDragHandle
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.SongDetailScreen
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedInScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel
) {
    val state = mainViewModel.state
    val offlineModeState by settingsViewModel.offlineModeStateFlow.collectAsState()

    val songState by mainViewModel.currentSongStateFlow().collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val errorMessageOffline = stringResource(id = R.string.error_offline)

    // if offline show fixed snackbar to allow user time to tap on the button
    val showSnackBarShowDuration = if (offlineModeState) {
        SnackbarDuration.Indefinite
    } else SnackbarDuration.Long

    // disable snackbar errors in offline mode
    if (state.errorMessage != "") {
        val offlineModeStr = stringResource(R.string.offlineMode).uppercase()
        LaunchedEffect(scaffoldState.snackbarHostState, state.errorMessage) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = state.errorMessage,
                actionLabel = if (state.errorMessage == errorMessageOffline) offlineModeStr else null,
                withDismissAction = true,
                duration = showSnackBarShowDuration
            ).apply {
                when (this) {
                    SnackbarResult.Dismissed ->
                        mainViewModel.onEvent(MainEvent.OnDismissUserMessage)
                    SnackbarResult.ActionPerformed ->
                        mainViewModel.onEvent(MainEvent.OnEnableOfflineMode)
                }
            }
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState, songState) {
        if (songState == null && scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            try {
                scaffoldState.bottomSheetState.hide()
            } catch (e: Exception) {
                L.e(e)
            }
        }
    }

    var introDialogVisible by remember { mutableStateOf(settingsViewModel.shouldShowIntroDialog()) }

    AnimatedVisibility(introDialogVisible) {
        IntroDialog {
            introDialogVisible = false
            settingsViewModel.onDismissIntroDialog()
        }
    }

    // This scaffold is used just for the bottom sheet
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SongDetailScreen(mainScaffoldState = scaffoldState, viewModel = mainViewModel)
        },
        sheetDragHandle = {
            AnimatedVisibility(
                visible = songState != null,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = shrinkOut() + slideOutVertically(),
            ) {
                SheetDragHandle(scaffoldState = scaffoldState, mainViewModel = mainViewModel)
            }
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = getPeakHeight(songState) // peek only when current song not null
    ) {
        Column {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                dependenciesContainerBuilder = {
                    // those are declared in the activity
                    dependency(mainViewModel)
                    dependency(authViewModel)
                    dependency(settingsViewModel)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding()
                    )
            )
        }
    }
}

@Composable
fun getPeakHeight(song: Song?): Dp = //TODO find a way to animate this (low-priority)
    if (song == null) { 0.dp } else { dimensionResource(id = R.dimen.miniPlayer_height) }
