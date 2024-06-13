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
package luci.sixsixsix.powerampache2.presentation.screens.main.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.AuthTokenCheckBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomDrawer(
    username: String,
    password: String,
    url: String,
    authToken: String,
    sheetState: SheetState,
    isLoginSheetOpen: MutableState<Boolean>,
    authTokenLoginEnabled: MutableState<Boolean>,
    onEvent: (AuthEvent) -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { isLoginSheetOpen.value = false },
        containerColor = colorResource(id = R.color.surfaceContainerDark)
    ) {
        LoginForm(
            username = username,
            password = password,
            url = url,
            authToken = authToken,
            isLoginSheetOpen = isLoginSheetOpen,
            authTokenLoginEnabled = authTokenLoginEnabled,
            onEvent = onEvent
        )
    }
}

@Composable
fun LoginDialog(
    username: String,
    password: String,
    url: String,
    authToken: String,
    isLoginSheetOpen: MutableState<Boolean>,
    authTokenLoginEnabled: MutableState<Boolean>,
    onEvent: (AuthEvent) -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { isLoginSheetOpen.value = false }
    )  {
        Card(
//            border = BorderStroke(
//                width = 0.dp,
//                color = MaterialTheme.colorScheme.onSurface
//            ),
            modifier = Modifier,
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.surfaceDark)
            ),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            LoginForm(
                username = username,
                password = password,
                url = url,
                authToken = authToken,
                isLoginSheetOpen = isLoginSheetOpen,
                authTokenLoginEnabled = authTokenLoginEnabled,
                onEvent = {
                    onEvent(it)
                }
            )
        }
    }
}

@Composable
private fun LoginForm(
    username: String,
    password: String,
    url: String,
    authToken: String,
    isLoginSheetOpen: MutableState<Boolean>,
    authTokenLoginEnabled: MutableState<Boolean>,
    onEvent: (AuthEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = colorResource(id = R.color.surfaceDark))
            .padding(top = 6.dp, bottom = 16.dp)
    ) {
        if (Constants.config.enableTokenLogin) {
            AuthTokenCheckBox(authTokenLoginEnabled = authTokenLoginEnabled)
        }
        LoginTextFields(
            username = username,
            password = password,
            url = url,
            serverUrlVisible = BuildConfig.DEFAULT_SERVER_URL.isBlank(),
            authToken = authToken,
            authTokenLoginEnabled = authTokenLoginEnabled.value,
            onEvent = onEvent,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        LoginButton(
            onEvent = {
                isLoginSheetOpen.value = false
                onEvent(it)
            }
        )
    }
}
