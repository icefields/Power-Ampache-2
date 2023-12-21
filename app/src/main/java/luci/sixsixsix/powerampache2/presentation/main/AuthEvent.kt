package luci.sixsixsix.powerampache2.presentation.main

sealed class AuthEvent {
    data object Login: AuthEvent()
    data object TryAutoLogin: AuthEvent()
    data class OnChangeUsername(val username: String): AuthEvent()
    data class OnChangePassword(val password: String): AuthEvent()
    data class OnChangeServerUrl(val url: String): AuthEvent()
}
