package luci.sixsixsix.powerampache2.data.remote

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Constants.NETWORK_REQUEST_LIMIT_DEBUG
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.dto.AlbumsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.ArtistDto
import luci.sixsixsix.powerampache2.data.remote.dto.ArtistsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.AuthDto
import luci.sixsixsix.powerampache2.data.remote.dto.GoodbyeDto
import luci.sixsixsix.powerampache2.data.remote.dto.PlaylistsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.SongsResponse
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Song
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

    @GET("json.server.php?action=goodbye")
    suspend fun goodbye(@Query("auth") authKey: String = ""): GoodbyeDto

    @GET("json.server.php?action=search_songs")
    suspend fun getSongs(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = NETWORK_REQUEST_LIMIT_DEBUG,
        @Query("filter") filter: String = "",
        @Query("exact") exact: Int = 0,
        @Query("offset") offset: Int = 0,
    ): SongsResponse // TODO remove default values

    @GET("json.server.php?action=albums")
    suspend fun getAlbums(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = NETWORK_REQUEST_LIMIT_DEBUG,
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
        @Query("hide_search") hideSearch: Int = 1, // 0, 1 (if true do not include searches/smartlists in the result)
        @Query("show_dupes") showDupes: Int = 1, // 0, 1 (if true if true ignore 'api_hide_dupe_searches' setting)
    ): PlaylistsResponse // TODO remove default values

    @GET("json.server.php?action=artist")
    suspend fun getArtistInfo(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 0,
        @Query("filter") artistId: String = "",
        @Query("offset") offset: Int = 0, ): ArtistDto

    @GET("json.server.php?action=artist_albums")
    suspend fun getAlbumsFromArtist(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 0,
        @Query("filter") artistId: String = "",
        @Query("offset") offset: Int = 0, ): AlbumsResponse

    @GET("json.server.php?action=album_songs")
    suspend fun getSongsFromAlbum(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 0,
        @Query("filter") albumId: String = "",
        @Query("offset") offset: Int = 0, ): SongsResponse

    @GET("json.server.php?action=playlist_songs")
    suspend fun getSongsFromPlaylist(
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 0,
        @Query("random") random: Int = 0, // integer 0, 1 (if true get random songs using limit)
        @Query("filter") albumId: String = "",
        @Query("offset") offset: Int = 0, ): SongsResponse

// ------------------------- STATS CALLS -----------------------------------

    @GET("json.server.php?action=stats")
    suspend fun getSongsFlagged( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = NETWORK_REQUEST_LIMIT_DEBUG,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "song", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "flagged", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): SongsResponse

    @GET("json.server.php?action=stats")
    suspend fun getSongsNewest( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "song", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "newest", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): SongsResponse

    @GET("json.server.php?action=stats")
    suspend fun getSongsHighest( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "song", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "highest", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): SongsResponse

    @GET("json.server.php?action=stats")
    suspend fun getSongsFrequent( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "song", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "frequent", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): SongsResponse

    @GET("json.server.php?action=stats")
    suspend fun getSongsRecent( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "song", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "recent", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): SongsResponse

    @GET("json.server.php?action=stats")
    suspend fun getSongsRandom( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = NETWORK_REQUEST_LIMIT_DEBUG,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "song", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "random", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): SongsResponse

    @GET("json.server.php?action=stats")
    suspend fun getAlbumsFlagged( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = NETWORK_REQUEST_LIMIT_DEBUG,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "album", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "flagged", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): AlbumsResponse

    @GET("json.server.php?action=stats")
    suspend fun getAlbumsNewest( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "album", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "newest", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): AlbumsResponse

    @GET("json.server.php?action=stats")
    suspend fun getAlbumsHighest( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "album", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "highest", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): AlbumsResponse

    @GET("json.server.php?action=stats")
    suspend fun getAlbumsFrequent( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "album", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "frequent", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): AlbumsResponse

    @GET("json.server.php?action=stats")
    suspend fun getAlbumsRecent( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = 11,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "album", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "recent", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): AlbumsResponse

    @GET("json.server.php?action=stats")
    suspend fun getAlbumsRandom( // flagged = favourites
        @Query("auth") authKey: String,
        @Query("limit") limit: Int = NETWORK_REQUEST_LIMIT_DEBUG,
        //@Query("user_id") userId: Int,
        @Query("username") username: String? = null,
        @Query("type") _type: String = "album", // song, album, artist, video, playlist, podcast, podcast_episode
        @Query("filter") _filter: String = "random", // newest, highest, frequent, recent, forgotten, flagged, random
        @Query("offset") offset: Int = 0, ): AlbumsResponse
    companion object {
        const val API_KEY = "0db9dcbb4a945e443547e3c082110abf"
        const val BASE_URL = "http://localhost/"
    }
}
//""http://192.168.1.100/ampache/public/server/" //"https://demo.ampache.dev/server/" //
// http://192.168.1.100/ampache/public/server/json.server.php?action=songs&auth=5bbec52145f2761fbb1f9bb3db529d06&limit=4