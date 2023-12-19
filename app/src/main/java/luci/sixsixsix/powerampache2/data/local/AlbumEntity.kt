package luci.sixsixsix.powerampache2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import java.time.LocalDateTime




@Entity
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val basename: String = "",
    val artist: MusicAttribute,
    val artists: List<MusicAttribute>,
    val time: Int = 0,
    val year: Int = 0,
    val songCount: Int = 0,
    val diskCount: Int = 0,
    val genre: List<MusicAttribute>,
    val artUrl: String = "",
    val flag: Int = 0,
    val rating: Int = 0,
    val averageRating: Int = 0,
)

fun AlbumEntity.toAlbum() = Album(
    id = id,
    name = name ?: "",
    basename = basename ?: "",
    artists = artists,// Gson().fromJson(artists, MusicAttributesContainer::class.java).attr,
    artist = artist, //Gson().fromJson(artist, MusicAttribute::class.java),
    artUrl = artUrl ?: "",
    songCount = songCount ?: 0,
    flag = flag ?: 0,
    time = time ?: 0,
    year = year ?: 0,
    genre = genre //Gson().fromJson(genre, MusicAttributesContainer::class.java).attr,
    )

fun Album.toAlbumEntity() = AlbumEntity(
    id = id,
    name = name ?: "",
    basename = basename ?: "",
    artists = artists, //Gson().toJson(MusicAttributesContainer(artists)) ?: "{}",
    artist = artist, //Gson().toJson(artist) ?: "{}",
    artUrl = artUrl ?: "",
    songCount = songCount ?: 0,
    flag = flag ?: 0,
    time = time ?: 0,
    year = year ?: 0,
    genre = genre //Gson().toJson(MusicAttributesContainer(genre)) ?: "{}",
)