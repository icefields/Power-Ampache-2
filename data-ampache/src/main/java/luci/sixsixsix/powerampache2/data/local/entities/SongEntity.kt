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
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.common.normalizeForSearch
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class SongEntity(
    @PrimaryKey val mediaId: String,
    val title: String,
    val albumId: String,
    val albumName: String,
    val artistId: String,
    val artistName: String,
    val albumArtist: MusicAttribute = MusicAttribute.emptyInstance(),
    val songUrl: String,
    val imageUrl: String,
    val bitrate: Int,
    val streamBitrate: Int,
    val catalog: Int,
    val channels: Int,
    val composer: String,
    val filename: String,
    val genre: List<MusicAttribute> = listOf(),
    val mime: String? = null,
    val playCount: Int,
    val playlistTrackNumber: Int,
    val rateHz: Int,
    val size: Int,
    val time: Int?,
    val trackNumber: Int,
    val year: Int,
    val name: String,
    val mode: String? = null,
    val artists: List<MusicAttribute> = listOf(),
    val flag: Int = 0,
    val streamFormat: String? = null,
    val format: String? = null,
    val streamMime: String? = null,
    val publisher: String? = null,
    val replayGainTrackGain: Float? = null,
    val replayGainTrackPeak: Float? = null,
    val disk: Int,
    val diskSubtitle: String,
    val mbId: String,
    val comment: String,
    val language: String,
    val lyrics: String,
    val albumMbId: String,
    val artistMbId: String,
    val albumArtistMbId: String,
    val averageRating: Float,
    val preciseRating: Float,
    val rating: Float,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String,
    @ColumnInfo(name = "searchTitle", defaultValue = "")
    val searchTitle: String = ""
)

fun SongEntity.toSong() = Song(
    mediaId = mediaId,
    title = title ?: "",
    artist = MusicAttribute(id = artistId, name = artistName),
    album = MusicAttribute(id = albumId, name = albumName),
    albumArtist = albumArtist,
    songUrl = songUrl ?: "",
    imageUrl = imageUrl ?: "",
    bitrate = bitrate ?: Constants.ERROR_INT,
    streamBitrate = streamBitrate ?: Constants.ERROR_INT,
    catalog = catalog ?: Constants.ERROR_INT,
    channels = channels ?: Constants.ERROR_INT,
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre,
    mime = mime,
    name = name ?: "",
    playCount = playCount ?: Constants.ERROR_INT,
    playlistTrackNumber = playlistTrackNumber ?: Constants.ERROR_INT,
    rateHz = rateHz ?: Constants.ERROR_INT,
    size = size ?: Constants.ERROR_INT,
    time = time ?: Constants.ERROR_INT,
    trackNumber = trackNumber ?: Constants.ERROR_INT,
    year = year ?: Constants.ERROR_INT,
    mode = mode,
    artists = artists,
    flag = flag,
    streamFormat = streamFormat,
    format = format,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = replayGainTrackGain,
    replayGainTrackPeak = replayGainTrackGain,
    lyrics = lyrics,
    comment = comment,
    language = language,
    disk = disk,
    diskSubtitle = diskSubtitle,
    mbId = mbId,
    albumMbId = albumMbId,
    artistMbId = artistMbId,
    albumArtistMbId = albumArtistMbId,
    preciseRating = preciseRating,
    averageRating = averageRating,
    rating = rating
)

fun Song.toSongEntity(username: String, serverUrl: String) = SongEntity(
    mediaId = mediaId,
    title = title ?: "",
    artistId = artist.id,
    artistName = artist.name,
    albumId = album.id,
    albumName = album.name,
    albumArtist = albumArtist,
    songUrl = songUrl ?: "",
    imageUrl = imageUrl ?: "",
    bitrate = bitrate ?: Constants.ERROR_INT,
    streamBitrate = streamBitrate ?: Constants.ERROR_INT,
    catalog = catalog ?: Constants.ERROR_INT,
    channels = channels ?: Constants.ERROR_INT,
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre,
    mime = mime,
    name = name ?: "",
    playCount = playCount ?: Constants.ERROR_INT,
    playlistTrackNumber = playlistTrackNumber ?: Constants.ERROR_INT,
    rateHz = rateHz ?: Constants.ERROR_INT,
    size = size ?: Constants.ERROR_INT,
    time = time ?: Constants.ERROR_INT,
    trackNumber = trackNumber ?: Constants.ERROR_INT,
    year = year ?: Constants.ERROR_INT,
    mode = mode,
    artists = artists,
    flag = flag,
    streamFormat = streamFormat,
    format = format,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = replayGainTrackGain,
    replayGainTrackPeak = replayGainTrackGain,
    lyrics = lyrics,
    comment = comment,
    language = language,
    disk = disk,
    diskSubtitle = diskSubtitle,
    mbId = mbId,
    albumMbId = albumMbId,
    artistMbId = artistMbId,
    albumArtistMbId = albumArtistMbId,
    preciseRating = preciseRating,
    averageRating = averageRating,
    rating = rating,
    multiUserId = multiuserDbKey(username, serverUrl),
    searchTitle = title.normalizeForSearch()
)
