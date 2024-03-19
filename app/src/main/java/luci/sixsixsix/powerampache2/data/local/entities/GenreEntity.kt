package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Genre

@Entity
data class GenreEntity (
    @PrimaryKey
    val id: String,
    val name: String,
    val albums: Int,
    val artists: Int,
    val songs: Int,
    val playlists: Int
)

fun GenreEntity.toGenre() = Genre(
    id = id,
    name = name,
    albums = albums,
    artists = artists,
    songs = songs,
    playlists = playlists
)

fun Genre.toGenreEntity() = GenreEntity(
    id = id,
    name = name,
    albums = albums,
    artists = artists,
    songs = songs,
    playlists = playlists
)
