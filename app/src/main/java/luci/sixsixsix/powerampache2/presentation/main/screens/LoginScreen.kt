package luci.sixsixsix.powerampache2.presentation.main.screens

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
import luci.sixsixsix.powerampache2.common.Constants.DEBUG_DEMO_URL
import luci.sixsixsix.powerampache2.common.Constants.DEBUG_PASSWORD
import luci.sixsixsix.powerampache2.common.Constants.DEBUG_URL
import luci.sixsixsix.powerampache2.common.Constants.DEBUG_USER
import luci.sixsixsix.powerampache2.common.Constants.DEMO_AUTH_TOKEN
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

        // TODO DEBUG, REMOVE
        Button(onClick = {
            viewModel.onEvent(AuthEvent.OnChangeServerUrl(DEBUG_DEMO_URL))
            viewModel.onEvent(AuthEvent.OnChangePassword(""))
            viewModel.onEvent(AuthEvent.OnChangeUsername(""))
            viewModel.onEvent(AuthEvent.OnChangeAuthToken(DEMO_AUTH_TOKEN))
            viewModel.onEvent(AuthEvent.Login)
        }) {
            Text(text = stringResource(id = R.string.loginScreen_demo_server))
        }

        Button(onClick = {
            viewModel.onEvent(AuthEvent.OnChangeServerUrl(DEBUG_URL))
            viewModel.onEvent(AuthEvent.OnChangePassword(DEBUG_PASSWORD))
            viewModel.onEvent(AuthEvent.OnChangeUsername(DEBUG_USER))
            viewModel.onEvent(AuthEvent.OnChangeAuthToken(""))
            viewModel.onEvent(AuthEvent.Login)
        }) {
            Text(text = "Local Debug-Server")
        }
    }
}
