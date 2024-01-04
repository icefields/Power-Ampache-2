package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.data.remote.dto.MusicAttributeDto
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
    val catalog: Int,
    val channels: Int,
    val composer: String,
    val filename: String,
    val genre: List<MusicAttribute> = listOf(),
    val mime: String? = null,
    val playCount: Int,
    val playlistTrackNumber: Int,
    val rate: Int,
    val size: Int,
    val time: Int?,
    val trackNumber: Int,
    val year: Int,
    val name: String,
    val mode: String? = null,
    val artists: List<MusicAttribute> = listOf(),
    val flag: Int = 0,
    val streamFormat: String? = null,
    val streamMime: String? = null,
    val publisher: String? = null,
    val replayGainTrackGain: Float? = null ,
    val replayGainTrackPeak: Float? = null
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
    catalog = catalog ?: Constants.ERROR_INT,
    channels = channels ?: Constants.ERROR_INT,
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre,
    mime = mime,
    name = name ?: "",
    playCount = playCount ?: Constants.ERROR_INT,
    playlistTrackNumber = playlistTrackNumber ?: Constants.ERROR_INT,
    rate = rate ?: Constants.ERROR_INT,
    size = size ?: Constants.ERROR_INT,
    time = time ?: Constants.ERROR_INT,
    trackNumber = trackNumber ?: Constants.ERROR_INT,
    year = year ?: Constants.ERROR_INT,
    mode = mode,
    artists = artists,
    flag = flag,
    streamFormat = streamFormat,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = replayGainTrackGain,
    replayGainTrackPeak = replayGainTrackGain,
)

fun Song.toSongEntity() = SongEntity(
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
    catalog = catalog ?: Constants.ERROR_INT,
    channels = channels ?: Constants.ERROR_INT,
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre,
    mime = mime,
    name = name ?: "",
    playCount = playCount ?: Constants.ERROR_INT,
    playlistTrackNumber = playlistTrackNumber ?: Constants.ERROR_INT,
    rate = rate ?: Constants.ERROR_INT,
    size = size ?: Constants.ERROR_INT,
    time = time ?: Constants.ERROR_INT,
    trackNumber = trackNumber ?: Constants.ERROR_INT,
    year = year ?: Constants.ERROR_INT,
    mode = mode,
    artists = artists,
    flag = flag,
    streamFormat = streamFormat,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = replayGainTrackGain,
    replayGainTrackPeak = replayGainTrackGain,
)