package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.processFlag
import luci.sixsixsix.powerampache2.domain.models.User

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String = "",
    @SerializedName("auth")
    val auth: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("access")
    val access: Int? = null,
    @SerializedName("streamtoken")
    val streamToken: String? = null,
    @SerializedName("fullname_public")
    val fullNamePublic: Any? = null,
    @SerializedName("validation")
    val validation: Any? = null,
    @SerializedName("disabled")
    val disabled: Boolean? = null,
    @SerializedName("create_date")
    val createDate: Int? = null,
    @SerializedName("last_seen")
    val lastSeen: Int? = null,
    @SerializedName("website")
    val website: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("city")
    val city: String? = null,
)

fun UserDto.toUser() = User(
    id,
    username,
    email ?: "",
    access ?: ERROR_INT,
    streamToken,
    processFlag( fullNamePublic),
    disabled ?: false,
    createDate ?: ERROR_INT,
    lastSeen ?: ERROR_INT,
    website ?: "",
    state ?: "",
    city ?: ""
)
