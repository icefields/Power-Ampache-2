package luci.sixsixsix.powerampache2.data

import androidx.lifecycle.map
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toSessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.toUser
import luci.sixsixsix.powerampache2.data.local.entities.toUserEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toBoolean
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toServerInfo
import luci.sixsixsix.powerampache2.data.remote.dto.toSession
import luci.sixsixsix.powerampache2.data.remote.dto.toUser
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.User
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
@OptIn(DelicateCoroutinesApi::class)
@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val dateMapper: DateMapper,
    private val db: MusicDatabase,
    private val errorHandler: ErrorHandler
): MusicRepository {
    private var serverInfo: ServerInfo? = null
    private val dao = db.dao
    override val sessionLiveData = dao.getSessionLiveData().map { it?.toSession() }

    init {
        // Things to do when we get new or different session
        // user will itself emit a user object to observe
        sessionLiveData.observeForever { session ->
            session?.auth?.let {
                GlobalScope.launch {
                    getUser()
                }
            }
        }
    }

    override suspend fun userLiveData() = dao.getUserLiveData().map { it?.toUser() }

    private suspend fun getSession(): Session? = dao.getSession()?.toSession()

    private suspend fun setSession(se: Session) {
        if (se.auth != getSession()?.auth) {
            // albums, songs, playlists and artist have links that contain the auth token
            dao.clearCachedData()
            L("setSession CLEARING DATABASE")
        }
        dao.updateSession(se.toSessionEntity())
    }

    private suspend fun getCredentials(): CredentialsEntity? = dao.getCredentials()

    private suspend fun getUser() {
        getCredentials()?.username?.let { username ->
            setUser(
                api.getUser(
                    authKey = getSession()?.auth ?: "",
                    username = username
                ).toUser()
            )
        }
    }

    private suspend fun setUser(user: User) = dao.updateUser(user.toUserEntity())

    private suspend fun setCredentials(se: CredentialsEntity) = dao.updateCredentials(se)

    override suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading(true))

        // clear database first so even with lack of connection the user will be logged out
        val currentAuth = getSession()?.auth // need this to make the logout call
        dao.clearCredentials()
        dao.clearSession()
        dao.clearCachedData()
        dao.clearUser()

        val resp = currentAuth?.let {
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
        val auth = tryAuthorize(username, sha256password, authToken, force)
        emit(Resource.Success(auth))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("authorize()", e, this) }

    @Throws(Exception::class)
    private suspend fun tryAuthorize(
        username:String,
        sha256password:String,
        authToken: String,
        force: Boolean,
    ): Session {
        var session = getSession()
        if (session == null || session.isTokenExpired() || force) {
            val auth = if (authToken.isBlank()) {
                    val timestamp = Instant.now().epochSecond
                    val authHash = "$timestamp$sha256password".sha256()
                    api.authorize(authHash = authHash, user = username, timestamp = timestamp)
                } else {
                    api.authorize(apiKey = authToken)
                }
            auth.error?.let { throw (MusicException(it.toError())) }
            auth.auth?.let {
                L("NEW auth $auth")
                auth.toSession(dateMapper).also { sess ->
                    setSession(sess)
                    L("auth token was null or expired", sess.sessionExpire,
                        "\nisTokenExpired?", sess.isTokenExpired(),
                        "new auth", sess.auth)
                }
            }
        }
        return getSession()!! // will throw exception if session null
    }
}
