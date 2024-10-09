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
package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
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
    val city: String = "",
    @ColumnInfo(name = "art", defaultValue = "")
    val art: String,
    @ColumnInfo(name = "serverUrl", defaultValue = "")
    val serverUrl: String,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String = multiuserDbKey(username = username, serverUrl = serverUrl)
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
    city = city,
    art = art,
    serverUrl = serverUrl
)

fun User.toUserEntity2(serverUrl: String) = UserEntity(
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
    city = city,
    art = art,
    serverUrl = serverUrl
)

fun User.toUserEntity(serverUrl: String): UserEntity {
    val userEmail = email ?: ""
    val userAccess = access ?: 0
    val userStreamToken = streamToken ?: ""
    val userFullNamePublic = fullNamePublic ?: 0
    val userFullName = fullName ?: ""
    val userDisabled = disabled ?: false
    val userCreateDate = createDate ?: Constants.ERROR_INT
    val userLastSeen = lastSeen ?: Constants.ERROR_INT
    val userWebsite = website ?: ""
    val userState = state ?: ""
    val userCity = city ?: ""

    return UserEntity(
        id = id,
        username = username.lowercase(), // lowercase to facilitate queries
        email = userEmail,
        access = userAccess,
        streamToken = userStreamToken,
        fullNamePublic = userFullNamePublic,
        fullName = userFullName,
        disabled = userDisabled,
        createDate = userCreateDate,
        lastSeen = userLastSeen,
        website = userWebsite,
        state = userState,
        city = userCity,
        serverUrl = serverUrl/*.lowercase()*/,
        art = art
    )
}