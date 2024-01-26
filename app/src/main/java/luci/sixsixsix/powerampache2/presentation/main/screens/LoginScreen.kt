package luci.sixsixsix.powerampache2.presentation.main.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.data.Servers
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.main.AuthViewModel

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

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {

        items(6) {
            when(it) {
                1 -> Icon(
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(20.dp),
                    painter = painterResource(id = R.drawable.ic_power_ampache_mono),
                    contentDescription = "Power Ampache Logo"
                )
                2 -> LoginTextFields(
                    username = username,
                    password = password,
                    url = url,
                    authToken = authToken,
                    onEvent = onEvent,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                )
                3 -> LoginButton(onEvent)
                4 -> Text(text = error)
                5 -> DebugLoginButtons(onEvent, modifier = Modifier.wrapContentHeight().fillMaxWidth())
            }
        }
    }
}

@Composable
fun DebugLoginButtons(
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DebugLoginButton(
            server = Servers.LocalDebug,
            buttonText = R.string.loginScreen_local_server,
            onEvent = onEvent
        )
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
    }
}

@Composable
fun LoginTextFields(
    username: String,
    password: String,
    url: String,
    authToken: String,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = {
                onEvent(AuthEvent.OnChangeUsername(it))
            },
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.login_inputText_padding))
                .fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.loginScreen_username))
            },
            maxLines = 1,
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                onEvent(AuthEvent.OnChangePassword(it))
            },
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.login_inputText_padding))
                .fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.loginScreen_password))
            },
            maxLines = 1,
            singleLine = true
        )
        OutlinedTextField(
            value = url,
            onValueChange = {
                onEvent(AuthEvent.OnChangeServerUrl(it))
            },
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.login_inputText_padding))
                .fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.loginScreen_server_url))
            },
            maxLines = 1,
            singleLine = true
        )
        OutlinedTextField(
            value = authToken,
            onValueChange = {
                onEvent(AuthEvent.OnChangeAuthToken(it))
            },
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.login_inputText_padding))
                .fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.loginScreen_auth_token))
            },
            maxLines = 1,
            singleLine = true
        )
    }
}

@Composable
fun LoginButton(
    onEvent: (AuthEvent) -> Unit) {
    TextButton(
        modifier = Modifier
            .padding(horizontal = 26.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = {
            onEvent(AuthEvent.Login)
        }
    ) {
        Icon(imageVector = Icons.Default.CurrencyBitcoin, contentDescription = "Donate Bitcoin")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp),
            text = stringResource(id = R.string.loginScreen_login),
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
    TextButton(
        modifier = Modifier
            .padding(horizontal = 26.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = {
            onEvent(AuthEvent.OnChangeServerUrl(server.url))
            onEvent(AuthEvent.OnChangePassword(server.password))
            onEvent(AuthEvent.OnChangeUsername(server.user))
            onEvent(AuthEvent.OnChangeAuthToken(server.apiKey))
            onEvent(AuthEvent.Login)
        }
    ) {
        Icon(imageVector = Icons.Default.CurrencyBitcoin, contentDescription = "Donate Bitcoin")
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
@Preview
fun LoginScreenPreview() {
    LoginScreenContent(
        username = "state.username",
        password = "state.password",
        url = "state.url",
        authToken = "state.authToken",
        error = "state.error",
        onEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}
