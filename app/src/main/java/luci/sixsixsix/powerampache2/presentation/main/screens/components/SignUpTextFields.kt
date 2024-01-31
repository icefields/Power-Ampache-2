package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.DefaultFullWidthButton
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.main.screens.bottomDrawerPaddingHorizontal

@Composable
fun SignUpBottomSheet(
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier
) {
    val topPaddingInputFields = 8.dp
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }

    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }
    var emailErrorMessage by rememberSaveable { mutableStateOf("") }
    var urlErrorMessage by rememberSaveable { mutableStateOf("") }
    var usernameErrorMessage by rememberSaveable { mutableStateOf("") }
    var repeatPasswordErrorMessage by rememberSaveable { mutableStateOf("") }

    val requiredFieldMessage = stringResource(id = R.string.loginScreen_signUp_requiredField)

    Column(
        modifier = modifier
            .padding(horizontal = bottomDrawerPaddingHorizontal)
    ) {

        OutlinedTextField(
            value = url,
            onValueChange = {
                url = it
                if(it.isBlank()) {
                    urlErrorMessage = requiredFieldMessage
                } else {
                    urlErrorMessage = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
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

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if(it.isBlank()) {
                    usernameErrorMessage = requiredFieldMessage
                } else {
                    usernameErrorMessage = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.loginScreen_username)) },
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

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if(it.isBlank()) {
                    emailErrorMessage = requiredFieldMessage
                } else {
                    emailErrorMessage = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.loginScreen_signUp_email)) },
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

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            placeholder = { Text(text = stringResource(id = R.string.loginScreen_fullname)) },
            maxLines = 1,
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onValueChange = {
                password = it
                if(it.isBlank()) {
                    passwordErrorMessage = requiredFieldMessage
                } else {
                    passwordErrorMessage = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.loginScreen_password)) },
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
                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
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

        OutlinedTextField(
            value = repeatPassword,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onValueChange = {
                repeatPassword = it
                repeatPasswordErrorMessage = ""
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.loginScreen_password_repeat)) },
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
                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
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

        DefaultFullWidthButton(
            modifier = Modifier
                .padding(horizontal = bottomDrawerPaddingHorizontal, vertical = 10.dp)
                .fillMaxWidth(),
            colours = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            onClick = {
                val isError =
                    password.isBlank().also { if (it) { passwordErrorMessage = requiredFieldMessage } } or
                    password.isBlank().also { if (it) passwordErrorMessage = requiredFieldMessage } or
                    username.isBlank().also { if (it) usernameErrorMessage = requiredFieldMessage } or
                    email.isBlank().also { if (it) emailErrorMessage = requiredFieldMessage } or
                    url.isBlank().also { if (it)urlErrorMessage = requiredFieldMessage } or
                    (password != repeatPassword).also { if (it) repeatPasswordErrorMessage = "The 2 passwords do not match" }

                if (!isError) {
                    onEvent(AuthEvent.SignUp(
                        serverUrl = url,
                        username = username,
                        password = password,
                        fullName = fullName,
                        email = email
                    ))
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Login,
                contentDescription = stringResource(id = R.string.loginScreen_signup)
            )
            Text(
                modifier = Modifier
                    .padding(vertical = 9.dp, horizontal = 9.dp),
                text = stringResource(id = R.string.loginScreen_signup),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
