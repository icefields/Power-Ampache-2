package luci.sixsixsix.powerampache2.presentation.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: MusicRepository) : ViewModel() {
    var state by mutableStateOf(AuthState())

    init {
        viewModelScope.launch {
            // try to login with saved auth token
            state = state.copy(isLoading = true)
            when(val ping = repository.ping()) {
                is Resource.Success -> {
                    // if the session returned by ping is null, user is not authorized
                    // if the session is not null we are authorized and the session is refreshed
                    ping.data?.second?.let {
                        state = state.copy(session = it)
                        state = state.copy(isLoading = false)
                    } ?: run {
                        // try autologin
                        repository
                            .autoLogin()
                            .collect { result ->
                                when(result) {
                                    is Resource.Success -> {
                                        result.data?.let { auth ->
                                            Log.d("aaaa", "AuthViewModel ${auth}")
                                            state = state.copy(session = auth)
                                        }
                                    }
                                    is Resource.Error -> {
                                        state = state.copy(isLoading = false)
                                        Log.d("aaaa", "ERROR AuthViewModel ${result.exception}")
                                    }
                                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                                }
                            }
                    }
                }
                is Resource.Error -> {
                    // TODO handle error
                    Log.d("aaaa", "ERROR AuthViewModel.INIT ${ping.data?.second} ${ping.exception}")
                    state = state.copy(isLoading = false)
                }
                else -> {}
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
                .authorize(username, password, serverUrl)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { auth ->
                                Log.d("aaaa", "AuthViewModel ${auth}")
                                state = state.copy(session = auth)
                            }
                        }
                        is Resource.Error -> {
                            Log.d("aaaa", "ERROR AuthViewModel login ${result.exception?.localizedMessage}")
                            state = state.copy(isLoading = false)
                        }
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
