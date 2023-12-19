package luci.sixsixsix.powerampache2.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.toAlbum
import luci.sixsixsix.powerampache2.data.local.toAlbumEntity
import luci.sixsixsix.powerampache2.data.local.toSong
import luci.sixsixsix.powerampache2.data.local.toSongEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.MainNetwork.Companion.DEMO_PASSWORD
import luci.sixsixsix.powerampache2.data.remote.MainNetwork.Companion.DEMO_USER
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
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
import java.lang.NullPointerException
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
    private var session: Session? = null
    private var serverInfo: ServerInfo? = null
    private val dao = db.dao

    override suspend fun ping(): Resource<Pair<ServerInfo, Session?>> =
        try {
            val resp = api.ping(session?.let { it.auth } ?: run { "" })
            Log.d("aaaa", "PING  $resp")
            try {
                resp.toSession(dateMapper)
            } catch (e: Exception) {
                null
            }?.let {se ->
                se.auth?.let {
                    session = se
                }
            }
            serverInfo = resp.toServerInfo()
            Resource.Success(Pair(serverInfo!!, session))
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }


    override suspend fun authorize(force: Boolean): Resource<Session> =
        try {
            Resource.Success(authorize2(force))
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }

    @Throws(Exception::class)
    private suspend fun authorize2(force: Boolean): Session {
        if (session == null || session!!.isTokenExpired() || force) {
            val timestamp = Instant.now().epochSecond
            Log.d("aaaa", "${DEMO_PASSWORD.sha256()} ${timestamp} $timestamp${DEMO_PASSWORD.sha256()}")
            val auth = api.authorize(
                authHash = "$timestamp${DEMO_PASSWORD.sha256()}".sha256(),
                user = DEMO_USER,
                timestamp = timestamp
            )
            auth.error?.let {
                throw (MusicException(it.toError()))
            }
            auth.auth?.let {
                session = auth.toSession(dateMapper)
                Log.d("aaaa", "auth token was null or expired ${session?.sessionExpire}, isTokenExpired? ${session?.isTokenExpired()}, new auth ${session?.auth}")
            }
        }
        return session!! // will throw exception if session null
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
        val auth = authorize2(false)
        val resp1 = api.getAllSongs(auth.auth, filter = query)
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

        val auth = authorize2(false)
        val resp1 = api.getAllAlbums(auth.auth, filter = query)
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
        val auth = authorize2(false)
        val response = api.getArtists(auth.auth, filter = query)
        response.error?.let { throw(MusicException(it.toError())) }
        emit(Resource.Success(response.artists!!.map { it.toArtist() })) // will throw exception if songs null
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
        val auth = authorize2(false)
        val response = api.getPlaylists(auth.auth, filter = query)
        response.error?.let { throw(MusicException(it.toError())) }
        emit(Resource.Success(response.playlist!!.map { it.toPlaylist() })) // will throw exception if songs null
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
