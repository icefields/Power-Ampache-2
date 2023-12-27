package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.local.entities.toSongEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@Singleton
class SongsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val playlistManager: MusicPlaylistManager
): SongsRepository {
    private val dao = db.dao

    private suspend fun <T> handleError(
        label: String = "",
        e: Throwable,
        fc: FlowCollector<Resource<T>>,
    ) = MusicRepositoryImpl.handleError(label, e, fc) {
        // TODO DEBUG this is just for debugging
        playlistManager.updateErrorMessage(it)
    }

    private suspend fun getSession(): Session? = dao.getSession()?.toSession()
    private suspend fun getCredentials(): CredentialsEntity? = dao.getCredentials()

    /**
     * TODO BREAKING_RULE single source of truth
     *  the code here is very dirty, CLEAN SOON!!
     */
    override suspend fun getSongs(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Song>>> = flow<Resource<List<Song>>> {
        emit(Resource.Loading(true))
        L( "getSongs - repo getSongs")

        // TODO home page is not loading cached data at the beginning
        //      to avoid invalid or outdated data
        // db
        // the offset is meant to be use to fetch more data from the web,
        // return cache only if the offset is zero
//        if (offset == 0) {
//            val localSongs = dao.searchSong(query)
//            L("getSongs - songs from cache ${localSongs.size}")
//            val isDbEmpty = localSongs.isEmpty() && query.isEmpty()
//            if (!isDbEmpty) {
//                emit(Resource.Success(data = localSongs.map { it.toSong() }))
//            }
//            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
//            if (shouldLoadCacheOnly) {
//                emit(Resource.Loading(false))
//                return@flow
//            }
//        }

        // network TODO WHAT IS THIS!!?? FIX !!!
        val auth = getSession()!!//authorize2(false)
        val hashSet = LinkedHashSet<Song>()
        val songs = if (query.isNullOrBlank()) {
            try {
                api.getSongsRandom(
                    authKey = auth.auth,
                    username = getCredentials()?.username
                ).songs?.let { dto ->
                    val random = dto.map { it.toSong() }
                    hashSet.addAll(random)
                    if (random.isNotEmpty()) {
                        emit(Resource.Success(data = hashSet.toList()))
                    }
                }
            } catch (e: Exception) {
            }

            try {
                api.getSongsNewest(
                    authKey = auth.auth,
                    username = getCredentials()?.username
                ).songs?.let { dto ->
                    val newest = dto.map { it.toSong() }
                    hashSet.addAll(newest)
                    if (newest.isNotEmpty()) {
                        emit(Resource.Success(data = hashSet.toList()))
                    }
                }
            } catch (e: Exception) {
            }

            try {
                api.getSongsRecent(
                    authKey = auth.auth,
                    username = getCredentials()?.username
                ).songs?.let { dto ->
                    val recent = dto.map { it.toSong() }
                    hashSet.addAll(recent)
                    if (recent.isNotEmpty()) {
                        emit(Resource.Success(data = hashSet.toList()))
                    }
                }
            } catch (e: Exception) {
            }

            try {
                api.getSongsHighest(
                    authKey = auth.auth,
                    username = getCredentials()?.username
                ).songs?.let { dto ->
                    val highest = dto.map { it.toSong() }
                    hashSet.addAll(highest)
                    if (highest.isNotEmpty()) {
                        emit(Resource.Success(data = hashSet.toList()))
                    }
                }
            } catch (e: Exception) {
            }

            try {
                api.getSongsFrequent(
                    authKey = auth.auth,
                    username = getCredentials()?.username
                ).songs?.let { dto ->
                    val frequent = dto.map { it.toSong() }
                    hashSet.addAll(frequent)
                    if (frequent.isNotEmpty()) {
                        emit(Resource.Success(data = hashSet.toList()))
                    }
                }
            } catch (e: Exception) {
            }

            try {
                api.getSongsFlagged(
                    authKey = auth.auth,
                    username = getCredentials()?.username
                ).songs?.let { flaggedDto ->
                    val flagged = flaggedDto.map { it.toSong() }
                    hashSet.addAll(flagged)
                    if (flagged.isNotEmpty()) {
                        emit(Resource.Success(data = hashSet.toList()))
                    }
                }
            } catch (e: Exception) {
            }

            hashSet.toList()
        } else {
            val response = api.getSongs(auth.auth, filter = query, offset = offset)
            response.error?.let { throw(MusicException(it.toError())) }
            /*val songs = */response.songs!!.map { it.toSong() } // will throw exception if songs null
            //emit(Resource.Success(songs)) // will throw exception if songs null
        }

        L("getsongs - songs from web ${songs.size}")

        // db
        if (query.isNullOrBlank() && offset == 0 && Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search, or we were fetching more items (offset > 0) do not clear cache
            dao.clearSongs()
        }
        dao.insertSongs(songs.map { it.toSongEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        val songsDb = dao.searchSong(query).map { it.toSong() }
        L( "getSongs songs from db after web ${songsDb.size}")
        val hashset = LinkedHashSet<Song>()
        hashset.addAll(songs)
        hashset.addAll(songsDb) // add all the cached history at the end of the list
        emit(Resource.Success(data = hashset.toList(), networkData = songs))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getSongs()", e, this) }

    /**
     * TODO BREAKING_RULE: inconsistent data in the response, must use network response.
     *   INVESTIGATE !
     *
     * USE Network response for this, database not reliable for unknown reason
     * use the cache to just preload some data
     */
    override suspend fun getSongsFromAlbum(
        albumId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val localSongs = dao.getSongFromAlbum(albumId).map { it.toSong() }
        if (!checkEmitCacheData(localSongs, fetchRemote, this)) {
            return@flow
        }

        val auth = getSession()!!//authorize2(false)
        val response = api.getSongsFromAlbum(auth.auth, albumId = albumId)
        response.error?.let { throw(MusicException(it.toError())) }
        val songs = response.songs!!.map { songDto -> songDto.toSong() } // will throw exception if songs null
        L("getSongsFromAlbum songs from web ${songs.size}")

        // TODO BREAKING_RULE single source of truth. (see function doc)
        emit(Resource.Success(data = songs, networkData = songs))
        emit(Resource.Loading(false))

        // cache songs after emitting success because the result of this is not used right now
        dao.insertSongs(songs.map { it.toSongEntity() })
    }.catch { e -> handleError("getSongsFromAlbum()", e, this) }

    /**
     * TODO BREAKING_RULE: Implement cache for playlist songs
     *  There is currently no way to get songs given the playlistId
     *  Songs are cached regardless for quick access from SongsScreen and Albums
     * This method only fetches from the network, breaking one of the rules defined in the
     * documentation of this class.
     */
    override suspend fun getSongsFromPlaylist(
        playlistId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        L("repo getSongsFromPlaylist $playlistId")

        val auth = getSession()!!
        val response = api.getSongsFromPlaylist(auth.auth, albumId = playlistId)
        response.error?.let { throw(MusicException(it.toError())) }
        val songs = response.songs!!.map { songDto -> songDto.toSong() } // will throw exception if songs null

        emit(Resource.Success(data = songs, networkData = songs))
        emit(Resource.Loading(false))

        // Songs are cached regardless for quick access from SongsScreen and Albums
        // cache songs after emitting success
        dao.insertSongs(songs.map { it.toSongEntity() })
    }.catch { e -> handleError("getSongsFromPlaylist()", e, this) }

    override suspend fun getRecentSongs(): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getSongsRecent(auth.auth, username = getCredentials()?.username).songs?.map { it.toSong() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getRecentSongs()", e, this) }

    override suspend fun getNewestSongs(): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getSongsNewest(auth.auth, username = getCredentials()?.username).songs?.map { it.toSong() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getNewestSongs()", e, this) }

    override suspend fun getHighestSongs(): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getSongsHighest(auth.auth, username = getCredentials()?.username).songs?.map { it.toSong() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getHighestSongs()", e, this) }

    override suspend fun getFrequentSongs(): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getSongsFrequent(auth.auth, username = getCredentials()?.username).songs?.map { it.toSong() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getFrequentSongs()", e, this) }

    override suspend fun getFlaggedSongs(): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getSongsFlagged(auth.auth).songs?.map { it.toSong() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getFlaggedSongs()", e, this) }

    override suspend fun getRandomSongs(): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getSongsRandom(auth.auth, username = getCredentials()?.username).songs?.map { it.toSong() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            throw Exception("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getRandomSongs()", e, this) }


    /**
     * returns false if Network data is not required, true otherwise
     */
    private suspend fun <T> checkEmitCacheData
                (localData: List<T>,
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

// NOTES FOR ERROR CATCHING TODO REMOVE_COMMENTS
//        .catch { e ->
//        when(e) {
//            is IOException ->
//                emit(Resource.Error(message = "getSongs cannot load data IOException $e", exception = e))
//            is HttpException ->
//                emit(Resource.Error(message = "getSongs cannot load data HttpException $e", exception = e))
//            is MusicException ->
//                emit(Resource.Error(message = e.musicError.toString(), exception = e))
//            else ->
//                emit(Resource.Error(message = "getSongs generic exception $e", exception = e))
//        }
//   }