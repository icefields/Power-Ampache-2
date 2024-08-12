/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.data

import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Pa2Config
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toGenre
import luci.sixsixsix.powerampache2.data.local.entities.toGenreEntity
import luci.sixsixsix.powerampache2.data.local.entities.toMultiUserCredentialEntity
import luci.sixsixsix.powerampache2.data.local.entities.toMultiUserSessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toSessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.toUser
import luci.sixsixsix.powerampache2.data.local.models.UserWithCredentials
import luci.sixsixsix.powerampache2.data.local.models.toUser
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toGenre
import luci.sixsixsix.powerampache2.data.remote.dto.toPa2Config
import luci.sixsixsix.powerampache2.data.remote.dto.toServerInfo
import luci.sixsixsix.powerampache2.data.remote.dto.toSession
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
import java.time.LocalDateTime
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
    db: MusicDatabase,
    private val errorHandler: ErrorHandler
): BaseAmpacheRepository(api, db, errorHandler), MusicRepository {
    private val _serverInfoStateFlow = MutableStateFlow(ServerInfo())
    override val serverInfoStateFlow: StateFlow<ServerInfo> = _serverInfoStateFlow

    val serverVersionStateFlow = serverInfoStateFlow.mapNotNull { it.version }.distinctUntilChanged()

    override val sessionLiveData = dao.getSessionLiveData().map { it?.toSession() }

    override val userLiveData: Flow<User?> = dao.getUserLiveData().map {
        val cred = getCurrentCredentials()
        if (cred.username.isNotBlank()) {
            val userEntity = it ?: dao.getUser(cred.username)
            userEntity?.toUser() ?: UserWithCredentials(username = cred.username).toUser(cred.serverUrl)
        } else null
    }

    // used to check if a call to getUserNetwork() is necessary
    private var currentAuthToken: String? = null
    private var currentUser: User? = null

    init {
        // Things to do when we get new or different session
        // user will itself emit a user object to observe
        GlobalScope.launch {
            sessionLiveData.distinctUntilChanged().mapNotNull { it?.auth }.distinctUntilChanged().collect { newToken ->
                // if token has changed or user is null, get user from network
                if (newToken != currentAuthToken || currentUser == null) {
                    currentAuthToken = newToken
                    try {
                        currentUser = getUserNetwork()
                    } catch (e: Exception) {
                        errorHandler.logError(e)
                    }
                }
            }
        }

        GlobalScope.launch {
            initialize()
        }
    }

    private suspend fun initialize() {
        Constants.config = try {
            api.getConfig().toPa2Config()
        } catch (e: Exception) {
            L.e(e)
            Pa2Config()
        }
    }

    private suspend fun setSession(se: Session) {
        val previousCleanDate = getSession()?.clean ?: LocalDateTime.MAX

        dao.updateSession(se.toSessionEntity())
        val cred = getCurrentCredentials()
        dao.insertMultiUserSession(se.toMultiUserSessionEntity(username = cred.username, serverUrl = cred.serverUrl))


        // if a new clean is performed on server, empty library cache
        if (Constants.config.clearLibraryOnCatalogClean && se.clean.isAfter(previousCleanDate)) {
            // TODO: this has to be refactored for multi-user, right now every time a new sessions
            //  has an updated clean date, ALL the cache is emptied
            dao.clearCachedLibraryData()
        }
    }

    private suspend fun setCredentials(
        username: String,
        sha256password: String,
        authToken: String,
        serverUrl: String
    ) {
        val se = CredentialsEntity(
            username = username.lowercase(),
            password = sha256password,
            serverUrl = serverUrl/*.lowercase()*/,
            authToken = authToken
        )
        dao.updateCredentials(se)
        if (username.isNotBlank()) {
            dao.insertMultiUserCredentials(se.toMultiUserCredentialEntity())
        }
    }

    /**
     * ping the server, refresh the session stored in db, and returns 2 objects:
     *  - in case of a valid session a new Session object is return along with a server info object
     *  - in case the session is not valid, only the server info object will be returned
     */
    override suspend fun ping(): Resource<Pair<ServerInfo, Session?>> =
        try {
            val dbSession = getSession()
            val pingResponse = api.ping(authKey = dbSession?.auth)

            // Updated session only valid of previous session exists, authorize otherwise
            dbSession?.let { cachedSession ->
                try {
                    // add credentials to the new session
                    pingResponse.toSession(dateMapper)
                    // TODO Check connection error before making this call crash into the try-catch
                } catch (e: Exception) {
                    L("clear session, set the session to null ssss ping()")
                    dao.clearSession()
                    null
                } ?.let { se ->
                    if (se.auth != null) {
                        // save new session if auth is not null
                        setSession(se)
                    }
                }
            }

            // server info always available
            val servInfo = pingResponse.toServerInfo()
            L("aaa setting live data for server info ${servInfo.version}")
            _serverInfoStateFlow.value = servInfo
            Resource.Success(Pair(servInfo, getSession()))
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }

    override suspend fun autoLogin() = getCredentials()?.let {
        authorize(
            it.username,
            it.password,
            it.serverUrl,
            it.authToken,
            true
        )
    } ?: authorize("", "", "", "", true)


    override suspend fun authorize(
        username: String,
        sha256password: String,
        serverUrl: String,
        authToken: String,
        force: Boolean
    ): Flow<Resource<Session>> = flow {
        emit(Resource.Loading(true))
        val usernameLow = username.lowercase()
        //   Save current credentials, so they can be picked up by the interceptor,
        // and for future autologin, this has to be first line of code before any network call
        setCredentials(
            username = usernameLow,
            sha256password = sha256password,
            serverUrl = serverUrl/*.lowercase()*/,
            authToken = authToken
        )
        L("authorize CREDENTIALS ${getCredentials()}")
        val auth = tryAuthorize(usernameLow, sha256password, authToken, force)
        emit(Resource.Success(auth))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("authorize()", e, this) }

    @Throws(Exception::class)
    private suspend fun tryAuthorize(
        username: String,
        sha256password: String,
        authToken: String,
        force: Boolean,
    ): Session {
        val session = getSession()
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
                }
            }

            if (authToken.isNotBlank()) {
                // set the user manually because we won't have it from handshake if logged in with token
                (auth.username ?: getUserNetwork()?.username)?.let { usernameNet ->
                    setCredentials(
                        username = usernameNet.lowercase(),
                        sha256password = sha256password,
                        serverUrl = getCurrentCredentials().serverUrl,
                        authToken = authToken
                    ) }
            }
        }

        // try to get server info from ping and return the session from ping, otherwise return the
        // already saved session, as a safety net return the already saved session
        return try { ping().data?.second ?: getSession()!! } catch (e: Exception) { getSession()!! }
    }

    override suspend fun register(
        serverUrl: String,
        username: String,
        sha256password: String,
        email: String,
        fullName: String?
    ): Flow<Resource<Any>> = flow {
        emit(Resource.Loading(true))

        setCredentials(
            username = username,
            sha256password = sha256password,
            serverUrl = serverUrl/*.lowercase()*/,
            authToken = ""
        )

        val resp = api.register(
            username = username,
            password = sha256password,
            email = email,
            fullName = fullName
        )

        resp.error?.let { throw (MusicException(it.toError())) }
        resp.success?.let {
            emit(Resource.Success(it))
        } ?: run {
            // do not show anything to the user if in prod mode, log error instead
            errorHandler.logError("there is an error in the logout response.\nLOGOUT $resp")
            throw Exception(if (BuildConfig.DEBUG) "there is an error registering your account\nIs user registration allowed on the server?" else "")
        }

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("register()", e, this) }

    override suspend fun getGenres(fetchRemote: Boolean) = flow {
        emit(Resource.Loading(true))

        val localGenres = dao.getGenres()
        val isDbEmpty = localGenres.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localGenres.map { it.toGenre() }))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        val auth = authToken()
        val serverVersion = try {
            serverInfoStateFlow.value.version?.split(".")?.firstOrNull()?.let { version ->
                if (version.isDigitsOnly()) version.toInt() else Int.MAX_VALUE
            } ?: Int.MAX_VALUE
        } catch (e: Exception) { Int.MAX_VALUE } // set to max value in case of errors to force the newest api

        val response = if (serverVersion >= 5) {
            api.getGenres(authKey = auth).genres!!.map { it.toGenre() }
        } else {
            api.getTags(authKey = auth).tags!!.map { it.toGenre() }
        }

        if (Constants.CLEAR_TABLE_AFTER_FETCH) {
            dao.clearGenres()
        }
        val cred = getCurrentCredentials()
        dao.insertGenres(response.map { it.toGenreEntity(username = cred.username, serverUrl = cred.serverUrl) })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.getGenres().map { it.toGenre() }, networkData = response))

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getGenres()", e, this) }

    override suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading(true))

        // first clear db then logout to guarantee data is cleared even if the call fails
        // clear credentials after api call, since the api uses base url from credentials
        dao.clearCredentials()
        dao.clearSession()
        dao.clearCachedData()
        dao.clearUser()

        emit(Resource.Success(true))

        /*
        val url = dao.getCredentials()?.serverUrl?.let { serverUrl ->
            "${MainNetwork.buildServerUrl(serverUrl)}/json.server.php?action=goodbye&auth=${getSession()?.auth}"
        } ?: ""

         dao.clearCredentials()
        dao.clearSession()
        dao.clearCachedData()
        dao.clearUser()

        // TODO: logout is not working, clear db is enough to logout but we must invalidate the token from server
        val resp = getSession()?.auth?.let {
            api.goodbye(url)
        }

        L( "LOGOUT $resp", url)

        if (resp?.toBoolean() == true) {
            emit(Resource.Success(true))
        } else {
            // do not show anything to the user if in prod mode, log error instead
            errorHandler.logError("there is an error in the logout response.\nLOGOUT $resp")
            throw Exception(if (BuildConfig.DEBUG) "there is an error in the logout response" else "")
        }
        */

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("logout()", e, this) }
}
