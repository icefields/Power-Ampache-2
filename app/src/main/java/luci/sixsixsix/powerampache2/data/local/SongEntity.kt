package luci.sixsixsix.powerampache2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.data.remote.dto.SongDto
import luci.sixsixsix.powerampache2.data.remote.dto.toMusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class SongEntity(
    @PrimaryKey val mediaId: String,
    val title: String,
    val album: MusicAttribute = MusicAttribute.emptyInstance(),
    val artist: MusicAttribute = MusicAttribute.emptyInstance(),
    val albumArtist: MusicAttribute = MusicAttribute.emptyInstance(),
    val songUrl: String,
    val imageUrl: String,
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

fun SongEntity.toSong() = Song(
    mediaId = mediaId,
    title = title ?: "",
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    songUrl = songUrl ?: "",
    imageUrl = imageUrl ?: "",
    bitrate = bitrate ?: Constants.ERROR_INT,
    catalog = catalog ?: Constants.ERROR_INT,
    channels = channels ?: Constants.ERROR_INT,
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre,
    mime = mime ?: "",
    name = name ?: "",
    playCount = playCount ?: Constants.ERROR_INT,
    playlistTrackNumber = playlistTrackNumber ?: Constants.ERROR_INT,
    rate = rate ?: Constants.ERROR_INT,
    size = size ?: Constants.ERROR_INT,
    time = time ?: Constants.ERROR_INT,
    trackNumber = trackNumber ?: Constants.ERROR_INT,
    year = year ?: Constants.ERROR_INT
)

fun Song.toSongEntity() = SongEntity(
    mediaId = mediaId,
    title = title ?: "",
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    songUrl = songUrl ?: "",
    imageUrl = imageUrl ?: "",
    bitrate = bitrate ?: Constants.ERROR_INT,
    catalog = catalog ?: Constants.ERROR_INT,
    channels = channels ?: Constants.ERROR_INT,
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre,
    mime = mime ?: "",
    name = name ?: "",
    playCount = playCount ?: Constants.ERROR_INT,
    playlistTrackNumber = playlistTrackNumber ?: Constants.ERROR_INT,
    rate = rate ?: Constants.ERROR_INT,
    size = size ?: Constants.ERROR_INT,
    time = time ?: Constants.ERROR_INT,
    trackNumber = trackNumber ?: Constants.ERROR_INT,
    year = year ?: Constants.ERROR_INT
)
