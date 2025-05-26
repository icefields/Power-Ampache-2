/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.data.local.models

import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.domain.common.Constants.USER_ACCESS_DEFAULT
import luci.sixsixsix.powerampache2.domain.common.Constants.USER_EMAIL_DEFAULT
import luci.sixsixsix.powerampache2.domain.common.Constants.USER_FULL_NAME_PUBLIC_DEFAULT
import luci.sixsixsix.powerampache2.domain.models.User

data class UserWithCredentials(
    val id: String? = null,
    val username: String? = null,
    val credentialsUsername: String? = null,
    val email: String? = USER_EMAIL_DEFAULT,
    val access: Int? = USER_ACCESS_DEFAULT,
    val streamToken: String? = null,
    val fullNamePublic: Int? = USER_FULL_NAME_PUBLIC_DEFAULT,
    val fullName: String? = null,
    //val validation: Any? = null,
    val disabled: Boolean? = false,
    val createDate: Int? = ERROR_INT,
    val lastSeen: Int? = ERROR_INT,
    val website: String? = null,
    val state: String? = null,
    val city: String? = null,
//    val authToken: String,
//    val serverUrl: String
)

fun UserWithCredentials.toUser(serverUrl: String) = User(
    username = username ?: credentialsUsername ?: ERROR_STRING,
    id = id ?: ERROR_INT.toString(),
    email = email ?: USER_EMAIL_DEFAULT,
    access = access ?: USER_ACCESS_DEFAULT,
    streamToken = streamToken ?: "",
    fullNamePublic = fullNamePublic ?: USER_FULL_NAME_PUBLIC_DEFAULT,
    fullName = fullName ?: "",
    disabled = disabled ?: false,
    createDate = createDate ?: ERROR_INT,
    lastSeen = lastSeen ?: ERROR_INT,
    website = website ?: "",
    state = state ?: "",
    city = city ?: "",
    art = "",
    serverUrl = serverUrl
)
