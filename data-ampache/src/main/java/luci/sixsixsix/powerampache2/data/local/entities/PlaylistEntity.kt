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
import luci.sixsixsix.powerampache2.data.remote.dto.fromStringToPlaylistType
import luci.sixsixsix.powerampache2.domain.models.Playlist

@Entity
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val owner: String,
    val items: Int? = 0,
    val type: String? = null,
    val artUrl: String? = null,
    val flag: Int = 0,
    val preciseRating: Float = 0.0f,
    val rating: Int = 0,
    val averageRating: Float = 0.0f,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String
)

fun PlaylistEntity.toPlaylist() = Playlist(
    id = id,
    name = name ?: "",
    owner = owner ?: "",
    items = items ?: 0,
    type = fromStringToPlaylistType(type),
    artUrl = artUrl ?: "",
    flag = flag,
    preciseRating = preciseRating,
    rating = rating,
    averageRating = averageRating
)

fun Playlist.toPlaylistEntity(username: String, serverUrl: String) = PlaylistEntity(
    id = id,
    name = name ?: "",
    owner = owner ?: "",
    items = items ?: 0,
    type = type?.name ?: "",
    artUrl = artUrl ?: "",
    flag = flag,
    preciseRating = preciseRating,
    rating = rating,
    averageRating = averageRating,
    multiUserId = multiuserDbKey(username = username, serverUrl = serverUrl)
)
