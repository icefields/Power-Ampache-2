package luci.sixsixsix.powerampache2.presentation.screens.main

sealed class AuthEvent {
    data object Login: luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent()
    data object TryAutoLogin: luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent()
    data class OnChangeUsername(val username: String): luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent()
    data class OnChangePassword(val password: String): luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent()
    data class OnChangeServerUrl(val url: String): luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent()
    data class OnChangeAuthToken(val token: String): luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent()
    data class SignUp(val serverUrl: String, val username: String, val password: String, val email: String, val fullName: String): luci.sixsixsix.powerampache2.presentation.screens.main.AuthEvent()
}
