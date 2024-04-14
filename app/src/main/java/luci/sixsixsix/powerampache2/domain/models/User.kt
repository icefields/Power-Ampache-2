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
package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.DOGMAZIC_FAKE_CITY
import luci.sixsixsix.powerampache2.common.Constants.DOGMAZIC_FAKE_NAME
import luci.sixsixsix.powerampache2.common.Constants.DOGMAZIC_FAKE_STATE
import luci.sixsixsix.powerampache2.common.Constants.DOGMAZIC_FAKE_USERNAME
import luci.sixsixsix.powerampache2.common.Constants.MASTODON_URL
import luci.sixsixsix.powerampache2.common.Constants.USER_ERROR_MESSAGE
import luci.sixsixsix.powerampache2.common.Constants.USER_ID_ERROR

@Parcelize
data class User(
    val id: String,
    val username: String,
    val email: String = Constants.USER_EMAIL_DEFAULT,
    val access: Int = Constants.USER_ACCESS_DEFAULT,
    val streamToken: String = "",
    val fullNamePublic: Int = Constants.USER_FULL_NAME_PUBLIC_DEFAULT,
    val fullName: String = "",
    //val validation: Any? = null,
    val disabled: Boolean = false,
    val createDate: Int = Constants.ERROR_INT,
    val lastSeen: Int = Constants.ERROR_INT,
    val website: String = "",
    val state: String = "",
    val city: String = "",
    val art: String = "",
    val serverUrl: String
): Parcelable {

    fun isError() =
        id == USER_ID_ERROR.toString()

    companion object {
        fun emptyUser(): User = User(
            "", "", "",
            Constants.ERROR_INT,
            "",
            Constants.ERROR_INT,
            "",
            true,
            Constants.ERROR_INT,
            Constants.ERROR_INT,
            "",
            "",
            "",
            art = "",
            serverUrl = ""
        )

        fun demoUser(): User = User(
            id = "demoUser",
            username = DOGMAZIC_FAKE_USERNAME,
            email = Constants.DOGMAZIC_FAKE_EMAIL,
            access = Constants.USER_ACCESS_DEFAULT,
            streamToken = "",
            fullNamePublic = 1,
            fullName = DOGMAZIC_FAKE_NAME,
            disabled = false,
            createDate = Constants.ERROR_INT,
            lastSeen = Constants.ERROR_INT,
            website = MASTODON_URL,
            state = DOGMAZIC_FAKE_STATE,
            city = DOGMAZIC_FAKE_CITY,
            serverUrl = "https://retroscroll.cat/wp-content/",
            art = "https://retroscroll.cat/wp-content/uploads/2014/10/shyguy.png"
        )

        fun mockUser(): User = Gson().fromJson(
            "{\n" +
                "    \"id\": \"3\",\n" +
                "    \"username\": \"luci\",\n" +
                "    \"auth\": null,\n" +
                "    \"email\": \"some@er.fd\",\n" +
                "    \"access\": 25,\n" +
                "    \"streamtoken\": null,\n" +
                "    \"fullname_public\": 1,\n" +
                "    \"validation\": null,\n" +
                "    \"disabled\": false,\n" +
                "    \"create_date\": 1704516888,\n" +
                "    \"last_seen\": 1706202621,\n" +
                "    \"website\": \"http://somewebsite.mockd\",\n" +
                "    \"state\": \"Mercury\",\n" +
                "    \"city\": \"Phobos Town\",\n" +
                "    \"fullname\": \"Lucifer The Conqueror\"\n" +
                "}",
            User::class.java
        )
    }
}
