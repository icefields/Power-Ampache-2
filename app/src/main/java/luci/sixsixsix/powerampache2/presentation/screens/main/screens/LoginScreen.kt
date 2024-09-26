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

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.data.Servers
import luci.sixsixsix.powerampache2.presentation.common.DefaultFullWidthButton
import luci.sixsixsix.powerampache2.presentation.common.DownloadFullVersionButton
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthViewModel
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.LoginBottomDrawer
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.LoginButton
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.LoginDialog
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.SignUpBottomDrawer
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.SignUpDialog

@Composable
@Destination(start = false)
fun LoginScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    val error by viewModel.messagesStateFlow.collectAsState()

    LoginScreenContent(
        username = state.username,
        password = state.password,
        url = state.url,
        authToken = state.authToken,
        error = error,
        onEvent = {
            viewModel.onEvent(it)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    username: String,
    password: String,
    url: String,
    authToken: String,
    error: String,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    val isLoginSheetOpen = remember { mutableStateOf(false) }
    val isSignUpSheetOpen = remember { mutableStateOf(false) }
    var isDebugButtonsSheetOpen by remember { mutableStateOf(false) }
    val authTokenLoginEnabled = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.surfaceDark))
    ) {
        Image(
            modifier = Modifier
                //.fillMaxWidth(0.8f)
                .fillMaxHeight(0.4f)
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp)
                .clickable {
                    if (BuildConfig.DEBUG) {
                        isDebugButtonsSheetOpen = !isDebugButtonsSheetOpen
                    }
                },
            contentScale = ContentScale.FillHeight,
            painter = painterResource(id = R.drawable.img_power_ampache_logo_login),
            contentDescription = "Power Ampache Logo"
        )

        Icon(
            tint = colorResource(id = R.color.onBackgroundDark),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(top = 1.dp, bottom = 10.dp),
            painter = painterResource(id = R.drawable.powerampache_title),
            contentDescription = "Power Ampache Title"
        )

        if (!error.isNullOrBlank()) {
            ErrorView(
                error = error,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom) {

            items(7) {
                when(it) {
                    1 -> { }
                    2 -> LoginButton {
                        isLoginSheetOpen.value = true
                        // DO NOT call onEvent(AuthEvent.Login), This is just
                        // for opening the drawer
                    }
                    3 -> SignUpButton {
                        isSignUpSheetOpen.value = !isSignUpSheetOpen.value
                        // DO NOT call onEvent(AuthEvent.SignUp), This is just
                        // for opening the drawer
                    }
                    4 -> if (BuildConfig.ENABLE_OFFICIAL_DEMO_SERVER) {
                        DebugLoginButton(
                            server = Servers.AmpacheDemo,
                            buttonText = R.string.loginScreen_demo_server,
                            onEvent = onEvent
                        )
                    }
                    6 -> if (BuildConfig.ENABLE_DOGMAZIC_DEMO_SERVER) {
                        DebugLoginButton(
                            server = Servers.Dogmazic,
                            buttonText = R.string.loginScreen_dogmazic_server,
                            onEvent = onEvent
                        )
                    }
                    5 -> if (BuildConfig.DEMO_VERSION) {
                        DownloadFullVersionButton()
                    }
                    7 -> LoginScreenFooter(modifier = Modifier.padding(top = 16.dp))
                    else -> { }
                }
            }
        }
    }

    // use bottom drawer or dialog
    val showBottomDrawer = Build.VERSION.SDK_INT > Build.VERSION_CODES.Q &&
            !Constants.config.forceLoginDialogsOnAllVersions

    if (isLoginSheetOpen.value) {
        if (showBottomDrawer) {
            LoginBottomDrawer(
                username = username,
                password = password,
                url = url,
                authToken = authToken,
                sheetState = sheetState,
                isLoginSheetOpen = isLoginSheetOpen,
                authTokenLoginEnabled = authTokenLoginEnabled,
                onEvent = onEvent
            )
        } else {
            LoginDialog(
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

    if (isSignUpSheetOpen.value) {
        if (showBottomDrawer) {
            SignUpBottomDrawer(
                sheetState = sheetState,
                isSignUpSheetOpen = isSignUpSheetOpen,
                onEvent = onEvent
            )
        } else {
            SignUpDialog(isSignUpSheetOpen = isSignUpSheetOpen, onEvent = onEvent)
        }
    }

    if (isDebugButtonsSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isDebugButtonsSheetOpen = false }
        ) {
            Column {
                DebugLoginButtons(onEvent, modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth())
            }
        }
    }
}

@Composable
fun LoginScreenFooter(
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier)
}

@Composable
fun DebugLoginButtons(
    onEvent: (luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DebugLoginButton(
            server = Servers.RemoteDebug,
            buttonText = R.string.loginScreen_remote_server,
            onEvent = onEvent
        )
        DebugLoginButton(
            server = Servers.NextcloudLocal,
            buttonText = R.string.loginScreen_local_nextcloud,
            onEvent = onEvent
        )
        DebugLoginButton(
            server = Servers.AmpacheDemo,
            buttonText = R.string.loginScreen_demo_server,
            onEvent = onEvent
        )
        DebugLoginButton(
            server = Servers.LocalDev,
            buttonText = R.string.loginScreen_local_server,
            onEvent = onEvent
        )
    }
}

@Composable
fun SignUpButton(
    onClick: () -> Unit
) {
    DefaultFullWidthButton(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal),
                vertical = 10.dp
            )
            .fillMaxWidth(),
        borderStrokeColour = colorResource(id = R.color.loginScreen_signUpButton_stroke),
        colours = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.loginScreen_signUpButton_background),
            contentColor = colorResource(id = R.color.loginScreen_signUpButton_foreground)
        ),
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Default.PersonAddAlt, contentDescription = "Sign Up")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp, horizontal = 9.dp),
            text = stringResource(id = R.string.loginScreen_signup),
            textAlign = TextAlign.Center,
            //fontWeight = FontWeight.SemiBold,
            fontSize = fontDimensionResource(id = R.dimen.button_login_text_size)
        )
    }
}

@Composable
fun DebugLoginButton(
    server: Servers,
    onEvent: (AuthEvent) -> Unit,
    @StringRes buttonText: Int
) {
    DefaultFullWidthButton(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal),
                vertical = 10.dp
            )
            .fillMaxWidth(),
        onClick = {
            onEvent(AuthEvent.OnChangeServerUrl(server.url))
            onEvent(AuthEvent.OnChangePassword(server.password))
            onEvent(AuthEvent.OnChangeUsername(server.user))
            onEvent(AuthEvent.OnChangeAuthToken(server.apiKey))
            onEvent(AuthEvent.Login)
        },

        colours = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.loginScreen_demoButton_background),
            contentColor = colorResource(id = R.color.loginScreen_demoButton_foreground)
        )
    ) {
        Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Demo Server login")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp),
            text = stringResource(id = buttonText),
            textAlign = TextAlign.Center,
            //fontWeight = FontWeight.SemiBold,
            fontSize = fontDimensionResource(id = R.dimen.button_login_text_size)
        )
    }
}

@Composable
fun AuthTokenCheckBox(
    authTokenLoginEnabled: MutableState<Boolean>,
   // onCheckedChange: ((Boolean) -> Unit),
    modifier: Modifier = Modifier
) {
    //var authTokenCheckBoxChecked by remember { mutableStateOf(authTokenLoginEnabled) }

    Row(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = authTokenLoginEnabled.value,
            onCheckedChange = {
                authTokenLoginEnabled.value = it
                //onCheckedChange(it)
            },
            enabled = true
        )
        Text(text = stringResource(id = R.string.loginScreen_useAuthToken))
    }
}

@Composable
fun ErrorView(
    error: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .wrapContentHeight()
        .background(MaterialTheme.colorScheme.errorContainer)
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = error,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = fontDimensionResource(id = R.dimen.button_login_text_size)
        )
    }
}

@Composable
@Preview(heightDp = 500)
fun LoginScreenPreview() {
    LoginScreenContent(
        username = "state.username",
        password = "state.password",
        url = "state.url",
        authToken = "state.authToken",
        error = " re stufferror message /n more stufferror message /n more stufferror message /n more stufferror message /n more stuff",
        onEvent = {},
        //isLoginSheetOpen = true,
        //isSignUpSheetOpen = false,
        modifier = Modifier.fillMaxSize()
    )
}
