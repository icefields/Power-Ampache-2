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
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class HistoryEntity(
    val mediaId: String,
    val playCount: Int,
    val lastPlayed: Long = System.currentTimeMillis(),
    val multiUserId: String,
    @PrimaryKey val id: String = "$multiUserId$mediaId"
) {
    companion object {
        fun newEntry(
            mediaId: String,
            playCount: Int,
            lastPlayed: Long = System.currentTimeMillis(),
            username: String,
            serverUrl: String
        ) = HistoryEntity(
            mediaId = mediaId,
            lastPlayed = lastPlayed,
            playCount = playCount + 1,
            multiUserId = multiuserDbKey(username = username, serverUrl = serverUrl)
        )
    }
}

fun Song.toHistoryEntity(username: String,
                         serverUrl: String,
                         lastPlayed: Long = System.currentTimeMillis()
) = HistoryEntity.newEntry(
    mediaId = mediaId,
    playCount = playCount,
    username = username,
    lastPlayed = lastPlayed,
    serverUrl = serverUrl
)
