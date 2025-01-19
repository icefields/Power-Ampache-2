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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.NETWORK_REQUEST_LIMIT_HOME
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.toAlbum
import luci.sixsixsix.powerampache2.data.local.entities.toAlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AlbumsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val errorHandler: ErrorHandler
): BaseAmpacheRepository(api, db, errorHandler), AlbumsRepository {
    override suspend fun getAlbums(
        fetchRemote: Boolean,
        query: String,
        offset: Int,
        limit: Int
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        L("getAlbums - repo getSongs offset $offset")
        val cred = getCurrentCredentials()

        if (isOfflineModeEnabled()) {
            // TRY using cached data instead of downloaded song info if available
            val albumsList = mutableListOf<Album>()
            val dbAlbumsHash = HashMap<String, AlbumEntity>().apply {
                dao.getOfflineAlbums().forEach { ae ->
                    put(ae.id, ae)
                }
            }

            dao.generateOfflineAlbums(cred.username).forEach { dae ->
                albumsList.add(
                    if (dbAlbumsHash.containsKey(dae.id)) {
                        (dbAlbumsHash[dae.id] ?: dae)
                    } else { dae }.toAlbum()
                )
            }

            emit(Resource.Success(data = albumsList.toList()))
            emit(Resource.Loading(false))
            return@flow
        }

        val localAlbums = mutableListOf<Album>()
        if (offset == 0) {
            localAlbums.addAll(dao.searchAlbum(query).map { it.toAlbum() })
            val isDbEmpty = localAlbums.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localAlbums.toList()))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val response = api.getAlbums(authToken(), filter = query, offset = offset, limit = limit)
        response.error?.let { throw(MusicException(it.toError())) }
        val albums = response.albums!!.map { it.toAlbum() } // will throw exception if songs null
        dao.insertAlbums(albums.map { it.toAlbumEntity(username = cred.username, serverUrl = cred.serverUrl) })
        // stick to the single source of truth pattern despite performance deterioration
        // append to the initial list to avoid ui flickering
        val updatedDbAlbums = dao.searchAlbum(query).map { it.toAlbum() }.toMutableList()
        AmpacheModel.appendToList(updatedDbAlbums, localAlbums)
        emit(Resource.Success(data = localAlbums, networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbums()", e, this) }

    override suspend fun getAlbumsFromArtist(
        artistId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))

        // get all local album and filter the ones that have offline songs if offline mode enabled
        if (isOfflineModeEnabled()) {
            val offlineAlbums = dao.getOfflineAlbumsByArtist(artistId).map { it.toAlbum() }
            emit(Resource.Success(data = offlineAlbums, networkData = offlineAlbums))
            emit(Resource.Loading(false))
            return@flow
        }

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

        val cred = getCurrentCredentials()
        val response = api.getAlbumsFromArtist(authToken(), artistId = artistId)
        response.error?.let { throw(MusicException(it.toError())) }

        // some albums come from web with no artists id, add the id manually so the database can find it later
        val albums = response.albums!!.map { albumDto -> albumDto.toAlbum() }.toMutableList() // will throw exception if albums null
        dao.deleteAlbumsFromArtist(artistId)

        albums.forEachIndexed { index, alb ->
            try {
                // check if the album contains the current artist Id. If not, add the artist to the list of featured artists
                if (alb.artist.id != artistId && !alb.artists.map { art -> art.id }.contains(artistId)) {
                    val artName = dao.getArtist(artistId)?.name ?: ""
                    albums[index] = alb.copy(artists = alb.artists.toMutableList().apply {
                        add(MusicAttribute(id = artistId, name = artName))
                    })
                }
            } catch (e: Exception) {}

            // delete the album, it will be reinserted right after
            dao.deleteAlbum(alb.id)
        }
        dao.insertAlbums(albums.map { it.toAlbumEntity(username = cred.username, serverUrl = cred.serverUrl) })
        // stick to the single source of truth pattern despite performance deterioration
        val dbUpdatedAlbums = dao.getAlbumsFromArtist(artistId).map { it.toAlbum() }
        emit(Resource.Success(data = dbUpdatedAlbums, networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbumsFromArtist()", e, this) }

    override suspend fun getAlbum(
        albumId: String,
        fetchRemote: Boolean,
    ): Flow<Resource<Album>> = flow {
        emit(Resource.Loading(true))

        if (isOfflineModeEnabled()) {
            dao.generateOfflineAlbum(albumId)?.let { albumEntity ->
                emit(Resource.Success(data = albumEntity.toAlbum(), networkData = albumEntity.toAlbum()))
                emit(Resource.Loading(false))
                return@flow
            } ?: throw Exception("OFFLINE ALBUM does not exist")
        }

        dao.getAlbum(albumId)?.let { albumEntity ->
            emit(Resource.Success(data = albumEntity.toAlbum() ))
            if(!fetchRemote) {  // load cache only?
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val cred = getCurrentCredentials()
        val response = api.getAlbumInfo(authKey = authToken(), albumId = albumId)
        val album = response.toAlbum()  //will throw exception if artist null

        dao.insertAlbums(listOf(album.toAlbumEntity(username = cred.username, serverUrl = cred.serverUrl)))
        // stick to the single source of truth pattern despite performance deterioration
        dao.getAlbum(albumId)?.let { albumEntity ->
            emit(Resource.Success(data = albumEntity.toAlbum(), networkData = album ))
        }

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbum()", e, this) }

    override suspend fun getAlbum(albumId: String): Flow<Album> {
        if (dao.getAlbum(albumId) == null) {
            dao.generateOfflineAlbum(albumId)?.let {
                dao.insertAlbums(listOf(it))
            } ?: run {
                try {
                    val cred = getCurrentCredentials()
                    //will throw exception if session null
                    dao.insertAlbums(listOf(
                        api.getAlbumInfo(
                            authKey = authToken(),
                            albumId = albumId
                        ).toAlbum().toAlbumEntity(username = cred.username, serverUrl = cred.serverUrl))
                    )
                } catch (e: Exception) {
                    // TODO handle no album in playlist
                }

            }
        }
        return dao.getAlbumFlow(albumId).filterNotNull().map { it.toAlbum() }
    }

    // --- HOME PAGE data ---

    private suspend fun getAlbumsStatsDb(statFilter: MainNetwork.StatFilter) = when (statFilter) {
        MainNetwork.StatFilter.random -> dao.getRandomAlbums().map { it.toAlbum() }
        MainNetwork.StatFilter.recent -> getRecentlyPlayedAlbums()
        MainNetwork.StatFilter.newest -> dao.getRecentlyReleasedAlbums().map { it.toAlbum() }
        MainNetwork.StatFilter.frequent -> dao.getMostPlayedAlbums().map { it.toAlbum() }
        MainNetwork.StatFilter.flagged -> dao.getLikedAlbums().map { it.toAlbum() }
        MainNetwork.StatFilter.forgotten -> listOf()
        MainNetwork.StatFilter.highest -> dao.getHighestRatedAlbums().map { it.toAlbum() }
    }

    private suspend fun getRecentlyPlayedAlbums() = mutableListOf<Album>().apply {
        dao.getSongHistory().forEach {
            val artistAttr = MusicAttribute(id = it.artistId, name = it.artistName)
            val album = Album(
                id = it.albumId,
                name = it.albumName,
                basename = it.albumName,
                artist = artistAttr,
                artists = listOf(artistAttr),
                year = it.year,
                genre = it.genre,
                artUrl = it.imageUrl
            )
            add(album)
        }
    }

    /**
     * generates album-history starting from song-history
     */
    private fun songHistoryToAlbumHistory(songList: List<Song>): List<Album> =
        mutableListOf<Album>().apply {
            songList.forEach {
                if (!this.map { alb -> alb.id }.contains(it.album.id)) {
                    val artistAttr = MusicAttribute(id = it.artist.id, name = it.artist.name)
                    val album = Album(
                        id = it.album.id,
                        name = it.album.name,
                        basename = it.album.name,
                        artist = artistAttr,
                        artists = listOf(artistAttr),
                        year = it.year,
                        genre = it.genre,
                        artUrl = it.imageUrl
                    )
                    add(album)
                }
            }
        }.toList()

    override val recentlyPlayedAlbumsFlow
        get() = offlineModeFlow.flatMapLatest { isOfflineModeEnabled ->
            if (!isOfflineModeEnabled) {
                dao.getSongHistoryFlow().map { songList ->
                    songHistoryToAlbumHistory(songList.map { it.toSong() })
                }
            } else {
                dao.getOfflineSongHistoryFlow().map { songList ->
                    songHistoryToAlbumHistory(songList.map { it.toSong() })
                }
            }
        }

    override val randomAlbumsFlow
        get() = offlineModeFlow.flatMapLatest { isOfflineModeEnabled ->
            if (isOfflineModeEnabled) {
                dao.getRandomOfflineSongsFlow().map { songList ->
                    songHistoryToAlbumHistory(songList.map { it.toSong() })
                }
            } else {
                dao.getRandomAlbumsFlow().map { it.map { ent -> ent.toAlbum() } }
            }
        }

    override val flaggedAlbumsFlow: Flow<List<Album>>
        get() = offlineModeFlow.flatMapLatest { isOfflineModeEnabled ->
            dao.getLikedAlbumsFlow()
                .map { it.map { ent -> ent.toAlbum() } }
                .map { albums ->
                    if (isOfflineModeEnabled) {
                        albums.filter { album -> isAlbumOffline(album) }
                    } else
                        albums
                }
        }

    override val highestRatedAlbumsFlow: Flow<List<Album>>
        get() = offlineModeFlow.flatMapLatest { isOfflineModeEnabled ->
            dao.getHighestRatedAlbumsFlow()
                .map { it.map { ent -> ent.toAlbum() } }
                .map { albums ->
                    if (isOfflineModeEnabled) {
                        albums.filter { album -> isAlbumOffline(album) }
                    } else
                        albums
                }
        }

    override val frequentAlbumsFlow
        get() = offlineModeFlow.flatMapLatest { isOfflineModeEnabled ->
            if (!isOfflineModeEnabled) {
                dao.getMostPlayedAlbumsFlow()
            } else {
                dao.getMostPlayedOfflineAlbumsFlow()
            }
        }.map { albums ->
            albums.map { it.toAlbum() }
        }


    private suspend fun isAlbumOffline(album: Album) =
        dao.getOfflineSongsFromAlbum(album.id).isNotEmpty()

    /**
     * checks if offline mode is enabled, if enabled return only albums that are available offline
     * @return true if offline mode enabled and operation successful
     */
    private suspend fun checkFilterOfflineSongs(
        albums: List<Album>,
        fc: FlowCollector<Resource<List<Album>>>?
    ): Boolean {
        if (isOfflineModeEnabled()) {
            fc?.emit(Resource.Success(
                data = albums.filter { album -> isAlbumOffline(album) },
                networkData = null)
            )
            fc?.emit(Resource.Loading(false))
            return true
        }
        return false
    }

    private suspend fun getAlbumsStats(
        statFilter: MainNetwork.StatFilter,
        fetchRemote: Boolean = true
    ) = flow {
        emit(Resource.Loading(true))

        // RECENT, FLAGGED, FREQUENT, HIGHEST are listening to db flow changes already
        val isObservedFilter = (statFilter == MainNetwork.StatFilter.recent ||
                statFilter == MainNetwork.StatFilter.highest ||
                statFilter == MainNetwork.StatFilter.frequent ||
                statFilter == MainNetwork.StatFilter.flagged)

        if (isOfflineModeEnabled() && isObservedFilter) {
            // already getting db data trough flow
            emit(Resource.Success(data = listOf(), networkData = listOf()))
            emit(Resource.Loading(false))
            return@flow
        }

        if (!isObservedFilter) {
            // just newest added songs (StatFilter.newest) right now is not an observed album stat
            val dbA = getAlbumsStatsDb(statFilter)
            if (checkFilterOfflineSongs(dbA, this)) {
                return@flow
            }
            //else
            emit(Resource.Success(data = dbA, networkData = null))
        }

        // assign limit according to defined constants
        val limit = when (statFilter) {
            MainNetwork.StatFilter.random -> NETWORK_REQUEST_LIMIT_HOME
            MainNetwork.StatFilter.recent -> NETWORK_REQUEST_LIMIT_HOME
            MainNetwork.StatFilter.newest -> NETWORK_REQUEST_LIMIT_HOME
            MainNetwork.StatFilter.frequent -> NETWORK_REQUEST_LIMIT_HOME
            MainNetwork.StatFilter.flagged -> NETWORK_REQUEST_LIMIT_HOME
            MainNetwork.StatFilter.forgotten -> NETWORK_REQUEST_LIMIT_HOME
            MainNetwork.StatFilter.highest -> Constants.config.albumHighestFetchLimit
        }

        if (fetchRemote) {
            val cred = getCurrentCredentials()
            api.getAlbumsStats(
                authToken(),
                username = getCredentials()?.username,
                filter = statFilter,
                limit = limit
            ).albums?.let { albumsDto ->
                val data = albumsDto.map { it.toAlbum() }
                dao.insertAlbums(data.map { it.toAlbumEntity(username = cred.username, serverUrl = cred.serverUrl) })
                emit(Resource.Success(data = data, networkData = data))
            } ?: run {
                // TODO throw exception, without updating the UI error message snackbar!
                //  create a MusicException that ErrorHAndler will intercept?
                // throw Exception("error connecting or getting data")
                L.e("getAlbumsStats() error connecting or getting data")
            }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbumsStats()", e, this) }

    override suspend fun getRecentAlbums() =
        getAlbumsStats(MainNetwork.StatFilter.recent)
    override suspend fun getNewestAlbums() =
        getAlbumsStats(MainNetwork.StatFilter.newest)
    override suspend fun getHighestAlbums() =
        getAlbumsStats(MainNetwork.StatFilter.highest)
    override suspend fun getFrequentAlbums() =
        getAlbumsStats(MainNetwork.StatFilter.frequent)
    override suspend fun getFlaggedAlbums() =
        getAlbumsStats(MainNetwork.StatFilter.flagged)
    override suspend fun getRandomAlbums(fetchRemote: Boolean) =
        getAlbumsStats(MainNetwork.StatFilter.random, fetchRemote)

    override suspend fun getAlbumShareLink(albumId: String) = flow {
        emit(Resource.Loading(true))
        val response = api.createShare(
            authToken(),
            id = albumId,
            type = MainNetwork.Type.album
        )
        response.error?.let { throw(MusicException(it.toError())) }
        response.publicUrl!!.apply {
            emit(Resource.Success(data = this, networkData = this))
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylistShareLink()", e, this) }

    override suspend fun rateAlbum(albumId: String, rate: Int): Flow<Resource<Any>> =
        rate(albumId, rate, MainNetwork.Type.album)

    override suspend fun likeAlbum(id: String, like: Boolean) =
        like(id, like, MainNetwork.Type.album)
}
