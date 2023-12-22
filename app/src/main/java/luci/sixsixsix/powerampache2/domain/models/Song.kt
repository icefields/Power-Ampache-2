package luci.sixsixsix.powerampache2.domain.models

import android.media.browse.MediaBrowser
import android.os.Parcelable
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
    val catalog: Int = Constants.ERROR_INT,
    val channels: Int = Constants.ERROR_INT,
    val composer: String = "",
    val filename: String = "",
    val genre: List<MusicAttribute> = listOf(),
    val mime: String? = null,
    val playCount: Int = Constants.ERROR_INT,
    val playlistTrackNumber: Int = Constants.ERROR_INT,
    val rate: Int = Constants.ERROR_INT,
    val size: Int = Constants.ERROR_INT,
    val time: Int = Constants.ERROR_INT,
    val trackNumber: Int = Constants.ERROR_INT,
    val year: Int = Constants.ERROR_INT,
    val name: String = "",
    val mode: String? = null,
    val artists: List<MusicAttribute> = listOf(),
    val flag: Int = Constants.ERROR_INT,
    val streamFormat: String? = null,
    val streamMime: String? = null,
    val publisher: String? = null,
    val replayGainTrackGain: Float? = null ,
    val replayGainTrackPeak: Float? = null
): Comparable<Song>, Parcelable {
    override fun compareTo(other: Song): Int = mediaId.compareTo(other.mediaId)
}


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
            mime = null,
            name = "",
            playCount = Constants.ERROR_INT,
            playlistTrackNumber = Constants.ERROR_INT,
            rate = Constants.ERROR_INT,
            size = Constants.ERROR_INT,
            time = Constants.ERROR_INT,
            trackNumber = Constants.ERROR_INT,
            year = Constants.ERROR_INT
        )
