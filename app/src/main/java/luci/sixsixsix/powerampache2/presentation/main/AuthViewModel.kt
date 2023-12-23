package luci.sixsixsix.powerampache2.presentation.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.domain.MusicRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: MusicRepository) : ViewModel() {
    var state by mutableStateOf(AuthState())

    init {
        verifyAndAutologin()
    }

    fun verifyAndAutologin() {
        viewModelScope.launch {
            // try to login with saved auth token
            state = state.copy(isLoading = true)
            when( val ping = repository.ping() ) {
                is Resource.Success -> {
                    //   If the session returned by ping is null, the token is probably expired and
                    // the user is no longer authorized
                    //   If the session is not null we are authorized and the auth token in the
                    // session object is refreshed
                    ping.data?.second?.let {
                        state = state.copy(session = it, isLoading = false)
                    } ?: run {
                        autologin()
                    }
                }
                is Resource.Error -> {
                    state = state.copy(error = "{${ping.exception}", isLoading = false)
                }
                else -> {}
            }
        }
    }

    private suspend fun autologin() {
        repository
            .autoLogin()
            .collect { result ->
                when(result) {
                    is Resource.Success -> {
                        result.data?.let { auth ->
                            L( "AuthViewModel ${auth}")
                            state = state.copy(session = auth)
                        }
                    }
                    is Resource.Error -> state = state.copy(error = "{$result.exception}", isLoading = false)
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                }
            }
    }

    fun onEvent(event: AuthEvent) {
        when(event) {
            is AuthEvent.Login -> login()
            is AuthEvent.TryAutoLogin -> {}
            is AuthEvent.OnChangePassword -> state = state.copy(password = event.password)
            is AuthEvent.OnChangeServerUrl -> state = state.copy(url = event.url)
            is AuthEvent.OnChangeUsername -> state = state.copy(username = event.username)
        }
    }

    private fun login(
        username: String = state.username,
        password: String = state.password,
        serverUrl: String = state.url
    ) {
        viewModelScope.launch {
            repository
                .authorize(username, password.sha256(), serverUrl)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { auth ->
                                L( "AuthViewModel ${auth}")
                                state = state.copy(session = auth)
                            }
                        }
                        is Resource.Error -> {
                            L( "ERROR AuthViewModel login ${result.exception?.localizedMessage}")
                            state = state.copy(error = result.exception?.toString() ?: "authorization error", isLoading = false)
                        }
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
