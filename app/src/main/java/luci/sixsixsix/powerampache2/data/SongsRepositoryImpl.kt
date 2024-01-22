package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.mrlog.L
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
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject
import javax.inject.Singleton

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
    private val errorHandler: ErrorHandler
): SongsRepository {
    private val dao = db.dao

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
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        L( "getSongs - repo getSongs")

        // TODO songs view is not loading cached data at the beginning
        //      to avoid weird behaviour due to the mix of random songs from api
        //      and the overall .shuffled() called when emitting at the end
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
            // not a search
            try {
                api.getSongsStats(
                    filter = MainNetwork.StatFilter.random,
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
            hashSet.toList()
        } else {
            // if this is a search
            val response = api.getSongs(auth.auth, filter = query, offset = offset)
            response.error?.let { throw(MusicException(it.toError())) }
            response.songs!!.map { it.toSong() } // will throw exception if songs null
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

        val returnSongList =  if (query.isNullOrBlank()) {
            // if not a search append the songsDb to the network result
            ArrayList(songsDb).removeAll(songs.toSet())
            ArrayList(songs).apply {
                addAll(songsDb.shuffled())
            }
        } else {
            // if it's a search return what the db found
            songsDb
        }

        emit(Resource.Success(data = returnSongList, networkData = songs))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongs()", e, this) }

    private suspend fun cacheSongs(songs: List<Song>) =
        dao.insertSongs(songs.map { it.toSongEntity() })


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
        L("getSongsFromAlbum songs from web", songs.size)

        // TODO BREAKING_RULE single source of truth. (see function doc)
        emit(Resource.Success(data = songs, networkData = songs))
        emit(Resource.Loading(false))

        // cache songs after emitting success because the result of this is not used right now
        cacheSongs(songs)
    }.catch { e -> errorHandler("getSongsFromAlbum()", e, this) }

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
        L("repo getSongsFromPlaylist playlistId: $playlistId")

        val auth = getSession()!!
        val response = api.getSongsFromPlaylist(auth.auth, albumId = playlistId)
        response.error?.let { throw(MusicException(it.toError())) }
        val songs = response.songs!!.map { songDto -> songDto.toSong() } // will throw exception if songs null

        emit(Resource.Success(data = songs, networkData = songs))
        emit(Resource.Loading(false))

        // cache songs after emitting success
        // Songs are cached regardless for quick access from SongsScreen and Albums
        cacheSongs(songs)
    }.catch { e -> errorHandler("getSongsFromPlaylist()", e, this) }

    private suspend fun getSongsStats(statFilter: MainNetwork.StatFilter): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getSongsStats(
            auth.auth,
            username = getCredentials()?.username,
            filter = statFilter
        ).songs?.map { it.toSong() }?.let { songs ->
            emit(Resource.Success(data = songs, networkData = songs))

            // cache songs after emitting success because the result of this is not used right now
            cacheSongs(songs)
        }?:run {
            throw Exception("error connecting or getting data in getSongsStats")
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongsStats()", e, this) }

    override suspend fun getRecentSongs() = getSongsStats(MainNetwork.StatFilter.recent)
    override suspend fun getNewestSongs() = getSongsStats(MainNetwork.StatFilter.newest)
    override suspend fun getHighestSongs() = getSongsStats(MainNetwork.StatFilter.highest)
    override suspend fun getFrequentSongs() = getSongsStats(MainNetwork.StatFilter.frequent)
    override suspend fun getFlaggedSongs() = getSongsStats(MainNetwork.StatFilter.flagged)
    override suspend fun getRandomSongs() = getSongsStats(MainNetwork.StatFilter.random)


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