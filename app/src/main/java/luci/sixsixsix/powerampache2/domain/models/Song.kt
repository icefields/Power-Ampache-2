package luci.sixsixsix.powerampache2.domain.models

import android.media.browse.MediaBrowser
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.md5
import luci.sixsixsix.powerampache2.data.remote.dto.toMusicAttribute

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
//    val flag: Boolean = false,
    val streamFormat: String? = null,
    val streamMime: String? = null,
    val publisher: String? = null,
    val replayGainTrackGain: Float? = null ,
    val replayGainTrackPeak: Float? = null
): Comparable<Song>, Parcelable {
    override fun compareTo(other: Song): Int = mediaId.compareTo(other.mediaId)
    override fun hashCode(): Int = key().hashCode()

    companion object {
        val mockSong = Song(
            mediaId = "21863",
            title = "Beauty In The Sorrow",
            artist = MusicAttribute("468", "Trivium"),
            album = MusicAttribute("1986", "The Sin And The Sentence"),
            albumArtist = MusicAttribute.emptyInstance(),
            songUrl = "http://192.168.1.100/ampache/public/play/index.php?ssid=bd15d8f22785f5176aa2f783f88616f3&type=song&oid=21863&uid=2&player=api&name=Trivium%20-%20Beauty%20In%20The%20Sorrow.mp3",
            imageUrl = "http://192.168.1.100/ampache/public/image.php?object_id=1986&object_type=album&auth=bd15d8f22785f5176aa2f783f88616f3&name=art.jpg",

        )
    }
}

// LISTS PERFORMANCE . urls contain the token, do not rely only on id
fun Song.key(): String = "${mediaId}${songUrl}"//.md5()

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
