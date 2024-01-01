package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import luci.sixsixsix.powerampache2.common.Constants.CLEAR_TABLE_AFTER_FETCH
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toArtist
import luci.sixsixsix.powerampache2.data.local.entities.toArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylist
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toSessionEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
import luci.sixsixsix.powerampache2.data.remote.dto.toBoolean
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.data.remote.dto.toServerInfo
import luci.sixsixsix.powerampache2.data.remote.dto.toSession
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val dateMapper: DateMapper,
    private val db: MusicDatabase,
    private val playlistManager: MusicPlaylistManager,
    private val errorHandler: ErrorHandler
): MusicRepository {
    private var serverInfo: ServerInfo? = null
    private val dao = db.dao

    private suspend fun getSession(): Session? {
        L("GET_SESSION ${dao.getSession()?.toSession()}")
        return dao.getSession()?.toSession()
    }

    private suspend fun setSession(se: Session) {
        if (se.auth != getSession()?.auth) {
            // albums, songs, playlists and artist have links that contain the auth token
            dao.clearCachedData()
            L("setSession CLEARING DATABASE")
        }
        dao.updateSession(se.toSessionEntity())
    }

    private suspend fun getCredentials(): CredentialsEntity? {
        return dao.getCredentials()
    }

    private suspend fun setCredentials(se: CredentialsEntity) {
        L("setCredentials $se")
        dao.updateCredentials(se)
    }

    override suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading(true))

        // clear database first so even with lack of connection the user will be logged out
        dao.clearCredentials()
        dao.clearSession()
        dao.clearCachedData()

        val resp = getSession()?.auth?.let {
            api.goodbye(it)
        }

        L( "LOGOUT $resp")

        if (resp?.toBoolean() == true) {
            emit(Resource.Success(true))
        } else {
            throw Exception("there is an error in the logout response")
        }

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("logout()", e, this) }

    override suspend fun ping(): Resource<Pair<ServerInfo, Session?>> =
        try {
            val dbSession = getSession()
            val pingResponse = api.ping(dbSession?.auth ?: "")

            // Updated session only valid of previous session exists, authorize otherwise
            dbSession?.let {
                try {
                    // add credentials to the new session
                    pingResponse.toSession(dateMapper)
                    // TODO Check connection error before making this call crash into the try-catch
                } catch (e: Exception) {
                    dao.clearSession()
                    null
                }?.let { se ->
                    se.auth?.let {
                        // save new session if auth is not null
                        setSession(se)
                    }
                }
            }

            // server info always available
            serverInfo = pingResponse.toServerInfo()
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
            credentials?.serverUrl ?: "",
            credentials?.authToken ?: "",
            true
        )
    }

    override suspend fun authorize(
        username: String,
        sha256password: String,
        serverUrl: String,
        authToken: String,
        force: Boolean
    ): Flow<Resource<Session>> = flow {
        emit(Resource.Loading(true))
        //   Save current credentials, so they can be picked up by the interceptor,
        // and for future autologin, this has to be first line of code before any network call
        setCredentials(CredentialsEntity(username = username, password = sha256password, serverUrl = serverUrl, authToken = authToken))
        L("authorize CREDENTIALS ${getCredentials()}")
        val auth = tryAuthorize(username, sha256password, serverUrl, authToken, force)
        emit(Resource.Success(auth))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("authorize()", e, this) }

    @Throws(Exception::class)
    private suspend fun tryAuthorize(
        username:String,
        sha256password:String,
        serverUrl:String,
        authToken: String,
        force: Boolean,
    ): Session {
        var session = getSession()
        if (session == null || session.isTokenExpired() || force) {
            val auth = if (authToken.isBlank()) {
                    val timestamp = Instant.now().epochSecond
                    val authHash = "$timestamp$sha256password".sha256()
                    L("hashed password:${serverUrl} $sha256password \ntimestamp: ${timestamp}\ntimestamp+hashedPass: $timestamp${sha256password} \nauthHash: $authHash")
                    api.authorize(authHash = authHash, user = username, timestamp = timestamp)
                } else {
                    api.authorize(apiKey = authToken)
                }

            auth.error?.let {
                throw (MusicException(it.toError()))
            }

            auth.auth?.let {
                L("NEW auth $auth")
                setSession(auth.toSession(dateMapper))

                // TODO remove logs
                session = getSession()
                L("auth token was null or expired", session?.sessionExpire,
                    "\nisTokenExpired?", session?.isTokenExpired(),
                    "new auth", session?.auth)
            }
        }
        return getSession()!! // will throw exception if session null
    }

    override suspend fun getArtist(
        artistId: String,
        fetchRemote: Boolean,
    ): Flow<Resource<Artist>> = flow {
        emit(Resource.Loading(true))

        dao.getArtist(artistId)?.let { artistEntity ->
            emit(Resource.Success(data = artistEntity.toArtist() ))
            if(!fetchRemote) {  // load cache only?
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!
        val response = api.getArtistInfo(authKey = auth.auth, artistId = artistId)
        val artist = response.toArtist()  //will throw exception if artist null

//        if (CLEAR_TABLE_AFTER_FETCH) { dao.clearArtists() }

        dao.insertArtists(listOf(artist.toArtistEntity()))
        // stick to the single source of truth pattern despite performance deterioration
        dao.getArtist(artistId)?.let { artistEntity ->
            emit(Resource.Success(data = artistEntity.toArtist(), networkData = artist ))
        }

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    override suspend fun getArtists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
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
        }

        val auth = getSession()!!
        val response = api.getArtists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val artists = response.artists!!.map { it.toArtist() } //will throw exception if artist null

        if (query.isNullOrBlank() && offset == 0 && CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearArtists()
        }
        dao.insertArtists(artists.map { it.toArtistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchArtist(query).map { it.toArtist() }, networkData = artists))

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    override suspend fun getPlaylists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
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
        }

        val auth = getSession()!!
        val response = api.getPlaylists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val playlists = response.playlist!!.map { it.toPlaylist() } // will throw exception if playlist null

        if (query.isNullOrBlank() && offset == 0 && CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearPlaylists()
        }
        dao.insertPlaylists(playlists.map { it.toPlaylistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchPlaylists(query).map { it.toPlaylist() }, networkData = playlists))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylists()", e, this) }


//    companion object {
//        suspend fun <T> handleError(
//            label:String = "",
//            e: Throwable,
//            fc: FlowCollector<Resource<T>>,
//            onError: (message: String, e: Throwable) -> Unit
//        ) {
//            StringBuilder(label)
//                .append(if (label.isBlank())"" else " - ")
//                .append( when(e) {
//                    is IOException -> "cannot load data IOException $e"
//                    is HttpException -> "cannot load data HttpException $e"
//                    is MusicException -> {
//                        if (e.musicError.isSessionExpiredError()) {
//                            // TODO clear session and try to autologin using the saved credentials
//                        } else if (e.musicError.isEmptyResult()) {
//                            // TODO handle empty result
//                        }
//                        e.musicError.toString()
//                    }
//                    else -> "generic exception $e"
//                }).toString().apply {
//                    fc.emit(Resource.Error<T>(message = this, exception = e))
//                    onError(this, e)
//                }
//        }
//    }
}
