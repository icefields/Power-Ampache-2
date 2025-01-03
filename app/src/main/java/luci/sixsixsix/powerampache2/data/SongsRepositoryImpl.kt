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

import androidx.lifecycle.map
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Constants.NETWORK_REQUEST_LIMIT_SONGS_SEARCH
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.WeakContext
import luci.sixsixsix.powerampache2.common.hasMore
import luci.sixsixsix.powerampache2.common.pop
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.HistoryEntity
import luci.sixsixsix.powerampache2.data.local.entities.toHistoryEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.local.entities.toSongEntity
import luci.sixsixsix.powerampache2.data.local.models.SongUrl
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.OfflineData.likedOffline
import luci.sixsixsix.powerampache2.data.remote.OfflineData.ratedOffline
import luci.sixsixsix.powerampache2.data.remote.OfflineData.songsToScrobble
import luci.sixsixsix.powerampache2.data.remote.ScrobbleData
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.data.remote.worker.SongDownloadWorker.Companion.startSongDownloadWorker
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.ScrobbleException
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import okio.IOException
import retrofit2.HttpException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@OptIn(DelicateCoroutinesApi::class)
@Singleton
class SongsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    db: MusicDatabase,
    private val errorHandler: ErrorHandler,
    private val storageManager: StorageManager,
    private val weakContext: WeakContext
): BaseAmpacheRepository(api, db, errorHandler), SongsRepository {

    override val offlineSongsLiveData = dao.getDownloadedSongsLiveData().map { entities ->
        entities.map {
            it.toSong()
        }
    }

    init {
        GlobalScope.launch {
            dao.offlineModeEnabled().distinctUntilChanged().collect {
                if (it != null && it == false) {
                    try {
                        backOnlineActions()
                    } catch (e: Exception) {
                        errorHandler.logError(e)
                    }
                }
            }
        }
    }

    private suspend fun backOnlineActions() {
        scrobbleEverything()

        while(likedOffline.hasMore()) {
            likedOffline.pop()?.let { likedItem ->
                try {
                    likeApiCall(likedItem.id, likedItem.like, likedItem.type)
                } catch (e: Exception) {
                    errorHandler.logError(e)
                }
            }
        }

        while(ratedOffline.hasMore()) {
            ratedOffline.pop()?.let { ratedItem ->
                try {
                    rateApiCall(ratedItem.id, ratedItem.rating, ratedItem.type)
                } catch (e: Exception) {
                    errorHandler.logError(e)
                }
            }
        }
    }

    override suspend fun getSongs(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ) = if (!isOfflineModeEnabled()) {
        val minDbSongs = 200
        // get songs from db, if the result is less than minDbSongs
        // also get songs from network
        val isSearch = query.isNotBlank()
        val songsDb = //if (query.isNullOrBlank())
            dao.searchSong(query)
        //else listOf()
        // always check network in case of search, if online
        if (isSearch || songsDb.size < minDbSongs) { // will always be less if it's a search
            getSongsNetwork(
                fetchRemote = fetchRemote,
                query = query,
                offset = offset,
                initialList = songsDb.map { it.toSong() }.toMutableList()
            )
        } else {
            flow {
                emit(Resource.Success(data = songsDb.map { it.toSong()}))
            }
        }
    } else {
        searchOfflineSongs(query)
    }

    override suspend fun getSongFromId(songId: String): Song? =
        dao.getSongById(songId)?.run { toSong() }
            ?: try { api.getSong(authToken(), songId).toSong() } catch (e: Exception) { null }

    private suspend fun getSongsNetwork(
        fetchRemote: Boolean,
        query: String,
        offset: Int,
        initialList: MutableList<Song> = mutableListOf()
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Success(data = initialList.toList()))

        val auth = authToken()
        val credentials = getCurrentCredentials()
        val songs = if (query.isNullOrBlank()) {
            // not a search
            try {
                api.getSongsStats(
                    filter = MainNetwork.StatFilter.random,
                    authKey = auth,
                    username = getCredentials()?.username
                ).songs!!.map { it.toSong() }
            } catch (e: Exception) {
                listOf()
            }
        } else {
            // if this is a search
            val response = api.getSongs(auth, filter = query, offset = offset, limit = NETWORK_REQUEST_LIMIT_SONGS_SEARCH)
            response.error?.let { throw(MusicException(it.toError())) }
            response.songs!!.map { it.toSong() } // will throw exception if songs null
        }

        // db
        if (query.isNullOrBlank() && offset == 0 && Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search, or we were fetching more items (offset > 0) do not clear cache
            dao.clearSongs()
        }
        dao.insertSongs(songs.map { it.toSongEntity(username = credentials.username, serverUrl = credentials.serverUrl) })

        // stick to the single source of truth pattern despite performance deterioration
        val songsDb = dao.searchSong(query).map { it.toSong() }
        L( "getSongs songs from db after web ${songsDb.size}")

        val returnSongList = if (query.isNullOrBlank()) {
            // if not a search append the songsDb to the network result
            AmpacheModel.appendToList(songsDb.toMutableList(), initialList)
            initialList
        } else {
            // if it's a search return what the db found
            songsDb
        }

        emit(Resource.Success(data = returnSongList, networkData = songs))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongs()", e, this) }

    private suspend fun getDbSongs(albumId: String, isOfflineModeEnabled: Boolean ): List<Song> =
        if (isOfflineModeEnabled) {
            dao.getOfflineSongsFromAlbum(albumId).map { it.toSong() }
        } else {
            dao.getSongFromAlbum(albumId).map { it.toSong() }
        }

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
        val isOfflineMode = isOfflineModeEnabled()
        val localSongs = getDbSongs(albumId, isOfflineMode)
        //val localSongs = dao.getSongFromAlbum(albumId).map { it.toSong() }
        if (!checkEmitCacheData(localSongs, fetchRemote, this) || isOfflineMode) {
            emit(Resource.Loading(false))
            return@flow
        }

        val response = api.getSongsFromAlbum(authToken(), albumId = albumId)
        response.error?.let { throw(MusicException(it.toError())) }
        val songs = response.songs!!.map { songDto -> songDto.toSong() } // will throw exception if songs null
        L("getSongsFromAlbum songs from web", songs.size)

        // TODO BREAKING_RULE single source of truth. (see function doc)
        emit(Resource.Success(data = songs, networkData = songs))
        emit(Resource.Loading(false))

        // cache songs after emitting success because the result of this is not used right now
        cacheSongs(songs)
    }.catch { e -> errorHandler("getSongsFromAlbum()", e, this) }

    private suspend fun getSongsStats(statFilter: MainNetwork.StatFilter, fetchRemote: Boolean = true) =
        if (!isOfflineModeEnabled()) flow {
            emit(Resource.Loading(true))

            if (statFilter == MainNetwork.StatFilter.recent) {
                emit(Resource.Success(data = dao.getSongHistory().map { it.toSong() }, networkData = null))
            } else if (statFilter == MainNetwork.StatFilter.frequent) {
                emit(Resource.Success(data = dao.getMostPlayedSongs().map { it.toSong() }, networkData = null))
            }

            if (!fetchRemote) {
                emit(Resource.Loading(false))
                return@flow
            }

            val cred = getCurrentCredentials()
            getSongsStatCall(authToken(), statFilter)?.map { it.toSong() }?.let { songs ->
                cacheSongs(songs)
                when (statFilter) {
                    MainNetwork.StatFilter.recent -> {
                        // cache new history
                        // lastPlayed info not available in the song object, to keep the order of
                        // the songs consistent, use current time and subtract the index of the song
                        // in the array returned by the server
                        val lastPlayed = System.currentTimeMillis()
                        dao.addSongsToHistory(songs.mapIndexed { index, so -> so.toHistoryEntity(
                            username = cred.username,
                            serverUrl = cred.serverUrl,
                            lastPlayed = lastPlayed - index) })

                        emit(Resource.Success(data = dao.getSongHistory().map { it.toSong() }.toMutableList(), networkData = songs))
                    }
                    MainNetwork.StatFilter.frequent -> {
                        // songs are saved already, just fetch them again and emit
                        emit(Resource.Success(
                            data = dao.getMostPlayedSongs().ifEmpty {
                                dao.getMostPlayedSongsLocal() }.map { it.toSong() },
                            networkData = songs))
                    }
                    else -> {
                        emit(Resource.Success(data = songs, networkData = songs))
                    }
                }
            }?:run {
                throw Exception("error connecting or getting data in getSongsStats")
            }
            emit(Resource.Loading(false))
        }.catch { e -> errorHandler("getSongsStats()", e, this) }
    else
        getSongsStatsDb(statFilter)

    private suspend fun getSongsStatsDb(statFilter: MainNetwork.StatFilter) = flow {
        emit(Resource.Loading(true))
        val songs = when(statFilter) {
            MainNetwork.StatFilter.random -> dao.getRandomOfflineSongs()
            MainNetwork.StatFilter.recent -> dao.getOfflineSongHistory()
            MainNetwork.StatFilter.newest -> dao.getRecentlyReleasedOfflineSongs()
            MainNetwork.StatFilter.frequent -> dao.getMostPlayedOfflineSongs()
            MainNetwork.StatFilter.flagged -> dao.getLikedOfflineSongs()
            MainNetwork.StatFilter.forgotten -> listOf()
            MainNetwork.StatFilter.highest -> dao.getHighestRatedOfflineSongs()
        }.map { it.toSong() }
        emit(Resource.Success(data = songs, networkData = songs))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongsStats()", e, this) }

    private suspend fun getSongsStatCall(auth: String, statFilter: MainNetwork.StatFilter) =
        api.getSongsStats(auth,
            username = getCredentials()?.username,
            filter = statFilter
        ).songs

    override suspend fun getRecentSongs(fetchRemote: Boolean) =
        getSongsStats(MainNetwork.StatFilter.recent, fetchRemote)

    override suspend fun getNewestSongs() =
        getSongsStats(MainNetwork.StatFilter.newest)

    override suspend fun getHighestSongs() =
        getSongsStats(MainNetwork.StatFilter.highest)

    override suspend fun getFrequentSongs() =
        getSongsStats(MainNetwork.StatFilter.frequent)

    override suspend fun getFlaggedSongs() =
        getSongsStats(MainNetwork.StatFilter.flagged)

    override suspend fun getRandomSongs() =
        getSongsStats(MainNetwork.StatFilter.random)

    override suspend fun rateSong(albumId: String, rate: Int): Flow<Resource<Any>> =
        rate(albumId, rate, MainNetwork.Type.song)

    override suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>> =
        like(id, like, MainNetwork.Type.song)

    override suspend fun getSongsByGenre(
        genre: Genre,
        fetchRemote: Boolean,
        offset: Int
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
            val localSongs = dao.searchSongByGenre(genre.name)
            val isDbEmpty = localSongs.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localSongs.map { it.toSong() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!
        val credentials = getCurrentCredentials()
        val response = api.getSongsByGenre(auth.auth, genreId = genre.id, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val songs = response.songs!!.map { it.toSong() } //will throw exception if artist null

        if (Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearSongs()
        }
        dao.insertSongs(songs.map { it.toSongEntity(username = credentials.username, serverUrl = credentials.serverUrl) })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchSongByGenre(genre.name).map { it.toSong() }, networkData = songs))

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    suspend fun searchOfflineSongs(query: String) = flow {
        emit(Resource.Loading(true))
        val songs = dao.searchOfflineSongs(query).map { it.toSong() }
        emit(Resource.Success(songs))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongsStats()", e, this) }


    override suspend fun getSongsForQuickPlay() = flow {
        emit(Resource.Loading(true))
        val resultSet = HashSet<Song>()
        // add downloaded songs
        try { resultSet.addAll(dao.getMostPlayedOfflineSongs().map { it.toSong() }) } catch (e: Exception) { }
        try { resultSet.addAll(dao.getLikedOfflineSongs().map { it.toSong() }) } catch (e: Exception) { }
        try { resultSet.addAll(dao.getHighestRatedOfflineSongs().map { it.toSong() }) } catch (e: Exception) { }
        if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
            try { resultSet.addAll(dao.getOfflineSongs().map { it.toSong() }) } catch (e: Exception) { }
        }
        // add cached songs? Too many can cause a crash when saving state
        // resultSet.addAll(dao.searchSong("").map { it.toSong() })
        // if not big enough start fetching from web
        if (!isOfflineModeEnabled()) {
            // try add cached songs first
            if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
                try { resultSet.addAll(dao.getMostPlayedSongs().map { it.toSong() }) } catch (e: Exception) { }
            }
            if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
                try { resultSet.addAll(dao.getMostPlayedSongsLocal().map { it.toSong() }) } catch (e: Exception) { }
            }
            if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
                try { resultSet.addAll(dao.searchSong("").map { it.toSong() }) } catch (e: Exception) { }
            }
            try {
                if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
                    // if not enough downloaded songs fetch most played songs
                    val auth = authToken()
                    getSongsStatCall(auth, MainNetwork.StatFilter.frequent)?.map { it.toSong() }?.let { freqSongs ->
                        cacheSongs(freqSongs)
                        resultSet.addAll(freqSongs)
                    }
                    if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
                        // if still not enough songs fetch random songs
                        getSongsStatCall(auth, MainNetwork.StatFilter.flagged)?.map { it.toSong() }?.let { flagSongs ->
                            cacheSongs(flagSongs)
                            resultSet.addAll(flagSongs)
                        }
                    }
                    if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
                        // if still not enough songs fetch random songs
                        getSongsStatCall(auth, MainNetwork.StatFilter.highest)?.map { it.toSong() }?.let { highestSongs ->
                            cacheSongs(highestSongs)
                            resultSet.addAll(highestSongs)
                        }
                    }
                    if (resultSet.size < Constants.QUICK_PLAY_MIN_SONGS) {
                        // if still not enough songs fetch random songs
                        getSongsStatCall(auth, MainNetwork.StatFilter.random)?.map { it.toSong() }?.let { randSongs ->
                            cacheSongs(randSongs)
                            resultSet.addAll(randSongs)
                        }
                    }
                }
            } catch (e: HttpException) {
                // do not fail in case of http exception, just return offline songs
                // TODO emit error anyway at the end?
            } catch (e: IOException) {
                // do not fail in case of IOException, just return offline songs
            }
        }
        val shuffledSongList = resultSet.toList().shuffled()
        emit(Resource.Success(data = shuffledSongList, networkData = shuffledSongList))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongsForQuickPlay()", e, this) }

    override suspend fun getSongUri(song: Song) =
        dao.getDownloadedSong(song.mediaId, song.artist.id, song.album.id)?.songUri
            ?: buildSongUrl(song)

    /**
     * Build Url for Ampache stream action
     * https://www.servername.org/server/json.server.php?action=stream&
     * auth=878944fc45e121977229fe8027e52187
     * &type=song
     * &id=8895
     */
    private suspend fun buildSongUrl(song: Song) = dao.getSongUrlData()?.run {
        getUrl(song.mediaId)
    } ?: getSession()?.auth ?.let { auth ->
        SongUrl(
            authToken = auth,
            serverUrl = getCredentials()!!.serverUrl
        ).getUrl(song.mediaId)
    } ?: song.songUrl

    override suspend fun isSongAvailableOffline(song: Song): Boolean =
        isSongAvailableOffline(song.mediaId, song.artist.id, song.album.id)

    private suspend fun isSongAvailableOffline(songId: String, artistId: String, albumId: String): Boolean =
        dao.getDownloadedSong(songId, artistId, albumId) != null

    // TODO FIX maybe should not be a flow since it only launches the worker,
    //  I don't need any result from this function
    override suspend fun downloadSong(song: Song): Flow<Resource<Any>> = channelFlow {
        send(Resource.Loading(true))
        startDownloadingSong(song)?.let {
            // retrieve the requestID
        } ?: run {
            // duplicate download
        }
        send(Resource.Success(data = Any(), networkData = Any()))
        send(Resource.Loading(false))
    }.catch { e -> errorHandler("downloadSong()", e, this) }

    /**
     * @return a UUID if download started successfully or null if it's a duplicate
     * @throws and exception in any other case
     */
    @Throws(Exception::class)
    private suspend fun startDownloadingSong(song: Song): UUID? {
        val isSongDownloadedAlready =
            dao.getDownloadedSong(song.mediaId, song.artist.id, song.album.id) != null
        if (isSongDownloadedAlready) {
            return null
        }
        return weakContext.get()?.let { context ->
            val auth = getSession()!!.auth
            val username = getUsername()!!

            val requestId = startSongDownloadWorker(
                context = context,
                authToken = auth,
                username = username,
                song = song
            )
            L(requestId)
            requestId
        } ?: run {
            throw NullPointerException("startDownloadingSong(), context was null")
        }
    }

    override suspend fun downloadSongs(songs: List<Song>) {
        songs.forEach { song ->
            try {
                startDownloadingSong(song)?.let {
                    // retrieve the requestID
                } ?: run {
                    // duplicate download
                }
            } catch (e: Exception) {
                errorHandler.logError(e)
            }
        }
    }

    private suspend fun emitDownloadSuccess(fc: ProducerScope<Resource<Any>>){
        fc.send(Resource.Success(data = Any(), networkData = Any()))
        fc.send(Resource.Loading(false))
    }

    private suspend fun emitDownloadProgress(fc: ProducerScope<Resource<Any>>, value: Int){
        fc.send(Resource.Loading((value != 100)))
    }

    override suspend fun deleteDownloadedSong(song: Song) = flow {
        emit(Resource.Loading(true))
        dao.deleteDownloadedSong(songId = song.mediaId)
        storageManager.deleteSong(song)
        emit(Resource.Success(data = Any(), networkData = Any()))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("downloadSong()", e, this) }

    override suspend fun getSongShareLink(song: Song) = flow {
        emit(Resource.Loading(true))
        val response = api.createShare(
            getSession()!!.auth,
            id = song.mediaId,
            type = MainNetwork.Type.song
        )
        response.error?.let { throw(MusicException(it.toError())) }
        response.publicUrl!!.apply {
            emit(Resource.Success(data = this, networkData = this))
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongShareLink()", e, this) }

    override suspend fun getDownloadedSongById(songId: String): Song? =
        dao.getSongById(songId)?.toSong()


    /**
     * Note: Only scrobble offline songs
     */
    override suspend fun scrobble(song: Song) = flow {
        emit(Resource.Loading(true))
        if (isSongAvailableOffline(song)) {
            songsToScrobble.add(ScrobbleData(song))
            L("scrobble song available offline", songsToScrobble.size)
            scrobbleEverything()
            emit(Resource.Success(data = Any(), networkData = Any()))
        } else {
            if (Constants.config.isRecordPlayApiEnabled) {
                scrobbleApiCall(authToken(), ScrobbleData(song))
            }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("scrobble()", e, this) }

    private suspend fun scrobbleEverything() {
        if (!isOfflineModeEnabled()) {
            while(songsToScrobble.hasMore()) {
                // delay(21000)
                songsToScrobble.pop()?.let { sts ->
                    if (scrobbleApiCall(authToken(), sts)) {
                        L("scrobble success!", songsToScrobble.size, sts.song.name)
                    } else {
                        L("scrobble failed", songsToScrobble.size, sts.song.name)
                    }
                }
            }
        }
    }

    override suspend fun addToHistory(song: Song) = getCurrentCredentials().run {
        // Nextcloud does not return a play count, pick that up from database if available
        val playCount = max((dao.getSongFromHistory(song.mediaId)?.playCount ?: ERROR_INT), song.playCount) + 1
        dao.addSongToHistory(HistoryEntity.newEntry(mediaId = song.mediaId,
            playCount = playCount,
            username = this.username,
            serverUrl = this.serverUrl
        ))
        true
    }

    @Throws(Exception::class)
    private suspend fun scrobbleApiCall(auth: String, scrobbleData: ScrobbleData) =
        api.recordPlay(auth, scrobbleData.song.mediaId, scrobbleData.unixTimestamp).run {
            error?.let { throw (ScrobbleException(it.toError())) }
            (success != null)
        }
//        api.scrobble(
//            authKey = auth,
//            song = song.name,
//            artist = song.artist.name,
//            album = song.album.name
//        ).run {
//            error?.let { throw (ScrobbleException(it.toError())) }
//            (success != null)
//        }
}
