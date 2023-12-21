package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session

data class AuthDto(
    @SerializedName("add")
    val add: String,
    @SerializedName("albums")
    val albums: Int?,
    @SerializedName("api")
    val api: String?,
    @SerializedName("artists")
    val artists: Int?,
    @SerializedName("auth")
    val auth: String?,
    @SerializedName("catalogs")
    val catalogs: Int,
    @SerializedName("clean")
    val clean: String,
    @SerializedName("genres")
    val genres: Int,
    @SerializedName("labels")
    val labels: Int,
    @SerializedName("licenses")
    val licenses: Int,
    @SerializedName("live_streams")
    val liveStreams: Int,
    @SerializedName("playlists")
    val playlists: Int,
    @SerializedName("playlists_searches")
    val playlistsSearches: Int,
    @SerializedName("podcast_episodes")
    val podcastEpisodes: Int,
    @SerializedName("podcasts")
    val podcasts: Int,
    @SerializedName("searches")
    val searches: Int,
    @SerializedName("session_expire")
    val sessionExpire: String,
    @SerializedName("shares")
    val shares: Int,
    @SerializedName("songs")
    val songs: Int,
    @SerializedName("update")
    val update: String,
    @SerializedName("users")
    val users: Int,
    @SerializedName("videos")
    val videos: Int,
    @SerializedName("server")
    var server: String? = null,
    @SerializedName("version")
    var version: String? = null,
    @SerializedName("compatible")
    var compatible: String? = null,
): AmpacheBaseResponse()

fun AuthDto.toServerInfo(): ServerInfo = ServerInfo(
    server = server,
    version = version,
    compatible = compatible
)


fun AuthDto.toSession(dateMapper: DateMapper): Session = Session(
    add = dateMapper(add),
    albums = albums ?: 0,
    api = api ?: "",
    artists = artists ?: 0,
    auth = auth ?: "",
    catalogs = catalogs,
    clean = dateMapper(clean),
    genres = genres,
    labels = labels,
    licenses = licenses,
    liveStreams = liveStreams,
    playlists = playlists,
    playlistsSearches = playlistsSearches,
    podcastEpisodes = podcastEpisodes,
    podcasts = podcasts,
    searches = searches,
    sessionExpire = dateMapper(sessionExpire),
    shares = shares,
    songs = songs,
    update = dateMapper(update),
    users = users,
    videos = videos,
)
