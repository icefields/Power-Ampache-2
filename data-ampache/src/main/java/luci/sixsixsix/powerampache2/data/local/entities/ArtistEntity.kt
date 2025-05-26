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
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute

@Entity
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val albumCount: Int = 0,
    val songCount: Int = 0,
    val genre: List<MusicAttribute> = listOf(),
    val artUrl: String = "",
    val flag: Int = 0,
    val summary: String? = null,
    val time: Int = 0,
    val yearFormed: Int = 0,
    val placeFormed: String? = null,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String = ""
)

fun ArtistEntity.toArtist() = Artist(
    id = id,
    name = name ?: "",
    albumCount = albumCount ?: 0,
    songCount = songCount,
    genre = genre,
    artUrl = artUrl ?: "",
    flag = flag,
    summary = summary,
    time = time ?: 0,
    yearFormed = yearFormed,
    placeFormed = placeFormed
)

fun Artist.toArtistEntity(username: String, serverUrl: String) = ArtistEntity(
    id = id,
    name = name ?: "",
    albumCount = albumCount ?: 0,
    songCount = songCount,
    genre = genre,
    artUrl = artUrl ?: "",
    flag = flag,
    summary = summary,
    time = time ?: 0,
    yearFormed = yearFormed,
    placeFormed = placeFormed.toString(),
    multiUserId = multiuserDbKey(username = username, serverUrl = serverUrl)
)
