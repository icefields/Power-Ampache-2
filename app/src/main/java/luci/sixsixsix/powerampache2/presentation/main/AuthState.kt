package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.domain.models.Session

data class AuthState(
    val session: Session? = null,
    val isLoading: Boolean = false,
    val error: String = "",
    val username: String = "",
    val password: String = "",
    val authToken: String = "",
    val url: String = "",
)
