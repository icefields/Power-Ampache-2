package luci.sixsixsix.powerampache2.domain.models

import android.media.browse.MediaBrowser
import luci.sixsixsix.powerampache2.common.Constants

data class Song(
    val mediaId: String,
    val title: String,
    val album: MusicAttribute = MusicAttribute.emptyInstance(),
    val artist: MusicAttribute = MusicAttribute.emptyInstance(),
    val albumArtist: MusicAttribute = MusicAttribute.emptyInstance(),
    val songUrl: String,
    val imageUrl: String ,
    val bitrate: Int,
    val catalog: Int,
    val channels: Int,
    val composer: String,
    val filename: String,
    val genre: List<MusicAttribute> = listOf(),
    val mime: String,
    val playCount: Int,
    val playlistTrackNumber: Int,
    val rate: Int,
    val size: Int,
    val time: Int?,
    val trackNumber: Int,
    val year: Int,
    val name: String
)

fun MediaBrowser.MediaItem.toSong() = Song(
            mediaId = mediaId!!,
            title = description.title.toString(),
            artist = MusicAttribute(id = "", name = description.subtitle.toString()),
            album = MusicAttribute.emptyInstance(),
            albumArtist = MusicAttribute.emptyInstance(),
            songUrl = description.mediaUri.toString() ?: "",
            imageUrl = description.iconUri.toString() ?: "",
            bitrate = Constants.ERROR_INT,
            catalog = Constants.ERROR_INT,
            channels = Constants.ERROR_INT,
            composer = "",
            filename = "",
            genre = listOf<MusicAttribute>(),
            mime = "",
            name = "",
            playCount = Constants.ERROR_INT,
            playlistTrackNumber = Constants.ERROR_INT,
            rate = Constants.ERROR_INT,
            size = Constants.ERROR_INT,
            time = Constants.ERROR_INT,
            trackNumber = Constants.ERROR_INT,
            year = Constants.ERROR_INT
        )


