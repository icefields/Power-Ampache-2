package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute

@Entity
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val albumCount: Int = 0,
    val songCount: Int = 0,
    val genre: List<MusicAttribute> = listOf(),
    val artUrl: String = "",
//    val flag: Boolean = false,
    val summary: String? = null,
    val time: Int = 0,
    val yearFormed: Int = 0,
    val placeFormed: String? = null
)

fun ArtistEntity.toArtist() = Artist(
    id = id,
    name = name ?: "",
    albumCount = albumCount ?: 0,
    songCount = songCount,
    genre = genre,
    artUrl = artUrl ?: "",
//    flag = flag ?: false,
    summary = summary,
    time = time ?: 0,
    yearFormed = yearFormed,
    placeFormed = placeFormed
)

fun Artist.toArtistEntity() = ArtistEntity(
    id = id,
    name = name ?: "",
    albumCount = albumCount ?: 0,
    songCount = songCount,
    genre = genre,
    artUrl = artUrl ?: "",
//    flag = flag ?: false,
    summary = summary,
    time = time ?: 0,
    yearFormed = yearFormed,
    placeFormed = placeFormed.toString()
)
