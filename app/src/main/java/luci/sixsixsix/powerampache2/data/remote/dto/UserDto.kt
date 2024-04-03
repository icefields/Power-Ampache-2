package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Constants.USER_ACCESS_DEFAULT
import luci.sixsixsix.powerampache2.common.processArtUrl
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
    @SerializedName("fullname")
    val fullName: String? = null,
    @SerializedName("validation")
    val validation: Any? = null,
    @SerializedName("disabled")
    val disabled: Any? = null,
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
    @SerializedName("has_art")
    val hasArt: Any? = null,
    @SerializedName("art")
    val art: String? = null
) {
    companion object {
        fun getMockUserDto(): UserDto {
            val userDtoString = "{\n" +
                    "    \"id\": \"7\",\n" +
                    "    \"username\": \"Iodlwpa\",\n" +
                    "    \"auth\": null,\n" +
                    "    \"email\": \"I@p.l\",\n" +
                    "    \"access\": 25,\n" +
                    "    \"streamtoken\": null,\n" +
                    "    \"fullname_public\": false,\n" +
                    "    \"validation\": null,\n" +
                    "    \"disabled\": false,\n" +
                    "    \"create_date\": 1711597870,\n" +
                    "    \"last_seen\": 1711681811,\n" +
                    "    \"website\": null,\n" +
                    "    \"state\": null,\n" +
                    "    \"city\": null,\n" +
                    "    \"art\": \"http:\\/\\/192.168.14.77\\/ampache-dev\\/image.php?object_id=7&object_type=user\",\n" +
                    "    \"has_art\": false\n" +
                    "}"
            return Gson().fromJson(userDtoString, UserDto::class.java)
        }
    }
}

fun UserDto.toUser() = User(
    id = id,
    username = username,
    email = email ?: "",
    access = access ?: USER_ACCESS_DEFAULT,
    streamToken = streamToken ?: "",
    fullNamePublic = processFlag(fullNamePublic),
    disabled = processFlag(disabled) == 1,
    createDate = createDate ?: ERROR_INT,
    lastSeen = lastSeen ?: ERROR_INT,
    website = website ?: "",
    state = state ?: "",
    city = city ?: "",
    fullName = fullName ?: "",
    art = processArtUrl(hasArt, art)
)
