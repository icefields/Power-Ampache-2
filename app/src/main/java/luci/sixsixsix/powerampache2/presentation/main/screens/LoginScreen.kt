package luci.sixsixsix.powerampache2.presentation.main.screens

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.data.Servers
import luci.sixsixsix.powerampache2.presentation.common.DefaultFullWidthButton
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.main.AuthViewModel
import luci.sixsixsix.powerampache2.presentation.main.screens.components.SignUpBottomSheet

val bottomDrawerPaddingHorizontal = 26.dp

@Composable
@Destination(start = false)
fun LoginScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    LoginScreenContent(
        username = state.username,
        password = state.password,
        url = state.url,
        authToken = state.authToken,
        error = state.error,
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
    modifier: Modifier = Modifier,
    isLoginSheetOpen:Boolean = false,
    isSignUpSheetOpen:Boolean = false
) {
    val sheetState = rememberModalBottomSheetState()
    var isLoginSheetOpen by rememberSaveable { mutableStateOf(isLoginSheetOpen) }
    var isSignUpSheetOpen by rememberSaveable { mutableStateOf(isSignUpSheetOpen) }
    var isDebugButtonsSheetOpen by rememberSaveable { mutableStateOf(false) }
    val authTokenLoginEnabled = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {

        Icon(
            tint = MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
                .clickable {
                    if (BuildConfig.DEBUG) {
                        isDebugButtonsSheetOpen = !isDebugButtonsSheetOpen
                    }
                },
            painter = painterResource(id = R.drawable.ic_power_ampache_mono),
            contentDescription = "Power Ampache Logo"
        )

        Icon(
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                //.padding(horizontal = 40.dp)
                .padding(top = 1.dp, bottom = 10.dp),
            painter = painterResource(id = R.drawable.powerampache_title),
            contentDescription = "Power Ampache Title"
        )

        if (!error.isNullOrBlank()) {
            ErrorView(
                error = error,
                modifier = modifier.fillMaxWidth()
            )
        }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom) {

            items(7) {
                when(it) {
                    1 -> { }
                    2 -> LoginButton {
                        isLoginSheetOpen = true
                        // DO NOT call onEvent(AuthEvent.Login), This is just
                        // for opening the drawer
                    }
                    3 -> SignUpButton {
                        isSignUpSheetOpen = !isSignUpSheetOpen
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
                    5 -> if (BuildConfig.ENABLE_DOGMAZIC_DEMO_SERVER) {
                        DebugLoginButton(
                            server = Servers.Dogmazic,
                            buttonText = R.string.loginScreen_dogmazic_server,
                            onEvent = onEvent
                        )
                    }
                    6 -> LoginScreenFooter(modifier = Modifier.padding(top = 16.dp))
                    else -> { }
                }
            }
        }
    }

    if (isLoginSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isLoginSheetOpen = false }
        ) {
            Column(
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
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
                LoginButton(onEvent = {
                    isLoginSheetOpen = false
                    onEvent(it)
                })
            }
        }
    }

    if (isSignUpSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSignUpSheetOpen = false }
        ) {
            SignUpBottomSheet(
                onEvent = onEvent,
                modifier = Modifier.wrapContentHeight().fillMaxWidth()
            )
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
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DebugLoginButton(
            server = Servers.RemoteDebug,
            buttonText = R.string.loginScreen_remote_server,
            onEvent = onEvent
        )
        DebugLoginButton(
            server = Servers.Dogmazic,
            buttonText = R.string.loginScreen_dogmazic_server,
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
fun LoginTextFields(
    username: String,
    password: String,
    url: String,
    authToken: String,
    authTokenLoginEnabled: Boolean,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier
) {
    val topPaddingInputFields = 8.dp
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = bottomDrawerPaddingHorizontal)
    ) {

        OutlinedTextField(
            value = url,
            onValueChange = {
                onEvent(AuthEvent.OnChangeServerUrl(it))
            },
            modifier = Modifier
                .padding(top = topPaddingInputFields)
                .fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.loginScreen_server_url))
            },
            maxLines = 1,
            singleLine = true
        )

        AnimatedVisibility(visible = !authTokenLoginEnabled) {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        onEvent(AuthEvent.OnChangeUsername(it))
                    },
                    modifier = Modifier
                        .padding(top = topPaddingInputFields)
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = R.string.loginScreen_username))
                    },
                    maxLines = 1,
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    onValueChange = {
                        onEvent(AuthEvent.OnChangePassword(it))
                    },
                    modifier = Modifier
                        .padding(top = topPaddingInputFields)
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = R.string.loginScreen_password))
                    },
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                )
            }
        }

        AnimatedVisibility(visible = authTokenLoginEnabled) {
            OutlinedTextField(
                value = authToken,
                onValueChange = {
                    onEvent(AuthEvent.OnChangeAuthToken(it))
                },
                modifier = Modifier
                    .padding(top = topPaddingInputFields, bottom = topPaddingInputFields)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.loginScreen_auth_token))
                },
                maxLines = 1,
                singleLine = true
            )
        }
    }
}

@Composable
fun LoginButton(
    onEvent: (AuthEvent) -> Unit
) {
    DefaultFullWidthButton(
        modifier = Modifier
            .padding(horizontal = bottomDrawerPaddingHorizontal, vertical = 10.dp)
            .fillMaxWidth(),
        colours = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = { onEvent(AuthEvent.Login) }
    ) {
        Icon(imageVector = Icons.Default.Login, contentDescription = "Login")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp, horizontal = 9.dp),
            text = stringResource(id = R.string.loginScreen_login),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun SignUpButton(
    onClick: () -> Unit
) {
    DefaultFullWidthButton(
        modifier = Modifier
            .padding(horizontal = bottomDrawerPaddingHorizontal, vertical = 10.dp)
            .fillMaxWidth(),
        colours = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Default.PersonAddAlt, contentDescription = "Sign Up")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp, horizontal = 9.dp),
            text = stringResource(id = R.string.loginScreen_signup),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
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
            .padding(horizontal = bottomDrawerPaddingHorizontal, vertical = 10.dp)
            .fillMaxWidth(),
        onClick = {
            onEvent(AuthEvent.OnChangeServerUrl(server.url))
            onEvent(AuthEvent.OnChangePassword(server.password))
            onEvent(AuthEvent.OnChangeUsername(server.user))
            onEvent(AuthEvent.OnChangeAuthToken(server.apiKey))
            onEvent(AuthEvent.Login)
        },
        colours = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Demo Server login")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp),
            text = stringResource(id = buttonText),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
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
        modifier = modifier.padding(horizontal = bottomDrawerPaddingHorizontal),
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
        Text(text = "Use Auth Token")
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
            fontSize = 16.sp
        )
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LoginScreenContent(
        username = "state.username",
        password = "state.password",
        url = "state.url",
        authToken = "state.authToken",
        error = "state.error",
        onEvent = {},
        isLoginSheetOpen = true,
        isSignUpSheetOpen = false,
        modifier = Modifier.fillMaxSize()
    )
}
