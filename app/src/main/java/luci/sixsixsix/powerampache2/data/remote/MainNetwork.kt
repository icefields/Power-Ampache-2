package luci.sixsixsix.powerampache2.data.remote

import luci.sixsixsix.powerampache2.common.Constants.NETWORK_REQUEST_LIMIT_DEBUG
import luci.sixsixsix.powerampache2.data.remote.dto.AlbumsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.ArtistsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.AuthDto
import luci.sixsixsix.powerampache2.data.remote.dto.PlaylistsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.SongsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Main network interface which will fetch a new welcome title for us
 */
interface MainNetwork {
    @GET("json.server.php?action=handshake")
    suspend fun authorize(@Query("auth") apiKey: String = API_KEY): AuthDto

    @GET("json.server.php?action=handshake")
    suspend fun authorize(
        @Query("auth") authHash: String,
        @Query("user") user: String,
        @Query("timestamp") timestamp: Long
    ): AuthDto

    @GET("json.server.php?action=ping")
    suspend fun ping(@Query("auth") authKey: String = ""): AuthDto

    @GET("json.server.php?action=songs")
    suspend fun getAllSongs(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 0,
        @Query("filter") filter: String = "",
        @Query("exact") exact: Int = 0,
        @Query("offset") offset: Int = 0,
    ): SongsResponse // TODO remove default values

    @GET("json.server.php?action=albums")
    suspend fun getAllAlbums(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 0,
        @Query("filter") filter: String = "",
        @Query("exact") exact: Int = 0,
        @Query("offset") offset: Int = 0,
        @Query("include") include: String = "", // albums, songs (includes track list)
    ): AlbumsResponse // TODO remove default values

    @GET("json.server.php?action=artists")
    suspend fun getArtists(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = NETWORK_REQUEST_LIMIT_DEBUG,
        @Query("filter") filter: String = "",
        @Query("exact") exact: Int = 0,
        @Query("offset") offset: Int = 0,
        @Query("include") include: String = "", // albums, songs (includes track list)
    ): ArtistsResponse // TODO remove default values

    @GET("json.server.php?action=playlists")
    suspend fun getPlaylists(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 0,
        @Query("filter") filter: String = "",
        @Query("exact") exact: Int = 0,
        @Query("offset") offset: Int = 0,
        @Query("hide_search") hideSearch: Int = 0, // 0, 1 (if true do not include searches/smartlists in the result)
        @Query("show_dupes") showDupes: Int = 0, // 0, 1 (if true if true ignore 'api_hide_dupe_searches' setting)
    ): PlaylistsResponse // TODO remove default values

    companion object {
        const val API_KEY = "0db9dcbb4a945e443547e3c082110abf"
        const val BASE_URL = "https://demo.ampache.dev/server/" // "http://192.168.1.100/ampache/public/server/"
        const val DEMO_USER = "demo"
        const val DEMO_PASSWORD = "demodemo"
    }
}

// http://192.168.1.100/ampache/public/server/json.server.php?action=songs&auth=5bbec52145f2761fbb1f9bb3db529d06&limit=4