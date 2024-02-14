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
package luci.sixsixsix.powerampache2.domain.models

import android.net.Uri
import android.os.Parcelable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.StarRating
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.common.Constants

@Parcelize
data class Song(
    val mediaId: String,
    val title: String,
    val album: MusicAttribute = MusicAttribute.emptyInstance(),
    val artist: MusicAttribute = MusicAttribute.emptyInstance(),
    val albumArtist: MusicAttribute = MusicAttribute.emptyInstance(),
    val songUrl: String = "",
    val imageUrl: String = "" ,
    val bitrate: Int = Constants.ERROR_INT,
    val streamBitrate: Int = Constants.ERROR_INT,
    val catalog: Int = Constants.ERROR_INT,
    val channels: Int = Constants.ERROR_INT,
    val composer: String = "",
    val filename: String = "",
    val genre: List<MusicAttribute> = listOf(),
    val mime: String? = null,
    val playCount: Int = Constants.ERROR_INT,
    val playlistTrackNumber: Int = Constants.ERROR_INT,
    val rateHz: Int = Constants.ERROR_INT,
    val size: Int = Constants.ERROR_INT,
    val time: Int = Constants.ERROR_INT,
    val trackNumber: Int = Constants.ERROR_INT,
    val year: Int = Constants.ERROR_INT,
    val name: String = "",
    val mode: String? = null,
    val artists: List<MusicAttribute> = listOf(),
    val flag: Int = 0,
    val streamFormat: String? = null,
    val format: String? = null,
    val streamMime: String? = null,
    val publisher: String? = null,
    val replayGainTrackGain: Float? = null,
    val replayGainTrackPeak: Float? = null,
    val disk: Int = Constants.ERROR_INT,
    val diskSubtitle: String = "",
    val mbId: String = "",
    val comment: String = "",
    val language: String = "",
    val lyrics: String = "",
    val albumMbId: String = "",
    val artistMbId: String = "",
    val albumArtistMbId: String = "",
    val averageRating: Float,
    val preciseRating: Float,
    val rating: Float,
): Comparable<Song>, Parcelable {
    override fun compareTo(other: Song): Int = mediaId.compareTo(other.mediaId)

    companion object {
        val mockSong = Song(
            mediaId = "21863",
            title = "Beauty In The Sorrow",
            artist = MusicAttribute("468", "Trivium"),
            album = MusicAttribute("1986", "The Sin And The Sentence"),
            albumArtist = MusicAttribute.emptyInstance(),
            songUrl = "http://192.168.1.100/ampache/public/play/index.php?ssid=bd15d8f22785f5176aa2f783f88616f3&type=song&oid=21863&uid=2&player=api&name=Trivium%20-%20Beauty%20In%20The%20Sorrow.mp3",
            imageUrl = "http://192.168.1.100/ampache/public/image.php?object_id=1986&object_type=album&auth=bd15d8f22785f5176aa2f783f88616f3&name=art.jpg",
            averageRating = Constants.ERROR_FLOAT,
            preciseRating = Constants.ERROR_FLOAT,
            rating = Constants.ERROR_FLOAT,
        )
    }
}

fun Song.hasLyrics() = lyrics.isNotBlank()

fun Song.totalTime(): String {
    val minutes = time / 60
    val seconds = time % 60
    return "$minutes:${if (seconds < 10) { "0" } else { "" } }${seconds}"
}

fun Song.toMediaItem(songUri: String) = MediaItem.Builder()
    .setMediaId(mediaId)
    .setUri(songUri)
    .setMimeType(mime)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
            .setDiscNumber(disk)
            .setWriter(composer)
            .setRecordingYear(year)
            .setArtworkUri(Uri.parse(imageUrl))
            .setAlbumTitle(album.name)
            .setArtist(artist.name)
            .setDisplayTitle(title)
            .setTitle(title)
            .setTrackNumber(if (trackNumber > 0) { trackNumber } else null)
            .setGenre(if (genre.isNotEmpty()) { genre[0].name } else null)
            .setComposer(composer)
            .setAlbumArtist(albumArtist.name)
            .setOverallRating(StarRating(5, if (averageRating in 0f..5f) averageRating.toFloat() else 0f))
            .setReleaseYear(year)
            .setUserRating(StarRating(5, if (rating in 0f..5f) rating.toFloat() else 0f))
            .build()
    ).build()

//fun MediaBrowser.MediaItem.toSong() = Song(
//            mediaId = mediaId!!,
//            title = description.title.toString(),
//            artist = MusicAttribute(id = "", name = description.subtitle.toString()),
//            album = MusicAttribute.emptyInstance(),
//            albumArtist = MusicAttribute.emptyInstance(),
//            songUrl = description.mediaUri.toString() ?: "",
//            imageUrl = description.iconUri.toString() ?: "",
//            bitrate = Constants.ERROR_INT,
//            catalog = Constants.ERROR_INT,
//            channels = Constants.ERROR_INT,
//            composer = "",
//            filename = "",
//            genre = listOf<MusicAttribute>(),
//            mime = null,
//            name = "",
//            playCount = Constants.ERROR_INT,
//            playlistTrackNumber = Constants.ERROR_INT,
//            rate = Constants.ERROR_INT,
//            size = Constants.ERROR_INT,
//            time = Constants.ERROR_INT,
//            trackNumber = Constants.ERROR_INT,
//            format = null, // TODO should be mp3 or whatever the format is
//            year = Constants.ERROR_INT
//        )
