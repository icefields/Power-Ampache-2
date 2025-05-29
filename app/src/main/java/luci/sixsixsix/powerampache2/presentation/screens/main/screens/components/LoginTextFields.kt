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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent

@Composable
fun LoginTextFields(
    username: String,
    password: String,
    url: String,
    authToken: String,
    authTokenLoginEnabled: Boolean,
    serverUrlVisible: Boolean = true,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal))
    ) {
        // do not show warning for pre-set url clients
        val warningText = if (BuildConfig.SHOW_LOGIN_SERVER_VERSION_WARNING && serverUrlVisible && !authTokenLoginEnabled) {
            Constants.config.loginWarning.ifBlank {
                stringResource(id = R.string.loginScreen_textFields_warning)
            }
        } else if (authTokenLoginEnabled && serverUrlVisible) {
            stringResource(id = R.string.loginScreen_auth_token_warning)
        } else ""

        Text(
            modifier = Modifier.padding(top = 2.dp).clickable { },
            text = warningText,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Light,
            color = colorResource(id = R.color.onSurfaceVariantDark)
        )

        if (serverUrlVisible) {
            LoginTextField(
                value = url,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                label = R.string.loginScreen_server_url
            ) { onEvent(AuthEvent.OnChangeServerUrl(it)) }
        }

        AnimatedVisibility(visible = !authTokenLoginEnabled) {
            Column {
                LoginTextField(
                    value = username,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    label = R.string.loginScreen_username
                ) { onEvent(AuthEvent.OnChangeUsername(it)) }

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
