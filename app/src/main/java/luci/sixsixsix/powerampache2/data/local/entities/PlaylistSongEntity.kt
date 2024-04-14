package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class PlaylistSongEntity(
    @PrimaryKey
    val id: String,
    val songId: String,
    val playlistId: String,
    val position: Int,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String
) {
    companion object {
        fun newEntry(
            songId: String,
            playlistId: String,
            position: Int,
            username: String,
            serverUrl: String
        ) = PlaylistSongEntity(
            id = "$songId$playlistId${multiuserDbKey(username, serverUrl)}",
            songId = songId,
            playlistId = playlistId,
            position = position,
            multiUserId = multiuserDbKey(username, serverUrl)
        )

        fun newEntries(
            songs: List<Song>,
                       playlistId: String,
                       username: String,
                       serverUrl: String
        ) = mutableListOf<PlaylistSongEntity>().apply {
            songs.map { it.mediaId }.forEachIndexed { position,  songId ->
                add(newEntry(songId = songId,
                    playlistId = playlistId,
                    position = position,
                    username = username,
                    serverUrl = serverUrl)
                )
            }
        }
    }
}
