package luci.sixsixsix.powerampache2.domain.models

import java.time.LocalDateTime

data class Session(
    val add: LocalDateTime,
    val albums: Int,
    val api: String,
    val artists: Int,
    val auth: String,
    val catalogs: Int,
    val clean: LocalDateTime,
    val genres: Int,
    val labels: Int,
    val licenses: Int,
    val liveStreams: Int,
    val playlists: Int,
    val playlistsSearches: Int,
    val podcastEpisodes: Int,
    val podcasts: Int,
    val searches: Int,
    val sessionExpire: LocalDateTime,
    val shares: Int,
    val songs: Int,
    val update: LocalDateTime,
    val users: Int,
    val videos: Int,
) {
    fun isTokenExpired(): Boolean = !LocalDateTime.now().isBefore(sessionExpire)
}
