package luci.sixsixsix.powerampache2.domain.models

import luci.sixsixsix.powerampache2.common.Constants

data class User(
    val id: String,
    val username: String,
    val email: String,
    val access: Int,
    val streamToken: String? = null,
    val fullNamePublic: Int? = null,
    //val validation: Any? = null,
    val disabled: Boolean,
    val createDate: Int = Constants.ERROR_INT,
    val lastSeen: Int = Constants.ERROR_INT,
    val website: String,
    val state: String,
    val city: String
)
