package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Session
import java.time.LocalDateTime

const val SESSION_PRIMARY_KEY = "power-ampache-2-session"
@Entity
data class SessionEntity(
    @PrimaryKey val primaryKey: String = SESSION_PRIMARY_KEY,
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
    val videos: Int
)

fun SessionEntity.toSession(): Session = Session(
    add = add,
    albums = albums ?: 0,
    api = api ?: "",
    artists = artists ?: 0,
    auth = auth ?: "",
    catalogs = catalogs,
    clean = clean,
    genres = genres,
    labels = labels,
    licenses = licenses,
    liveStreams = liveStreams,
    playlists = playlists,
    playlistsSearches = playlistsSearches,
    podcastEpisodes = podcastEpisodes,
    podcasts = podcasts,
    searches = searches,
    sessionExpire = sessionExpire,
    shares = shares,
    songs = songs,
    update = update,
    users = users,
    videos = videos
)

fun Session.toSessionEntity(): SessionEntity = SessionEntity(
    add = add,
    albums = albums ?: 0,
    api = api ?: "",
    artists = artists ?: 0,
    auth = auth ?: "",
    catalogs = catalogs,
    clean = clean,
    genres = genres,
    labels = labels,
    licenses = licenses,
    liveStreams = liveStreams,
    playlists = playlists,
    playlistsSearches = playlistsSearches,
    podcastEpisodes = podcastEpisodes,
    podcasts = podcasts,
    searches = searches,
    sessionExpire = sessionExpire,
    shares = shares,
    songs = songs,
    update = update,
    users = users,
    videos = videos
)
