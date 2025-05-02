/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.domain.common.Constants.USER_ACCESS_DEFAULT
import luci.sixsixsix.powerampache2.domain.common.processArtUrl
import luci.sixsixsix.powerampache2.domain.common.processFlag
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
): AmpacheBaseResponse() {
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

fun UserDto.toUser(serverUrl: String) = User(
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
    art = processArtUrl(hasArt, art),
    serverUrl = serverUrl
)
