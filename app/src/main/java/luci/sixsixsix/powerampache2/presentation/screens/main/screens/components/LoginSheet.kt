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

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent
import luci.sixsixsix.powerampache2.ui.theme.surfaceContainerDark
import luci.sixsixsix.powerampache2.ui.theme.surfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomDrawer(
    username: String,
    password: String,
    url: String,
    authToken: String,
    sheetState: SheetState,
    isAllowAllCerts: Boolean,
    isLoginSheetOpen: MutableState<Boolean>,
    authTokenLoginEnabled: MutableState<Boolean>,
    onEvent: (AuthEvent) -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { isLoginSheetOpen.value = false },
        containerColor = surfaceContainerDark
    ) {
        LoginForm(
            username = username,
            password = password,
            url = url,
            authToken = authToken,
            isLoginSheetOpen = isLoginSheetOpen,
            authTokenLoginEnabled = authTokenLoginEnabled,
            isAllowAllCerts = isAllowAllCerts,
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
    isAllowAllCerts: Boolean,
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
                containerColor = surfaceDark
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
                isAllowAllCerts = isAllowAllCerts,
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
    isAllowAllCerts: Boolean,
    isLoginSheetOpen: MutableState<Boolean>,
    authTokenLoginEnabled: MutableState<Boolean>,
    onEvent: (AuthEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = surfaceDark)
            .padding(top = 6.dp, bottom = 16.dp)
    ) {
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
        if (Constants.config.enableTokenLogin) {
            AuthTokenCheckBox(authTokenLoginEnabled = authTokenLoginEnabled)
        }
        AllowAllCertsCheckBox(
            isAllowAllCerts = isAllowAllCerts,
        ) { newValue ->
            onEvent(AuthEvent.OnAllowAllCerts(newValue))
        }

        Spacer(modifier = Modifier.height(10.dp))
        LoginButton(
            onEvent = {
                isLoginSheetOpen.value = false
                onEvent(it)
            }
        )
    }
}

@Composable
fun AuthTokenCheckBox(
    authTokenLoginEnabled: MutableState<Boolean>,
    // onCheckedChange: ((Boolean) -> Unit),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = authTokenLoginEnabled.value,
            onCheckedChange = { authTokenLoginEnabled.value = it },
            enabled = true
        )
        Text(text = stringResource(id = R.string.loginScreen_useAuthToken))
    }
}

@Composable
fun AllowAllCertsCheckBox(
    isAllowAllCerts: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)
    ) {
    Row(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(checked = isAllowAllCerts, onCheckedChange = onCheckedChange)
        Spacer(Modifier.width(10.dp))
        Text(text = stringResource(id = R.string.loginScreen_acceptAllCertificates))
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview(heightDp = 500)
fun LoginFormPreview() {
    LoginForm(
        username = "state.username",
        password = "state.password",
        url = "state.url",
        authToken = "state.authToken",
        onEvent = {},
        isAllowAllCerts = true,
        isLoginSheetOpen = mutableStateOf(true),
        authTokenLoginEnabled = mutableStateOf(true)
    )
}
