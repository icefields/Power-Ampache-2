package luci.sixsixsix.powerampache2.presentation.main

sealed class AuthEvent {
    data object Login: AuthEvent()
    data object TryAutoLogin: AuthEvent()
    data class OnChangeUsername(val username: String): AuthEvent()
    data class OnChangePassword(val password: String): AuthEvent()
    data class OnChangeServerUrl(val url: String): AuthEvent()
    data class OnChangeAuthToken(val token: String): AuthEvent()
    data class SignUp(val serverUrl: String, val username: String, val password: String, val email: String, val fullName: String): AuthEvent()
}
