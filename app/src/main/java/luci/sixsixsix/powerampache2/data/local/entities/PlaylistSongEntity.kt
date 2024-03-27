package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class PlaylistSongEntity(
    @PrimaryKey
    val id: String,
    val songId: String,
    val playlistId: String,
    val position: Int
) {
    companion object {
        fun newEntry(songId: String, playlistId: String, position: Int) = PlaylistSongEntity(
            id = "$songId$playlistId",
            songId = songId,
            playlistId = playlistId,
            position = position
        )

        fun newEntries(songs: List<Song>, playlistId: String) = mutableListOf<PlaylistSongEntity>().apply {
            songs.map { it.mediaId }.forEachIndexed { position,  songId ->
                add(newEntry(songId = songId, playlistId = playlistId, position = position))
            }
        }
    }
}
