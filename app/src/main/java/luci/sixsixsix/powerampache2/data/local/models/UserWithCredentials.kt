package luci.sixsixsix.powerampache2.data.local.models

import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.domain.models.User

data class UserWithCredentials(
    val id: String? = null,
    val username: String? = null,
    val credentialsUsername: String? = null,
    val email: String? = Constants.USER_EMAIL_DEFAULT,
    val access: Int? = Constants.USER_ACCESS_DEFAULT,
    val streamToken: String? = null,
    val fullNamePublic: Int? = Constants.USER_FULL_NAME_PUBLIC_DEFAULT,
    val fullName: String? = null,
    //val validation: Any? = null,
    val disabled: Boolean? = false,
    val createDate: Int? = Constants.ERROR_INT,
    val lastSeen: Int? = Constants.ERROR_INT,
    val website: String? = null,
    val state: String? = null,
    val city: String? = null,
//    val authToken: String,
//    val serverUrl: String
)

fun UserWithCredentials.toUser(serverUrl: String) = User(
    username = username ?: credentialsUsername ?: ERROR_STRING,
    id = id ?: ERROR_INT.toString(),
    email = email ?: Constants.USER_EMAIL_DEFAULT,
    access = access ?: Constants.USER_ACCESS_DEFAULT,
    streamToken = streamToken ?: "",
    fullNamePublic = fullNamePublic ?: Constants.USER_FULL_NAME_PUBLIC_DEFAULT,
    fullName = fullName ?: "",
    disabled = disabled ?: false,
    createDate = createDate ?: Constants.ERROR_INT,
    lastSeen = lastSeen ?: Constants.ERROR_INT,
    website = website ?: "",
    state = state ?: "",
    city = city ?: "",
    art = "",
    serverUrl = serverUrl
)
