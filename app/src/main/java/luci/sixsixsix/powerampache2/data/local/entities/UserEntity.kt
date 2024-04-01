package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.domain.models.User

@Entity
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val access: Int,
    val streamToken: String? = null,
    val fullNamePublic: Int,
    val fullName: String? = null,
    val disabled: Boolean = false,
    val createDate: Int = Constants.ERROR_INT,
    val lastSeen: Int = Constants.ERROR_INT,
    val website: String = "",
    val state: String = "",
    val city: String = ""
)

fun UserEntity.toUser() = User(
    id = id,
    username = username,
    email = email,
    access = access,
    streamToken = streamToken ?: "",
    fullNamePublic = fullNamePublic,
    fullName = fullName ?: "",
    disabled = disabled,
    createDate = createDate,
    lastSeen = lastSeen,
    website = website,
    state = state,
    city = city
)
fun User.toUserEntity() = UserEntity(
    id = id,
    username = username,
    email = email,
    access = access,
    streamToken = streamToken,
    fullNamePublic = fullNamePublic,
    fullName = fullName,
    disabled = disabled,
    createDate = createDate,
    lastSeen = lastSeen,
    website = website,
    state = state,
    city = city
)
