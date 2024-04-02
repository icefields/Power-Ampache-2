package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.processFlag
import luci.sixsixsix.powerampache2.common.push
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.UserEntity
import luci.sixsixsix.powerampache2.data.local.entities.toDownloadedSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettings
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.toUserEntity
import luci.sixsixsix.powerampache2.data.remote.LikeData
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.OfflineData
import luci.sixsixsix.powerampache2.data.remote.RateData
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toUser
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.User
import org.acra.ACRA.log

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

    suspend fun getUsername(): String? =
        dao.getUser()?.username ?: getCredentials()?.username
            .also {
                // if user doesn't exist, retrieve the object from network
                if (it == null && !isOfflineModeEnabled()) {
                    getUserNetwork()
                }
            }

    /**
     * updating the user in the database will trigger the user live data.
     * observe livedata to get updates on the user
     */
    private suspend fun setUser(user: User) {

        val userEmail = user.email ?: ""
        val userAccess = user.access ?: 0
        val userStreamToken = user.streamToken ?: ""
        val userFullNamePublic = user.fullNamePublic ?: 0
        val userFullName = user.fullName ?: ""
        val userDisabled = user.disabled ?: false
        val userCreateDate = user.createDate ?: ERROR_INT
        val userLastSeen = user.lastSeen ?: ERROR_INT
        val userWebsite = user.website ?: ""
        val userState = user.state ?: ""
        val userCity = user.city ?: ""

        val entity = UserEntity(
            id = user.id,
            username = user.username.lowercase(), // lowercase to facilitate queries
            email = userEmail,
            access = userAccess,
            streamToken = userStreamToken,
            fullNamePublic = userFullNamePublic,
            fullName = userFullName,
            disabled = userDisabled,
            createDate = userCreateDate,
            lastSeen = userLastSeen,
            website = userWebsite,
            state = userState,
            city = userCity
        )
        dao.updateUser(entity)//user.toUserEntity())
    }

    protected suspend fun getUserNetwork(): User? =
        getCredentials()?.username?.let { username ->
            getSession()?.let { session ->
                try {
                    val user = api.getUser(authKey = session.auth, username = username).toUser()
                    setUser(user)
                    user
                } catch (e: Exception) {
                    errHandler.logError(e)
                    null
                }
            }
        }

    protected suspend fun cacheSongs(songs: List<Song>) {
        dao.insertSongs(songs.map { it.toSongEntity() })
        songs.forEach { song ->
            dao.getDownloadedSong(song.mediaId, song.artist.id, song.album.id)?.let { downloadedSong ->
                dao.addDownloadedSong(
                    song.toDownloadedSongEntity(downloadedSong.songUri, owner = downloadedSong.owner)
                )
            }
        }
    }

    protected suspend fun like(id: String, like: Boolean, type: MainNetwork.Type): Flow<Resource<Any>> = flow {
        emit(Resource.Loading(true))

        // update the database
        val flag  = processFlag(like)
        when(type) {
            MainNetwork.Type.song -> {
                dao.getSongById(id)?.copy(flag = flag)?.let { dbSong ->
                    dao.insertSongs(listOf(dbSong))
                }
                // also update downloaded song
                dao.getDownloadedSongById(id)?.copy(flag = flag == 1)?.let { dbSong ->
                    dao.addDownloadedSong(dbSong)
                }
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
        if (!isOfflineModeEnabled()) {
            likeApiCall(
                id = id,
                like = like,
                type = type).apply {
                error?.let { throw(MusicException(it.toError())) }
                if (success != null) {
                    emit(Resource.Success(data = Any(), networkData = Any()))
                } else {
                    throw Exception("error getting a response from FLAG/LIKE call")
                }
            }
        } else {
            // add to offline data to send request when back online
            OfflineData.likedOffline.push(LikeData(id, like, type))
            emit(Resource.Success(data = Any(), networkData = Any()))
        }

        emit(Resource.Loading(false))
    }.catch { e -> errHandler("likeSong()", e, this) }

    protected suspend fun likeApiCall(id: String, like: Boolean, type: MainNetwork.Type) = api.flag(
        authKey = getSession()!!.auth,
        id = id,
        flag = if (like) { 1 } else { 0 },
        type = type
    )

    protected suspend fun rate(itemId: String, rating: Int, type: MainNetwork.Type): Flow<Resource<Any>> = flow {
        emit(Resource.Loading(true))

        // update the database
        when(type) {
            MainNetwork.Type.song -> {
                dao.getSongById(itemId)?.copy(rating = rating.toFloat())?.let { dbSong ->
                    dao.insertSongs(listOf(dbSong))
                }
                // also update downloaded song if available
                dao.getDownloadedSongById(itemId)?.copy(rating = rating.toFloat())?.let { dbSong ->
                    dao.addDownloadedSong(dbSong)
                }
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

        if (!isOfflineModeEnabled()) {
            rateApiCall(
                itemId = itemId,
                rating = rating,
                type = type
            ).apply {
                error?.let { throw (MusicException(it.toError())) }
                if (success != null) {
                    emit(Resource.Success(data = Any(), networkData = Any()))
                } else {
                    throw Exception("error getting a response from FLAG/LIKE call")
                }
            }
        } else {
            // add to offline data to send request when back online
            OfflineData.ratedOffline.push(RateData(itemId, rating, type))
            emit(Resource.Success(data = Any(), networkData = Any()))
        }
        emit(Resource.Loading(false))
    }.catch { e -> errHandler("likeSong()", e, this) }

    protected suspend fun rateApiCall(itemId: String, rating: Int, type: MainNetwork.Type) = api.rate(
        authKey = getSession()!!.auth,
        itemId = itemId,
        rating = rating,
        type = type
    )

    /**
     * returns false if Network data is not required, true otherwise
     */
    protected suspend fun <T> checkEmitCacheData(
        localData: List<T>,
        fetchRemote: Boolean,
        fc: FlowCollector<Resource<List<T>>>
    ): Boolean {
        val isDbEmpty = localData.isEmpty()
        if (!isDbEmpty) {
            L("checkCachedData ${localData.size}")
            fc.emit(Resource.Success(data = localData))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            fc.emit(Resource.Loading(false))
            return false
        }
        return true
    }
}
