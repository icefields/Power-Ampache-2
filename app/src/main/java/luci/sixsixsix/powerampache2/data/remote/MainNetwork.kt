package luci.sixsixsix.powerampache2.data.remote

import luci.sixsixsix.powerampache2.data.remote.dto.AuthDto
import luci.sixsixsix.powerampache2.data.remote.dto.SongsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Main network interface which will fetch a new welcome title for us
 */
interface MainNetwork {
    @GET("json.server.php?action=handshake")
    suspend fun authorize(@Query("auth") apiKey: String = API_KEY): AuthDto

    @GET("json.server.php?action=songs")
    suspend fun getAllSongs(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 22,
        @Query("filter") filter: String = "",
        @Query("exact") exact: Int = 0,
        @Query("offset") offset: Int = 4000,
    ): SongsResponse // TODO remove default values

    companion object {
        const val API_KEY = "0db9dcbb4a945e443547e3c082110abf"
        const val BASE_URL = "http://192.168.1.100/ampache/public/server/"
    }
}

// http://192.168.1.100/ampache/public/server/json.server.php?action=songs&auth=5bbec52145f2761fbb1f9bb3db529d06&limit=4