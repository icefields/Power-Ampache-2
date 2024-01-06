package luci.sixsixsix.powerampache2.presentation.main.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.data.Servers
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.main.AuthViewModel

@Composable
@Destination(start = false)
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.username,
            onValueChange = {
                viewModel.onEvent(AuthEvent.OnChangeUsername(it))
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
            value = state.password,
            onValueChange = {
                viewModel.onEvent(AuthEvent.OnChangePassword(it))
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
            value = state.url,
            onValueChange = {
                viewModel.onEvent(AuthEvent.OnChangeServerUrl(it))
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
            value = state.authToken,
            onValueChange = {
                viewModel.onEvent(AuthEvent.OnChangeAuthToken(it))
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
        Button(onClick = {
            viewModel.onEvent(AuthEvent.Login)
        }) {
            Text(text = stringResource(id = R.string.loginScreen_login))
        }
        Text(text = state.error)

        LoginButton(server = Servers.LocalDebug, buttonText = R.string.loginScreen_local_server)
        LoginButton(server = Servers.RemoteDebug, buttonText = R.string.loginScreen_remote_server)
        LoginButton(server = Servers.Dogmazic, buttonText = R.string.loginScreen_dogmazic_server)
        LoginButton(server = Servers.AmpacheDemo, buttonText = R.string.loginScreen_demo_server)
    }
}

@Composable
fun LoginButton(server: Servers, viewModel: AuthViewModel = hiltViewModel(), @StringRes buttonText: Int){
    Button(onClick = {
        viewModel.onEvent(AuthEvent.OnChangeServerUrl(server.url))
        viewModel.onEvent(AuthEvent.OnChangePassword(server.password))
        viewModel.onEvent(AuthEvent.OnChangeUsername(server.user))
        viewModel.onEvent(AuthEvent.OnChangeAuthToken(server.apiKey))
        viewModel.onEvent(AuthEvent.Login)
    }) {
        Text(text = stringResource(id = buttonText))
    }
}