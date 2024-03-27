package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.processFlag
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettings
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toUser
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.User

abstract class BaseAmpacheRepository(
    private val api: MainNetwork,
    db: MusicDatabase,
    private val errHandler: ErrorHandler
) {
    protected val dao = db.dao
    //protected var offlineModeEnabledLiveData = dao.offlineModeEnabled().distinctUntilChanged().map { it == true }

    suspend fun isOfflineModeEnabled(): Boolean =
        dao.getSettings()?.toLocalSettings()?.isOfflineModeEnabled == true

    protected suspend fun getSession(): Session? =
        dao.getSession()?.toSession()

    protected suspend fun getCredentials(): CredentialsEntity? =
        dao.getCredentials()

    suspend fun getUser(): User? =
        dao.getUser()?.toUser()

    protected suspend fun like(id: String, like: Boolean, type: MainNetwork.Type): Flow<Resource<Any>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.flag(
            authKey = auth.auth,
            id = id,
            flag = if (like) { 1 } else { 0 },
            type = type).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
                emit(Resource.Success(data = Any(), networkData = Any()))
            } else {
                throw Exception("error getting a response from FLAG/LIKE call")
            }
        }

        // update the cache
        val flag  = processFlag(like)
        when(type) {
            MainNetwork.Type.song ->
                dao.getSongById(id)?.copy(flag = flag)?.let { dbSong ->
                    dao.insertSongs(listOf(dbSong))
                }
            MainNetwork.Type.album -> dao.getAlbum(id)?.copy(flag = flag)?.let { dbAlbum ->
                dao.insertAlbums(listOf(dbAlbum))
            }
            MainNetwork.Type.artist -> dao.getArtist(id)?.copy(flag = flag)?.let { dbArtist ->
                dao.insertArtists(listOf(dbArtist))
            }
            MainNetwork.Type.playlist -> dao.getAllPlaylists()
                .firstOrNull { it.id == id}?.copy(flag = flag)?.let { dbPlaylist ->
                    dao.insertPlaylists(listOf(dbPlaylist))
                }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errHandler("likeSong()", e, this) }

    protected suspend fun rate(itemId: String, rating: Int, type: MainNetwork.Type): Flow<Resource<Any>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.rate(
            authKey = auth.auth,
            itemId = itemId,
            rating = rating,
            type = type).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
                emit(Resource.Success(data = Any(), networkData = Any()))
            } else {
                throw Exception("error getting a response from FLAG/LIKE call")
            }
        }

        // update the cache
        when(type) {
            MainNetwork.Type.song ->
                dao.getSongById(itemId)?.copy(rating = rating.toFloat())?.let { dbSong ->
                    dao.insertSongs(listOf(dbSong))
                }
            MainNetwork.Type.album -> dao.getAlbum(itemId)?.copy(rating = rating)?.let { dbAlbum ->
                dao.insertAlbums(listOf(dbAlbum))
            }
            MainNetwork.Type.artist -> {
            }
            MainNetwork.Type.playlist -> dao.getAllPlaylists()
                .firstOrNull { it.id == itemId}?.copy(rating = rating)?.let { dbPlaylist ->
                    dao.insertPlaylists(listOf(dbPlaylist))
                }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errHandler("likeSong()", e, this) }
}