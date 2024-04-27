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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
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
                    //isLoginSheetOpen.value = false
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
        if (BuildConfig.ENABLE_TOKEN_LOGIN) {
            AuthTokenCheckBox(authTokenLoginEnabled = authTokenLoginEnabled)
        }
        LoginTextFields(
            username = username,
            password = password,
            url = url,
            authToken = authToken,
            authTokenLoginEnabled = authTokenLoginEnabled.value,
            onEvent = onEvent,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        LoginButton(onEvent = {
            isLoginSheetOpen.value = false
            onEvent(it)
        })
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoginTextFields(
    username: String,
    password: String,
    url: String,
    authToken: String,
    authTokenLoginEnabled: Boolean,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal))
    ) {
        if (BuildConfig.SHOW_LOGIN_SERVER_VERSION_WARNING) {
            Text(
                modifier = Modifier.basicMarquee().padding(top = 2.dp),
                text = "Compatible with server versions 4 and above.\nTested on server version 6",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Light,
                color = colorResource(id = R.color.onSurfaceVariantDark)
            )
        }

        LoginTextField(
            value = url,
            label = R.string.loginScreen_server_url
        ) { onEvent(AuthEvent.OnChangeServerUrl(it)) }

        AnimatedVisibility(visible = !authTokenLoginEnabled) {
            Column {
                LoginTextField(
                    value = username,
                    label = R.string.loginScreen_username
                ) { onEvent(luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent.OnChangeUsername(it)) }

                LoginTextField(
                    value = password,
                    label = R.string.loginScreen_password,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        }
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description)
                        }
                    }
                ) { onEvent(AuthEvent.OnChangePassword(it)) }
            }
        }

        AnimatedVisibility(visible = authTokenLoginEnabled) {
            LoginTextField(
                value = authToken,
                label = R.string.loginScreen_auth_token
            ) { onEvent(AuthEvent.OnChangeAuthToken(it)) }
        }
    }
}
