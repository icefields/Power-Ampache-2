package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Playlist

@Entity
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val owner: String,
    val items: Int? = 0,
    val type: String? = null,
    val artUrl: String? = null,
    val flag: Int = 0,
    val preciseRating: Float = 0.0f,
    val rating: Int = 0,
    val averageRating: Float = 0.0f,
)

fun PlaylistEntity.toPlaylist() = Playlist(
    id = id,
    name = name ?: "",
    owner = owner ?: "",
    items = items ?: 0,
    type = type ?: "",
    artUrl = artUrl ?: "",
    flag = flag,
    preciseRating = preciseRating,
    rating = rating,
    averageRating = averageRating
)

fun Playlist.toPlaylistEntity() = PlaylistEntity(
    id = id,
    name = name ?: "",
    owner = owner ?: "",
    items = items ?: 0,
    type = type ?: "",
    artUrl = artUrl ?: "",
    flag = flag,
    preciseRating = preciseRating,
    rating = rating,
    averageRating = averageRating
)
