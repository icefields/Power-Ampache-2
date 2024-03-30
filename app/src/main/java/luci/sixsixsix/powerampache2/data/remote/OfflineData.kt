package luci.sixsixsix.powerampache2.data.remote

import luci.sixsixsix.powerampache2.common.Stack
import luci.sixsixsix.powerampache2.domain.models.Song

object OfflineData {
    val songsToScrobble: Stack<Song> = mutableListOf()
    val likedOffline: Stack<LikeData> = mutableListOf()
    val ratedOffline: Stack<RateData> = mutableListOf()
}

data class RateData(
    val id: String,
    val rating: Int,
    val type: MainNetwork.Type
)

data class LikeData(
    val id: String,
    val like: Boolean,
    val type: MainNetwork.Type
)
