package luci.sixsixsix.powerampache2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Playlist

@Entity
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val owner: String,
    val items: Int? = 0,
    val type: String? = null,
    val artUrl: String? = null,
)

fun PlaylistEntity.toPlaylist() = Playlist(
    id = id,
    name = name ?: "",
    owner = owner ?: "",
    items = items ?: 0,
    type = type ?: "",
    artUrl = artUrl ?: ""
)

fun Playlist.toPlaylistEntity() = PlaylistEntity(
    id = id,
    name = name ?: "",
    owner = owner ?: "",
    items = items ?: 0,
    type = type ?: "",
    artUrl = artUrl ?: ""
)
