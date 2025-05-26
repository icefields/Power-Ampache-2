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
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute

@Entity
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val basename: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val artists: List<MusicAttribute> = listOf(),
    val time: Int = 0,
    val year: Int = 0,
    val songCount: Int = 0,
    val diskCount: Int = 0,
    val genre: List<MusicAttribute>,
    val artUrl: String = "",
    val flag: Int = 0,
    val rating: Int = 0,
    val averageRating: Float = 0.0f,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String
)

fun AlbumEntity.toAlbum() = Album(
    id = id,
    name = name ?: "",
    basename = basename ?: "",
    artists = artists,
    artist = MusicAttribute(id = artistId, name = artistName),
    artUrl = artUrl ?: "",
    songCount = songCount ?: 0,
    flag = flag,
    rating = rating,
    time = time ?: 0,
    year = year ?: 0,
    genre = genre
)

fun Album.toAlbumEntity(username: String, serverUrl: String) = AlbumEntity(
    id = id,
    name = name,
    basename = basename,
    artists = artists,
    artistId = artist.id,
    artistName = artist.name,
    rating = rating,
    artUrl = artUrl,
    songCount = songCount,
    flag = flag,
    time = time,
    year = year,
    genre = genre,
    multiUserId = multiuserDbKey(username = username, serverUrl = serverUrl)
)
