package luci.sixsixsix.powerampache2.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.data.local.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.toAlbum
import luci.sixsixsix.powerampache2.data.local.toAlbumEntity
import luci.sixsixsix.powerampache2.data.local.toArtist
import luci.sixsixsix.powerampache2.data.local.toArtistEntity
import luci.sixsixsix.powerampache2.data.local.toPlaylist
import luci.sixsixsix.powerampache2.data.local.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.local.toSession
import luci.sixsixsix.powerampache2.data.local.toSessionEntity
import luci.sixsixsix.powerampache2.data.local.toSong
import luci.sixsixsix.powerampache2.data.local.toSongEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
import luci.sixsixsix.powerampache2.data.remote.dto.toBoolean
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.data.remote.dto.toServerInfo
import luci.sixsixsix.powerampache2.data.remote.dto.toSession
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val dateMapper: DateMapper,
    private val db: MusicDatabase
): MusicRepository {
    private var serverInfo: ServerInfo? = null
    private val dao = db.dao

    private suspend fun getSession(): Session? {
        Log.d("aaaa", "GET_SESSION ${dao.getSession()?.toSession()}")
        return dao.getSession()?.toSession()
    }

    private suspend fun setSession(se: Session) {
        dao.updateSession(se.toSessionEntity())
    }

    private suspend fun getCredentials(): CredentialsEntity? {
        return dao.getCredentials()
    }

    private suspend fun setCredentials(se: CredentialsEntity) {
        Log.d("aaaa", "setCredentials ${se}")
        dao.updateCredentials(se)
    }

    override suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading(true))
        val resp = getSession()?.auth?.let {
            api.goodbye(it)
        }

        dao.clearCredentials()
        dao.clearSession()
        dao.clearAlbums()
        dao.clearSongs()
        dao.clearArtists()
        dao.clearPlaylists()
        Log.d("aaaa", "LOGOUT ${resp}")

        if (resp?.toBoolean() == true) {
            emit(Resource.Success(true))
        } else {
            throw Exception("there is an error in the logout response")
        }

        emit(Resource.Loading(false))
    }.catch { e ->
        when(e) {
            is IOException ->
                emit(Resource.Error(message = "cannot load data IOException $e", exception = e))
            is HttpException ->
                emit(Resource.Error(message = "cannot load data HttpException $e", exception = e))
            is MusicException ->
                emit(Resource.Error(message = e.musicError.toString(), exception = e))
            else ->
                emit(Resource.Error(message = "generic exception $e", exception = e))
        }
    }

    override suspend fun ping(): Resource<Pair<ServerInfo, Session?>> =
        try {
            val session = getSession()
            val resp = api.ping(session?.auth ?: "")

            // Updated session only valid of previous session exists, authorize otherwise
            session?.let { oldSession ->
                try {
                    // add credentials to the new session
                    resp.toSession(dateMapper)
                } catch (e: Exception) {
                    null
                }?.let {se ->
                    se.auth?.let {
                        // save new session if auth is not null
                        setSession(se)
                    }
                }
            }

            // server info always available
            serverInfo = resp.toServerInfo()
            Resource.Success(Pair(serverInfo!!, getSession()))
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }

    override suspend fun autoLogin(): Flow<Resource<Session>> {
        val credentials = getCredentials()
        // authorization with empty string will fail
        return authorize(
            credentials?.username ?: "",
            credentials?.password ?: "",
            credentials?.serverUrl ?: ""
        )
    }

    override suspend fun authorize(
        username: String,
        password: String,
        serverUrl: String,
        force: Boolean
    ): Flow<Resource<Session>> = flow {
        emit(Resource.Loading(true))
        // save current credentials, so they can be picked up by the interceptor, and for future autologin
        setCredentials(CredentialsEntity(username = username, password = password, serverUrl = serverUrl))
        Log.d("aaaa","authorize CREDENTIALS ${getCredentials()}")
        emit(Resource.Success(authorize2(username, password, serverUrl, force)))
        emit(Resource.Loading(false))
    }.catch { e ->
        when(e) {
            is IOException ->
                emit(Resource.Error(message = "cannot load data IOException $e", exception = e))
            is HttpException ->
                emit(Resource.Error(message = "cannot load data HttpException $e", exception = e))
            is MusicException ->
                emit(Resource.Error(message = e.musicError.toString(), exception = e))
            else ->
                emit(Resource.Error(message = "generic exception $e", exception = e))
        }
    }

    @Throws(Exception::class)
    private suspend fun authorize2(
        username:String,
        password:String,
        serverUrl:String,
        force: Boolean
    ): Session {
        var session = getSession()
        if (session == null || session.isTokenExpired() || force) {
            val timestamp = Instant.now().epochSecond
            val authHash = "$timestamp${password.sha256()}".sha256()

            Log.d("aaaa", "hashed password:${serverUrl} ${password.sha256()} \ntimestamp: ${timestamp}\ntimestamp+hashedPass: $timestamp${password.sha256()} \nauthHash: $authHash")

            val auth = api.authorize(authHash = authHash, user = username, timestamp = timestamp)
            auth.error?.let {
                throw (MusicException(it.toError()))
            }
            auth.auth?.let {
                Log.d("aaaa", "NEW auth ${auth}")
                setSession(auth.toSession(dateMapper))
                session = getSession()
                Log.d("aaaa", "auth token was null or expired ${session?.sessionExpire}, \nisTokenExpired? ${session?.isTokenExpired()}, \nnew auth ${session?.auth}")
            }
        }
        return getSession()!! // will throw exception if session null
    }


    override suspend fun getSongs(fetchRemote: Boolean, query: String): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))

        // db
        val localSongs = dao.searchSong(query)
        val isDbEmpty = localSongs.isEmpty() && query.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localSongs.map { it.toSong() }))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        // network
        val auth = getSession()!!//authorize2(false)
        val resp1 = api.getSongs(auth.auth, filter = query)
        resp1.error?.let { throw(MusicException(it.toError())) }
        val songs = resp1.songs!!.map { it.toSong() } // will throw exception if songs null
        //emit(Resource.Success(songs)) // will throw exception if songs null

        // db
        dao.clearSongs()
        dao.insertSongs(songs.map { it.toSongEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchSong("").map { it.toSong() }))
        emit(Resource.Loading(false))
    }.catch { e ->
        when(e) {
            is IOException ->
                emit(Resource.Error(message = "cannot load data IOException $e", exception = e))
            is HttpException ->
                emit(Resource.Error(message = "cannot load data HttpException $e", exception = e))
            is MusicException ->
                emit(Resource.Error(message = e.musicError.toString(), exception = e))
            else ->
                emit(Resource.Error(message = "generic exception $e", exception = e))
        }
    }

    override suspend fun getAlbums(fetchRemote: Boolean, query: String): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))

        val localListings = dao.searchAlbum(query)
        val isDbEmpty = localListings.isEmpty() && query.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localListings.map { it.toAlbum() }))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        val auth = getSession()!!//authorize2(false)
        val resp1 = api.getAlbums(auth.auth, filter = query)
        resp1.error?.let { throw(MusicException(it.toError())) }
        val albums = resp1.albums!!.map { it.toAlbum() } // will throw exception if songs null
        //emit(Resource.Success(albums))

        dao.clearAlbums()
        dao.insertAlbums(albums.map { it.toAlbumEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchAlbum("").map { it.toAlbum() }))

        emit(Resource.Loading(false))
    }.catch { e ->
        when(e) {
            is IOException ->
                emit(Resource.Error(message = "cannot load data IOException $e", exception = e))
            is HttpException ->
                emit(Resource.Error(message = "cannot load data HttpException $e", exception = e))
            is MusicException ->
                emit(Resource.Error(message = e.musicError.toString(), exception = e))
            else ->
                emit(Resource.Error(message = "generic exception $e", exception = e))
        }
    }

    override suspend fun getArtists(
        fetchRemote: Boolean,
        query: String
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))

        val localArtists = dao.searchArtist(query)
        val isDbEmpty = localArtists.isEmpty() && query.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localArtists.map { it.toArtist() }))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        val auth = getSession()!! //authorize2(false)
        val response = api.getArtists(auth.auth, filter = query)
        response.error?.let { throw(MusicException(it.toError())) }
        val artists = response.artists!!.map { it.toArtist() }
        //emit(Resource.Success(artists)) // will throw exception if songs null

        dao.clearArtists()
        dao.insertArtists(artists.map { it.toArtistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchArtist("").map { it.toArtist() }))

        emit(Resource.Loading(false))
    }.catch { e ->
        when(e) {
            is IOException ->
                emit(Resource.Error(message = "cannot load data IOException $e", exception = e))
            is HttpException ->
                emit(Resource.Error(message = "cannot load data HttpException $e", exception = e))
            is MusicException ->
                emit(Resource.Error(message = e.musicError.toString(), exception = e))
            else ->
                emit(Resource.Error(message = "generic exception $e", exception = e))
        }
    }

    override suspend fun getPlaylists(
        fetchRemote: Boolean,
        query: String
    ): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading(true))

        val localPlaylists = dao.searchPlaylists(query)
        val isDbEmpty = localPlaylists.isEmpty() && query.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localPlaylists.map { it.toPlaylist() }))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        val auth = getSession()!!//authorize2(false)
        val response = api.getPlaylists(auth.auth, filter = query)
        response.error?.let { throw(MusicException(it.toError())) }
        val playlists = response.playlist!!.map { it.toPlaylist() }
        //emit(Resource.Success(playlists)) // will throw exception if songs null

        dao.clearPlaylists()
        dao.insertPlaylists(playlists.map { it.toPlaylistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchPlaylists("").map { it.toPlaylist() }))

        emit(Resource.Loading(false))
    }.catch { e ->
        when(e) {
            is IOException ->
                emit(Resource.Error(message = "cannot load data IOException $e", exception = e))
            is HttpException ->
                emit(Resource.Error(message = "cannot load data HttpException $e", exception = e))
            is MusicException ->
                emit(Resource.Error(message = e.musicError.toString(), exception = e))
            else ->
                emit(Resource.Error(message = "generic exception $e", exception = e))
        }
    }
}
