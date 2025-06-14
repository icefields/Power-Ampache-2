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
package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.models.Artist

@Entity
data class RecommendedArtistEntity(
    val recommendedArtistId: String,
    val baseArtistId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val multiUserId: String,
    @PrimaryKey val id: String = "$multiUserId$recommendedArtistId"
) {
    companion object {
        fun newEntry(
            recommendedArtistId: String,
            baseArtistId: String,
            username: String,
            serverUrl: String
        ) = RecommendedArtistEntity(
            recommendedArtistId = recommendedArtistId,
            baseArtistId = baseArtistId,
            multiUserId = multiuserDbKey(username = username, serverUrl = serverUrl)
        )
    }
}

fun Artist.toRecommendedArtistEntity(
    username: String,
    serverUrl: String,
    baseArtistId: String
) = RecommendedArtistEntity.newEntry(
    recommendedArtistId = id,
    baseArtistId = baseArtistId,
    username = username,
    serverUrl = serverUrl
)
