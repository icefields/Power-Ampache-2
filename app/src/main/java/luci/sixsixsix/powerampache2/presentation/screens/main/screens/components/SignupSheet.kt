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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.presentation.common.DefaultFullWidthButton
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpBottomDrawer(
    sheetState: SheetState,
    isSignUpSheetOpen: MutableState<Boolean>,
    onEvent: (luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent) -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { isSignUpSheetOpen.value = false },
        containerColor = colorResource(id = R.color.surfaceContainerDark)
    ) {
        SignUpBottomSheet(
            onEvent = onEvent,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        )
    }
}

@Composable
fun SignUpDialog(
    isSignUpSheetOpen: MutableState<Boolean>,
    onEvent: (luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent) -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { isSignUpSheetOpen.value = false }
    )  {
        Card(
//            border = BorderStroke(
//                width = 0.dp,
//                color = MaterialTheme.colorScheme.onSurface
//            ),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.surfaceContainerDark)
            ),
            //modifier = Modifier.padding(top = 16.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            SignUpBottomSheet(
                onEvent = onEvent,
                modifier = Modifier
                    .padding(top = 16.dp)
                    //.verticalScroll(rememberScrollState())
                    .wrapContentHeight()
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun SignUpBottomSheet(
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_SERVER_URL) }

    var fullName by rememberSaveable { mutableStateOf("") }

    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }
    var emailErrorMessage by rememberSaveable { mutableStateOf("") }
    var urlErrorMessage by rememberSaveable { mutableStateOf("") }
    var usernameErrorMessage by rememberSaveable { mutableStateOf("") }
    var repeatPasswordErrorMessage by rememberSaveable { mutableStateOf("") }

    val requiredFieldMessage = stringResource(id = R.string.loginScreen_signUp_requiredField)
    val serverUrlVisible = BuildConfig.DEFAULT_SERVER_URL.isBlank()

    LazyColumn(
        modifier = modifier
            .background(color = colorResource(id = R.color.surfaceDark))
            .padding(horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal))
    ) {
        items(10) { index ->
            when(index) {
                // URL
                0 -> if (serverUrlVisible) {
                    OutlinedTextField(
                        value = url,
                        onValueChange = {
                            url = it
                            if (it.isBlank()) {
                                urlErrorMessage = requiredFieldMessage
                            } else {
                                urlErrorMessage = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth().padding(bottom = 8.dp),
                        label = {
                            Text(text = stringResource(id = R.string.loginScreen_server_url))
                        },
                        supportingText = {
                            if (urlErrorMessage.isNotBlank()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = urlErrorMessage,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        maxLines = 1,
                        isError = urlErrorMessage.isNotBlank(),
                        singleLine = true
                    )
                }
                // USERNAME
                1 -> OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        if (it.isBlank()) {
                            usernameErrorMessage = requiredFieldMessage
                        } else {
                            usernameErrorMessage = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth().padding(bottom = 8.dp),
                    label = { Text(text = stringResource(id = R.string.loginScreen_username)) },
                    supportingText = {
                        if (usernameErrorMessage.isNotBlank()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = usernameErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    maxLines = 1,
                    isError = usernameErrorMessage.isNotBlank(),
                    singleLine = true
                )
                // EMAIL
                2 -> OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (it.isBlank()) {
                            emailErrorMessage = requiredFieldMessage
                        } else {
                            emailErrorMessage = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth().padding(bottom = 8.dp),
                    label = { Text(text = stringResource(id = R.string.loginScreen_signUp_email)) },
                    maxLines = 1,
                    isError = emailErrorMessage.isNotBlank(),
                    supportingText = {
                        if (emailErrorMessage.isNotBlank()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = emailErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true
                )
                // FULL NAME
                3 -> OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp),
                    label = { Text(text = stringResource(id = R.string.loginScreen_fullname)) },
                    maxLines = 1,
                    singleLine = true
                )
                // PASSWORD
                4 -> OutlinedTextField(
                    value = password,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    onValueChange = {
                        password = it
                        if (it.isBlank()) {
                            passwordErrorMessage = requiredFieldMessage
                        } else {
                            passwordErrorMessage = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth().padding(bottom = 6.dp, top = 6.dp),
                    label = { Text(text = stringResource(id = R.string.loginScreen_password)) },
                    maxLines = 1,
                    isError = passwordErrorMessage.isNotBlank(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        }
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    supportingText = {
                        if (passwordErrorMessage.isNotBlank()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = passwordErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                // REPEAT PASSWORD
                5 -> OutlinedTextField(
                    value = repeatPassword,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    onValueChange = {
                        repeatPassword = it
                        repeatPasswordErrorMessage = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.loginScreen_password_repeat)) },
                    maxLines = 1,
                    isError = repeatPasswordErrorMessage.isNotBlank(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        }
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    supportingText = {
                        if (repeatPasswordErrorMessage.isNotBlank()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = repeatPasswordErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                // BUTTON
                6 -> DefaultFullWidthButton(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    colours = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primaryDark),
                        contentColor = colorResource(id = R.color.onPrimaryDark)
                    ),
                    onClick = {
                        val isError =
                            password.isBlank().also {
                                if (it) {
                                    passwordErrorMessage = requiredFieldMessage
                                }
                            } or
                                password.isBlank()
                                    .also { if (it) passwordErrorMessage = requiredFieldMessage } or
                                username.isBlank()
                                    .also { if (it) usernameErrorMessage = requiredFieldMessage } or
                                email.isBlank()
                                    .also { if (it) emailErrorMessage = requiredFieldMessage } or
                                url.isBlank()
                                    .also { if (it) urlErrorMessage = requiredFieldMessage } or
                                (password != repeatPassword).also {
                                    if (it) repeatPasswordErrorMessage =
                                        "The 2 passwords do not match"
                                }

                        if (!isError) {
                            onEvent(
                                AuthEvent.SignUp(
                                    serverUrl = url,
                                    username = username,
                                    password = password,
                                    fullName = fullName,
                                    email = email
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAddAlt,
                        contentDescription = stringResource(id = R.string.loginScreen_signup)
                    )
                    Text(
                        modifier = Modifier
                            .padding(vertical = 9.dp, horizontal = 9.dp),
                        text = stringResource(id = R.string.loginScreen_signup),
                        textAlign = TextAlign.Center,
                        fontSize = fontDimensionResource(id = R.dimen.button_login_text_size)
                    )
                }
                // SPACER
                7 -> Spacer(modifier = Modifier.height(16.dp))
                else -> {}
            }
        }
    }
}
