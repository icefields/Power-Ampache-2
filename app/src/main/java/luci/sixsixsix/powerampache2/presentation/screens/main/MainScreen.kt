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
package luci.sixsixsix.powerampache2.presentation.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.LOGIN_SCREEN_TIMEOUT
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.LoggedInScreen
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.LoginScreen
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.LoadingShimmerScreen
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsEvent
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel
) {
    val session by authViewModel.sessionStateFlow.collectAsState()
    val user by authViewModel.userStateFlow.collectAsState()
    val offlineModeState by settingsViewModel.offlineModeStateFlow.collectAsState()
    var offlineModeSwitchVisible by remember { mutableStateOf(false) }
    val shouldShowLoginScreen = !(session != null || offlineModeState)
    var loginScreenVisible by remember { mutableStateOf(shouldShowLoginScreen) }

    LaunchedEffect(offlineModeSwitchVisible) {
        // wait 2 seconds before showing the switch
        delay(LOGIN_SCREEN_TIMEOUT)
        offlineModeSwitchVisible = user != null
    }

    LaunchedEffect(shouldShowLoginScreen) {
        // wait 2 seconds before showing the switch
        delay(500)
        // show login screen with a delay to allow time to the token to refresh
        loginScreenVisible = shouldShowLoginScreen
    }

    if(authViewModel.state.isLoading && !offlineModeState) {
        LoadingScreenWithOfflineSwitch(
            offlineModeSwitchVisible = offlineModeSwitchVisible,
            offlineModeState = offlineModeState
        ) {
            settingsViewModel.onEvent(SettingsEvent.OnOfflineToggle)
        }
    } else {
        offlineModeSwitchVisible = false
        if (session != null || offlineModeState) {
            LoggedInScreen(mainViewModel, authViewModel, settingsViewModel)
            loginScreenVisible = false
        } else {
            if (loginScreenVisible && shouldShowLoginScreen) {
                LoginScreen(viewModel = authViewModel)
            }
        }
    }
}

/**
 * LOADING SCREEN WITH OFFLINE SWITCH
 */
@Composable
private fun LoadingScreenWithOfflineSwitch(
    offlineModeSwitchVisible: Boolean,
    offlineModeState: Boolean,
    onSwitchToggle: (newValue: Boolean) -> Unit
) = Box(
        contentAlignment = Alignment.TopEnd
    ) {
        AnimatedVisibility(offlineModeSwitchVisible) {
            LoadingScreenOfflineSwitch(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(),
                offlineModeEnabled = offlineModeState,
                onSwitchToggle = onSwitchToggle
            )
        }
        LoadingShimmerScreen()
    }


@Composable
private fun LoadingScreenOfflineSwitch(
    modifier: Modifier,
    offlineModeEnabled: Boolean,
    onSwitchToggle: (newValue: Boolean) -> Unit
) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(id = R.string.offlineMode_switch_title),
            fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Switch(
            checked = offlineModeEnabled,
            onCheckedChange = onSwitchToggle,
            enabled = true
        )
    }
}