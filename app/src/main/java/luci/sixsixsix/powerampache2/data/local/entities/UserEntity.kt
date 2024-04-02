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
    city = city,
    art = ""
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
