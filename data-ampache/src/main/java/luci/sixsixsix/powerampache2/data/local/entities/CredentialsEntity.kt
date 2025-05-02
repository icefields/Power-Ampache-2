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
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey

const val CREDENTIALS_PRIMARY_KEY = "power-ampache-2-credentials"
@Entity
data class CredentialsEntity(
    @PrimaryKey val primaryKey: String = CREDENTIALS_PRIMARY_KEY,
    val username: String,
    val password: String,
    val authToken: String,
    val serverUrl: String,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String = multiuserDbKey(username = username, serverUrl = serverUrl)
)

fun CredentialsEntity.toMultiUserCredentialEntity() = MultiUserCredentialEntity(
    username = username,
    password = password,
    authToken = authToken,
    serverUrl = serverUrl
)
