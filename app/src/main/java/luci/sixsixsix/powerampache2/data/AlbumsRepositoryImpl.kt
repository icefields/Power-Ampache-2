package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.MusicRepositoryImpl.Companion.handleError
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toAlbum
import luci.sixsixsix.powerampache2.data.local.entities.toAlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toSessionEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toMusicAttribute
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@Singleton
class AlbumsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val playlistManager: MusicPlaylistManager
): AlbumsRepository {
    private val dao = db.dao

    private suspend fun <T> handleError(
        label:String = "",
        e: Throwable,
        fc: FlowCollector<Resource<T>>,
    ) = MusicRepositoryImpl.handleError(label, e, fc) { message, error ->
        // TODO DEBUG this is just for debugging
        playlistManager.updateErrorMessage(message)

        if (error is MusicException && error.musicError.isSessionExpiredError()) {
            runBlocking {
                dao.clearSession()
            }
        }
    }

    private suspend fun getSession(): Session? = dao.getSession()?.toSession()
    private suspend fun getCredentials(): CredentialsEntity? = dao.getCredentials()


    override suspend fun getAlbums(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        L("getAlbums - repo getSongs offset $offset")

        if (offset == 0) {
            val localAlbums = dao.searchAlbum(query)
            val isDbEmpty = localAlbums.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localAlbums.map { it.toAlbum() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!//authorize2(false)
        val response = api.getAlbums(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val albums = response.albums!!.map { it.toAlbum() } // will throw exception if songs null
        L("albums from web ${albums.size}")

        if (query.isNullOrBlank() && offset == 0 && Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearAlbums()
        }
        dao.insertAlbums(albums.map { it.toAlbumEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchAlbum(query).map { it.toAlbum() }, networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getAlbums()", e, this) }

    override suspend fun getAlbumsFromArtist(
        artistId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        L("repo getAlbumsFromArtist $artistId")

        val localAlbums = dao.getAlbumsFromArtist(artistId).map { it.toAlbum() }
        val isDbEmpty = localAlbums.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localAlbums))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        val auth = getSession()!!//authorize2(false)
        val response = api.getAlbumsFromArtist(auth.auth, artistId = artistId)
        response.error?.let { throw(MusicException(it.toError())) }

        // some albums come from web with no artists id, or with artist id zero, add the id manually
        // so the database can find it (db is single source of truth)
        val albums = response.albums!!
            .map { albumDto ->
                albumDto.toAlbum().copy(
                    artists = ArrayList(
                        albumDto.artists?.let { artists ->
                            artists.map { attribute -> attribute.toMusicAttribute() }
                        } ?: run { listOf() }
                    ).apply {
                        add(MusicAttribute(id = artistId, name = ""))
                    }
                )
            } // will throw exception if songs null

        L("albums from web ${albums.size}")

        dao.insertAlbums(albums.map { it.toAlbumEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        val dbUpdatedAlbums = dao.getAlbumsFromArtist(artistId).map { it.toAlbum() }
        emit(Resource.Success(data = dbUpdatedAlbums, networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getAlbumsFromArtist()", e, this) }

    override suspend fun getRecentAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getAlbumsRecent(auth.auth, username = getCredentials()?.username).albums?.map { it.toAlbum() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getRecentAlbums()", e, this) }

    override suspend fun getNewestAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getAlbumsNewest(auth.auth, username = getCredentials()?.username).albums?.map { it.toAlbum() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getNewestAlbums()", e, this) }

    override suspend fun getHighestAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getAlbumsHighest(auth.auth, username = getCredentials()?.username).albums?.map { it.toAlbum() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getHighestAlbums()", e, this) }

    override suspend fun getFrequentAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getAlbumsFrequent(auth.auth, username = getCredentials()?.username).albums?.map { it.toAlbum() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getFrequentAlbums()", e, this) }

    override suspend fun getFlaggedAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getAlbumsFlagged(auth.auth).albums?.map { it.toAlbum() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getFlaggedAlbums()", e, this) }

    override suspend fun getRandomAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getAlbumsRandom(auth.auth, username = getCredentials()?.username).albums?.map { it.toAlbum() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getRandomAlbums()", e, this) }
}
