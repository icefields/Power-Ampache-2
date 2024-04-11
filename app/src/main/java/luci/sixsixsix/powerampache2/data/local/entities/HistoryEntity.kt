package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class HistoryEntity(
    val mediaId: String,
    val playCount: Int,
    val lastPlayed: Long = System.currentTimeMillis(),
    val multiUserId: String,
    @PrimaryKey val id: String = "$multiUserId$mediaId"
) {
    companion object {
        fun newEntry(
            mediaId: String,
            playCount: Int,
            lastPlayed: Long = System.currentTimeMillis(),
            username: String,
            serverUrl: String
        ) = HistoryEntity(
            mediaId = mediaId,
            lastPlayed = lastPlayed,
            playCount = playCount + 1,
            multiUserId = multiuserDbKey(username = username, serverUrl = serverUrl)
        )
    }
}

fun Song.toHistoryEntity(username: String,
                         serverUrl: String,
                         lastPlayed: Long = System.currentTimeMillis()
) = HistoryEntity.newEntry(
    mediaId = mediaId,
    playCount = playCount,
    username = username,
    lastPlayed = lastPlayed,
    serverUrl = serverUrl
)
