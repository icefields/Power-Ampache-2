package luci.sixsixsix.powerampache2.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song

@Parcelize
data class SongUI(
    val mediaId: String,
    override val id: String = mediaId,
    val title: String,
    val album: MusicAttribute,
    val artist: MusicAttribute,
    val albumArtist: MusicAttribute,
    val songUrl: String,
    val imageUrl: String,
    val bitrate: Int,
    val streamBitrate: Int,
    val catalog: Int,
    val channels: Int,
    val composer: String,
    val filename: String,
    val genre: List<MusicAttribute>,
    val mime: String?,
    val playCount: Int,
    val playlistTrackNumber: Int,
    val rateHz: Int,
    val size: Int,
    val time: Int,
    val trackNumber: Int,
    val year: Int,
    val name: String,
    val mode: String?,
    val artists: List<MusicAttribute>,
    val flag: Int,
    val streamFormat: String?,
    val format: String?,
    val streamMime: String?,
    val publisher: String?,
    val replayGainTrackGain: Float?,
    val replayGainTrackPeak: Float?,
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
    val isDownloaded: Boolean,
): Comparable<SongUI>, Parcelable, AmpacheModel {
    override fun compareTo(other: SongUI): Int = mediaId.compareTo(other.mediaId)

    companion object {
        val mockSongUI = Song(
            mediaId = "12345",
            title = "Stabwound",
            artist = MusicAttribute("666", "Necrophagist"),
            album = MusicAttribute("2004", "Epitaph"),
            albumArtist = MusicAttribute.emptyInstance(),
            genre = listOf(MusicAttribute.randomInstance(),
                MusicAttribute.randomInstance(),
                MusicAttribute.randomInstance()
            ),
            songUrl = "http://192.168.200.200/ampache/public/play/index.php?ssid=bd15d8f22785f5176aa2f783f88616f3&type=song&oid=12345&uid=1&player=api&name=Necrophagist%20-%20Stabwound.mp3",
            imageUrl = "http://192.168.200.200/ampache/public/image.php?object_id=1986&object_type=album&auth=bd15d8f22785f5176aa2f783f88616f3&name=art.jpg",
            averageRating = Constants.ERROR_FLOAT,
            preciseRating = Constants.ERROR_FLOAT,
            rating = Constants.ERROR_FLOAT,
        ).toSongUI(true)

        fun mapSongs(songs: List<SongUI>) = LinkedHashMap<String, SongUI>().apply {
            songs.forEach {
                put(it.mediaId, it)
            }
        }
    }
}

fun SongUI.isAvailableOffline() = isDownloaded

fun SongUI.hasLyrics() = lyrics.isNotBlank()

fun SongUI.isFavourite() = flag != 0

fun SongUI.totalTime(): String {
    val minutes = time / 60
    val seconds = time % 60
    return "$minutes:${if (seconds < 10) { "0" } else { "" } }${seconds}"
}

fun List<SongUI>.reduceList() = if (size > Constants.config.queueSizeLimit) {
    subList(0, Constants.config.queueSizeLimit) } else this

fun Song.toSongUI(isDownloaded: Boolean = false) = SongUI(
    mediaId = mediaId,
    title = title,
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    songUrl = songUrl,
    imageUrl = imageUrl,
    bitrate = bitrate,
    streamBitrate = streamBitrate,
    catalog = catalog,
    channels = channels,
    composer = composer,
    filename = filename,
    genre = genre,
    mime = mime,
    name = name,
    playCount = playCount,
    playlistTrackNumber = playlistTrackNumber,
    rateHz = rateHz,
    size = size,
    time = time,
    trackNumber = trackNumber,
    year = year,
    mode = mode,
    artists = artists,
    flag = flag,
    streamFormat = streamFormat,
    format = format,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = replayGainTrackGain,
    replayGainTrackPeak = replayGainTrackPeak,
    lyrics = lyrics,
    comment = comment,
    language = language,
    disk = disk,
    diskSubtitle = diskSubtitle,
    mbId = mbId,
    albumMbId = albumMbId,
    artistMbId = artistMbId,
    albumArtistMbId = albumArtistMbId,
    rating = rating,
    preciseRating = preciseRating,
    averageRating = averageRating,
    isDownloaded = isDownloaded,
)

fun SongUI.toDomainSong() = Song(
    mediaId = mediaId,
    title = title,
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    songUrl = songUrl,
    imageUrl = imageUrl,
    bitrate = bitrate,
    streamBitrate = streamBitrate,
    catalog = catalog,
    channels = channels,
    composer = composer,
    filename = filename,
    genre = genre,
    mime = mime,
    name = name,
    playCount = playCount,
    playlistTrackNumber = playlistTrackNumber,
    rateHz = rateHz,
    size = size,
    time = time,
    trackNumber = trackNumber,
    year = year,
    mode = mode,
    artists = artists,
    flag = flag,
    streamFormat = streamFormat,
    format = format,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = replayGainTrackGain,
    replayGainTrackPeak = replayGainTrackPeak,
    lyrics = lyrics,
    comment = comment,
    language = language,
    disk = disk,
    diskSubtitle = diskSubtitle,
    mbId = mbId,
    albumMbId = albumMbId,
    artistMbId = artistMbId,
    albumArtistMbId = albumArtistMbId,
    rating = rating,
    preciseRating = preciseRating,
    averageRating = averageRating,
)

suspend fun List<Song>.toSongUI(isAvailableOfflineCallback: suspend (Song) -> Boolean): List<SongUI> {
    val songUIList = mutableListOf<SongUI>()
    this.forEach {
        songUIList.add(
            it.toSongUI(
                isAvailableOfflineCallback(it)
            )
        )
    }
    return songUIList
}

fun List<SongUI>.toDomainSong(): List<Song> {
    val songList = mutableListOf<Song>()
    this.forEach {
        songList.add(
            it.toDomainSong()
        )
    }
    return songList
}