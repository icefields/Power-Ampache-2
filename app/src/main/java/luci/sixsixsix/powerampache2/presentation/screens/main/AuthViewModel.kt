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
package luci.sixsixsix.powerampache2.presentation.screens.main

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.utils.AlarmScheduler
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
@OptIn(SavedStateHandleSaveableApi::class)
class AuthViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playlistManager: MusicPlaylistManager,
    pingScheduler: AlarmScheduler,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    var state by savedStateHandle.saveable { mutableStateOf(AuthState()) }

    val sessionStateFlow = repository.sessionLiveData
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val userStateFlow = repository.userLiveData
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val messagesStateFlow = playlistManager.logMessageUserReadableState
        .mapNotNull { it.logMessage }
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    init {
        L(sessionStateFlow.value, "ssss")
        state = state.copy(isLoading = true)
        verifyAndAutologin()
        // Listen to changes of the session table from the database
        viewModelScope.launch {
            sessionStateFlow.collect { session ->
                L(session, "ssss")
                if (session == null) {
                    pingScheduler.cancel()
                    // setting the session to null will show the login screen, but the autologin call
                    // will immediately set isLoading to true which will show the loading screen instead
                    state = state.copy(isLoading = true)
                    // autologin will log back in if credentials are correct
                    L("autologin from init AuthVM ssss")
                    autologin()
                } else {
                    pingScheduler.schedule()
                }
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login()
            is AuthEvent.TryAutoLogin -> {}
            is AuthEvent.OnChangePassword -> state = state.copy(password = event.password)
            is AuthEvent.OnChangeServerUrl -> state = state.copy(url = event.url)
            is AuthEvent.OnChangeUsername -> state = state.copy(username = event.username)
            is AuthEvent.OnChangeAuthToken -> state = state.copy(authToken = event.token)
            is AuthEvent.SignUp -> signUp(
                username = event.username,
                serverUrl = event.serverUrl,
                email = event.email,
                password = event.password,
                fullName = event.fullName
            )
        }
    }

    /**
     * If the session returned by ping is null, the token is probably expired and the user is no
     * longer authorized.
     *
     * NO NEED TO CALL AUTOLOGIN upon successful ping, a null token from the ping call will trigger
     * autologin in the init from the session flow.
     *
     * Do not show loading screen during ping, only during autologin
     */
    fun verifyAndAutologin() {
        viewModelScope.launch {
            // try to login with saved auth token
            L("verifyAndAutologin")
            when (val ping = repository.ping()) {
                is Resource.Success -> {}
                is Resource.Error -> state = state.copy(isLoading = false)
                is Resource.Loading -> if (!ping.isLoading) {
                    state = state.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun autologin() = repository.autoLogin().collect { result ->
        when (result) {
            is Resource.Success -> result.data?.let { auth ->
                L("AuthViewModel autologin", auth)
                // remove error messages after login
                playlistManager.updateErrorLogMessage("")
            }
            is Resource.Error -> state =
                state.copy(isLoading = false, isAutologin = false)
            is Resource.Loading ->
                state = state.copy(isLoading = result.isLoading, isAutologin = result.isLoading)
        }
    }

    private fun signUp(
        username: String,
        password: String,
        email: String,
        serverUrl: String,
        fullName: String
    ) {
        viewModelScope.launch {
            repository.register(
                username = username,
                password = password.sha256(),
                serverUrl = serverUrl,
                fullName = fullName,
                email = email
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            playlistManager.updateUserMessage(application.getString(R.string.loginScreen_register_success))
                        }
                    }

                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                    }

                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                }
            }
        }
    }

    private fun login(
        username: String = state.username,
        password: String = state.password,
        serverUrl: String = state.url,
        authToken: String = state.authToken
    ) {
        viewModelScope.launch {
            repository.authorize(
                username = username.trim(),
                password = password.sha256(),
                serverUrl = serverUrl.trim(),
                authToken = authToken
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { auth ->
                            // clear credentials after login
                            state = state.copy(username = "", authToken = "", password = "")
                            // clear any user facing message on the UI
                            playlistManager.updateUserMessage("")
                        }
                    }
                    is Resource.Error -> state = state.copy(isLoading = false)
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                }
            }
        }
    }
}
