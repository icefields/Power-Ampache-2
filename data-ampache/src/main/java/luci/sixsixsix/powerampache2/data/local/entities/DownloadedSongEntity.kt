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
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.common.processFlag
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.common.normalizeForSearch
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class DownloadedSongEntity(
    @PrimaryKey val mediaId: String,
    val title: String,
    val albumId: String,
    val albumName: String,
    val artistId: String,
    val artistName: String,
    val songUri: String,
    val bitrate: Int,
    val channels: Int,
    val genre: List<MusicAttribute> = listOf(),
    val mime: String? = null,
    val name: String,
    val mode: String? = null,
    val streamFormat: String? = null,
    val format: String? = null,
    val disk: Int,
    val composer: String = "",
    val rateHz: Int = Constants.ERROR_INT,
    val size: Int = Constants.ERROR_INT,
    val time: Int = Constants.ERROR_INT,
    val trackNumber: Int = Constants.ERROR_INT,
    val year: Int = Constants.ERROR_INT,
    val imageUrl: String = Constants.DEFAULT_NO_IMAGE,
    val albumArtist: MusicAttribute = MusicAttribute.emptyInstance(),
    val averageRating: Float,
    val preciseRating: Float,
    val rating: Float,
    val lyrics: String = "",
    val comment: String = "",
    val language: String = "",
    val relativePath: String,
    val owner: String,
    @ColumnInfo(name = "flag", defaultValue = "false")
    val flag: Boolean,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String,
    @ColumnInfo(name = "searchTitle", defaultValue = "")
    val searchTitle: String,
    @ColumnInfo(name = "searchAlbum", defaultValue = "")
    val searchAlbum: String,
    @ColumnInfo(name = "searchArtist", defaultValue = "")
    val searchArtist: String,
)

fun Song.toDownloadedSongEntity(
    downloadedSongUri: String,
    downloadedImageUri: String,
    owner: String,
    serverUrl: String
) = DownloadedSongEntity(
    mediaId = mediaId,
    title = title,
    artistId = artist.id,
    artistName = artist.name,
    albumId = album.id,
    albumName = album.name,
    songUri = downloadedSongUri,
    bitrate = bitrate,
    channels = channels,
    genre = genre,
    mime = mime,
    name = name,
    format = format,
    disk = disk,
    composer = composer,
    rateHz = rateHz,
    size = size,
    time = time,
    trackNumber = trackNumber,
    year = year,
    imageUrl = downloadedImageUri,
    albumArtist = albumArtist,
    averageRating = averageRating,
    preciseRating = preciseRating,
    rating = rating,
    relativePath = filename,
    owner = owner,
    flag = flag == 1,
    multiUserId = multiuserDbKey(username = owner, serverUrl = serverUrl),
    searchArtist = artist.name.normalizeForSearch(),
    searchAlbum = album.name.normalizeForSearch(),
    searchTitle = title.normalizeForSearch()
)

fun DownloadedSongEntity.toSong() = Song(
    mediaId = mediaId,
    title = title ?: "",
    artist = MusicAttribute(id = artistId, name = artistName),
    album = MusicAttribute(id = albumId, name = albumName),
    albumArtist = albumArtist,
    songUrl = songUri ?: "",
    imageUrl = imageUrl ?: "",
    bitrate = bitrate ?: Constants.ERROR_INT,
    channels = channels ?: Constants.ERROR_INT,
    composer = composer ?: "",
    genre = genre,
    mime = mime,
    name = name ?: "",
    rateHz = rateHz ?: Constants.ERROR_INT,
    size = size ?: Constants.ERROR_INT,
    time = time ?: Constants.ERROR_INT,
    trackNumber = trackNumber ?: Constants.ERROR_INT,
    year = year ?: Constants.ERROR_INT,
    mode = mode,
    streamFormat = streamFormat,
    format = format,
    lyrics = lyrics,
    comment = comment,
    language = language,
    disk = disk,
    preciseRating = preciseRating,
    averageRating = averageRating,
    rating = rating,
    filename = relativePath,
    flag = processFlag(flag)
)
