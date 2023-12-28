package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.common.Constants.DEBUG_PASSWORD
import luci.sixsixsix.powerampache2.common.Constants.DEBUG_URL
import luci.sixsixsix.powerampache2.common.Constants.DEBUG_USER
import luci.sixsixsix.powerampache2.domain.models.Session

data class AuthState(
    val session: Session? = null,
    val isLoading: Boolean = false,
    val error: String = "",
    val username: String = DEBUG_USER,
    val password: String = DEBUG_PASSWORD,
    val authToken: String = "",
    val url: String = DEBUG_URL
)
