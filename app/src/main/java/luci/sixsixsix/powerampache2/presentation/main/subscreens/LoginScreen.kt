package luci.sixsixsix.powerampache2.presentation.main.subscreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.main.AuthViewModel

@Composable
@Destination(start = false)
fun LoginScreen(
//    navigator: DestinationsNavigator,
    viewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
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
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Username")
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
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Password")
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
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "You Server Url")
            },
            maxLines = 1,
            singleLine = true
        )
        Button(onClick = {
            viewModel.onEvent(AuthEvent.Login)
        }) {
            Text(text = "Login")
        }

        Text(text = state.error)
    }
}
