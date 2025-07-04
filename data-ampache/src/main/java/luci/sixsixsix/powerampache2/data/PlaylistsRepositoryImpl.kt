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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.common.Constants.ADMIN_USERNAME
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.SongsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.di.LocalDataSource
import luci.sixsixsix.powerampache2.di.OfflineModeDataSource
import luci.sixsixsix.powerampache2.di.RemoteDataSource
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.common.Constants.ALWAYS_FETCH_ALL_PLAYLISTS
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.TotalCount
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.NullDataException
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@Singleton
class PlaylistsRepositoryImpl @Inject constructor(
    @LocalDataSource private val playlistsDbDataSource: PlaylistsDbDataSource,
    @OfflineModeDataSource private val playlistsOfflineDataSource: PlaylistsOfflineDataSource,
    @RemoteDataSource private val playlistsRemoteDataSource: PlaylistsRemoteDataSource,
    private val api: MainNetwork,
    db: MusicDatabase,
    private val errorHandler: ErrorHandler,
    private val configProvider: ConfigProvider,
): BaseAmpacheRepository(api, db, errorHandler), PlaylistsRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val playlistsFlow: Flow<List<Playlist>> = offlineModeFlow.flatMapLatest { isOffline ->
        if (isOffline) { playlistsOfflineDataSource.playlistsFlow
        } else playlistsDbDataSource.playlistsFlow
    }

    override fun getPlaylistFlow(id: String) = playlistsDbDataSource.getPlaylist(id)

    override suspend fun getPlaylists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading(true))

        val dbList = mutableListOf<Playlist>()
        if (offset == 0) {
            val isOfflineModeEnabled = isOfflineModeEnabled()
            val localPlaylists = if (isOfflineModeEnabled) {
                playlistsOfflineDataSource.getPlaylists(query)
            } else {
                playlistsDbDataSource.getPlaylists(query)
            }
            dbList.addAll(localPlaylists)

            val isDbEmpty = localPlaylists.isEmpty() && query.isEmpty()
            if (!isDbEmpty || isOfflineModeEnabled) {
                // show empty list if offline mode enabled because this will be the actual result, there won't be an api call
                emit(Resource.Success(data = localPlaylists))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly || isOfflineModeEnabled) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val cred = getCurrentCredentials()
        val user = cred.username

        if (query.isBlank()) {
            // fetch user and admin playlists to quick load user's playlists before everyone else's
            getUserPlaylists(username = user, serverUrl = cred.serverUrl)
        }

        if (Constants.config.playlistsServerAllFetch) {
            val playlistNetwork = getPlaylistsNetwork(
                auth = authToken(),
                username = user,
                offset = offset,
                query = query,
                fetchAll = ALWAYS_FETCH_ALL_PLAYLISTS
            )

            val shouldClearBeforeAdding = query.isBlank() && offset == 0 &&
                playlistNetwork.isNotEmpty() &&
                (!AmpacheModel.listsHaveSameElements(playlistNetwork, dbList))

            playlistsDbDataSource.savePlaylistsToDb(
                playlists = playlistNetwork,
                username = user, serverUrl = cred.serverUrl,
                shouldClearBeforeAdding = shouldClearBeforeAdding
            )

            // stick to the single source of truth pattern despite performance deterioration
            emit(Resource.Success(
                data = playlistsDbDataSource.getPlaylists(query), networkData = playlistNetwork))

            insertSongRefs(playlistNetwork)
        } else {
            Constants.config.run {
                smartlistsUserFetch || playlistsUserFetch || playlistsAdminFetch || smartlistsAdminFetch
            }.let { atLeastOneUserPlaylist ->
                // if at least one user playlist present and playlistsServerAllFetch=false
                // remove all playlists that are not user or admin playlists
                // TODO this is a hack, repository should not access dao directly
                if (atLeastOneUserPlaylist)
                    dao.deleteNonUserAdminPlaylist()
            }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylists()", e, this) }

    private suspend fun insertSongRefs(playlistNetwork: List<Playlist>) {
        val cred = getCurrentCredentials()
        // insert song references in db
        playlistNetwork.forEach { pl ->
            if (pl.songRefs.isNotEmpty()) {
                playlistsDbDataSource.savePlaylistSongRefsToDb(
                    songRefs = pl.songRefs,
                    playlistId = pl.id,
                    username = cred.username,
                    serverUrl = cred.serverUrl
                )
            }
        }
    }

    private suspend fun getUserPlaylists(username: String, serverUrl: String) {
        // first get user playlists and save to database, saving to db will trigger the flow
        val accumulatorList = mutableListOf<Playlist>()
        if (Constants.config.playlistsUserFetch) {
            try {
                api.getUserPlaylists(authToken()).playlist
                    ?.map { it.toPlaylist().also { pl -> accumulatorList.add(pl) } }
                    ?.map { it.toPlaylistEntity(username, serverUrl) }
                    ?.let { userPlaylists ->
                        dao.insertPlaylists(userPlaylists)
                    }
            } catch (e: Exception) {
                L.e(e)
            }
        }

        if (Constants.config.smartlistsUserFetch) {
            try {
                api.getUserSmartlists(authToken()).playlist
                    ?.map { it.toPlaylist().also { pl -> accumulatorList.add(pl) } }
                    ?.map { it.toPlaylistEntity(username, serverUrl) }
                    ?.let { userPlaylists ->
                        dao.insertPlaylists(userPlaylists)
                    }
            } catch (e: Exception) {
                L.e(e)
            }
        }

        if (Constants.config.playlistsAdminFetch) {
            // fetch admin playlists
            try {
                api.getUserPlaylists(authToken(), user = ADMIN_USERNAME).playlist
                    ?.map { it.toPlaylist().also { pl -> accumulatorList.add(pl) } }
                    ?.map { it.toPlaylistEntity(username, serverUrl) }
                    ?.let { userPlaylists ->
                        dao.insertPlaylists(userPlaylists)
                    }
            } catch (e: Exception) {
                L.e(e)
            }
        }

        if (Constants.config.smartlistsAdminFetch) {
            try {
                api.getUserSmartlists(authToken(), user = ADMIN_USERNAME).playlist
                    ?.map { it.toPlaylist().also { pl -> accumulatorList.add(pl) } }
                    ?.map { it.toPlaylistEntity(username, serverUrl) }
                    ?.let { userPlaylists ->
                        dao.insertPlaylists(userPlaylists)
                    }
            } catch (e: Exception) {
                L.e(e)
            }
        }

        if (accumulatorList.isNotEmpty())
            insertSongRefs(accumulatorList.toList())
    }

    private suspend fun getPlaylistsNetwork(
        auth: String,
        username: String,
        offset: Int,
        query: String,
        fetchAll: Boolean = false
    ) = mutableListOf<Playlist>().apply {
        var off = if (offset == 0 && fetchAll) 0 else offset
        val maxIterations = 100
        var counter = 0
        var isMoreAvailable = false
        do {
            var totalCount = Int.MAX_VALUE
            val response = try {
                val playlistsPair: Pair<List<Playlist>, TotalCount> =
                    playlistsRemoteDataSource.getPlaylists(auth,
                        query = query,
                        offset = off,
                        limit = Constants.config.playlistFetchLimit
                    )
                totalCount = playlistsPair.second
                playlistsPair.first
            } catch(he: HttpException) {
                // if httpException, try again with no `include`
                try {
                    val playlistsPair = playlistsRemoteDataSource.getPlaylists(auth, query = query, offset = off,
                            limit = Constants.config.playlistFetchLimit, include = null)
                    totalCount = playlistsPair.second
                    playlistsPair.first
                } catch (e: Exception) {
                    if (!fetchAll) throw e
                    listOf()
                }
            } catch (e: Exception) {
                if (!fetchAll) throw e
                listOf()
            }

            val responseSize = response.size
            off += responseSize

            val playlists = if (configProvider.SHOW_EMPTY_PLAYLISTS) {
                response
            } else {
                response.filter { toFilter ->
                    toFilter.items?.let { itemsCount ->
                        itemsCount > 0 || toFilter.owner?.lowercase() == username.lowercase() // edge-case default behaviour, user==null and owner==null will show the playlist
                    } ?: (toFilter.owner?.lowercase() == username.lowercase()) // if the count is null fallback to show the playlist if the user is the owner
                }
            }

            addAll(playlists)
            // if the current response is not empty there might be more
            // check if the total count data is less than then current offset
            isMoreAvailable = responseSize != 0 && totalCount > off
        } while (fetchAll && isMoreAvailable && ++counter < maxIterations)
    }.toList()


    /**
     * There is currently no way to get songs given the playlistId
     * Songs are cached for quick access from SongsScreen and Albums
     */
    override suspend fun getSongsFromPlaylist(
        playlist: Playlist,
        fetchRemote: Boolean
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val playlistId = playlist.id

        if (isOfflineModeEnabled()) {
            emit(Resource.Success(data = playlistsOfflineDataSource.getSongsFromPlaylist(playlistId), networkData = null))
            emit(Resource.Loading(false))
            return@flow
        }

        val dbData = playlistsDbDataSource.getSongsFromPlaylist(playlist)
        emit(Resource.Success(data = dbData, networkData = null))

        if (!fetchRemote) {
            emit(Resource.Loading(false))
            return@flow
        }

        val shouldEmitSteps = dbData.size < Constants.config.playlistSongsFetchLimit
        val cred = getCurrentCredentials()
        val songs = mutableListOf<Song>()
        var isFinished = false
        var limit = Constants.config.playlistSongsFetchLimit
        var offset = 0
        var lastException: MusicException? = null
        do {
            try {
                val partialResponse = playlistsRemoteDataSource.getSongsFromPlaylist(authToken(),
                    playlistId = playlistId,
                    limit = limit,
                    offset = offset
                )

                if (partialResponse.isNotEmpty()) {
                    songs.addAll(partialResponse)
                    if (shouldEmitSteps) {
                        emit(Resource.Success(data = songs, networkData = partialResponse))
                    }
                    // if the result is smaller than then limit, there is no more data to fetch
                    isFinished = partialResponse.size < limit
                } else { // if no more items to fetch finish
                    isFinished = true
                }
            } catch (me: MusicException) {
                lastException = me
                errorHandler.logError(me)
                // for partial recovery:in case a subset of songs fails, reduce the limit to
                // try and fetch at least part of the songs in the next iteration
                SongsResponse(songs = null).also { limit /= 2 }
            } catch (nde: NullDataException) {
                // A NullDataException is thrown when data is null, and no MusicException is thrown.
                isFinished = true
                errorHandler.logError(nde)
            }
            offset += limit
            limit = (limit + 66) * 2
        } while (!isFinished)

        if (!shouldEmitSteps) {
            // TODO: BREAKING_RULE(single source of truth). Playlists can be very large, emitting
            //  network data to avoid slow db operations blocking the UI.
            //  also there is no pagination for db calls
            emit(Resource.Success(data = songs.toList(), networkData = songs))
        }

        // DO LENGTHY OPERATIONS AFTER EMITTING DATA
        // Save playlists songs to db, including smartlists, smartlists only to be used in offline mode.
        playlistsDbDataSource.savePlaylistSongsToDb(songs, playlistId, username = cred.username, serverUrl = cred.serverUrl)
        emit(Resource.Loading(false))
        // Save songs after emitting success and stop loading. Songs are saved regardless for quick
        // access from SongsScreen and Albums.
        cacheSongs(songs)
    }.catch { e -> errorHandler("getSongsFromPlaylist()", e, this) }

    override suspend fun ratePlaylist(playlistId: String, rate: Int): Flow<Resource<Any>> =
        rate(playlistId, rate, MainNetwork.Type.playlist)

    override suspend fun likePlaylist(id: String, like: Boolean): Flow<Resource<Any>> =
        like(id, like, MainNetwork.Type.playlist)

    override suspend fun getPlaylistShareLink(playlistId: String) = flow {
        emit(Resource.Loading(true))
        val response = api.createShare(
            authToken(),
            id = playlistId,
            type = MainNetwork.Type.playlist
        )
        response.error?.let { throw(MusicException(it.toError())) }
        response.publicUrl!!.apply {
            emit(Resource.Success(data = this, networkData = this))
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylistShareLink()", e, this) }

    override suspend fun addSongToPlaylist(
        playlistId: String,
        songId: String
    ) = flow {
        emit(Resource.Loading(true))
        if (addSingleSongToPlaylist(playlistId = playlistId, songId = songId)) {
            val cred = getCurrentCredentials()
            dao.insertPlaylistSongs(
                PlaylistSongEntity.newEntries(listOf(dao.getSongById(songId)!!.toSong()),
                    playlistId, username = cred.username, serverUrl = cred.serverUrl)
            )
            emit(Resource.Success(data = Any(), networkData = Any()))
        } else {
            throw Exception("error getting a response from addSongToPlaylist call")
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("addSongToPlaylist()", e, this) }

    private suspend fun addSingleSongToPlaylist(
        playlistId: String,
        songId: String
    ): Boolean = api.addSongToPlaylist(
        authKey = authToken(),
        playlistId = playlistId,
        songId = songId
    ).run {
        error?.let { throw (MusicException(it.toError())) }
        success != null
    }

    override suspend fun removeSongFromPlaylist(
        playlistId: String,
        songId: String
    ) = flow {
        emit(Resource.Loading(true))
        api.removeSongFromPlaylist(
            authKey = authToken(),
            playlistId = playlistId,
            songId = songId
        ).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
                emit(Resource.Success(data = Any(), networkData = Any()))
            } else {
                throw Exception("error getting a response from removeSongFromPlaylist call")
            }
        }
        dao.deleteSongFromPlaylist(playlistId, songId)
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("removeSongFromPlaylist()", e, this) }

    override suspend fun createNewPlaylist(
        name: String,
        playlistType: PlaylistType
    ) = flow {
        emit(Resource.Loading(true))
        playlistsRemoteDataSource.createNewPlaylist(
            authToken = authToken(),
            name = name,
            playlistType = playlistType
        ).also {
            emit(Resource.Success(data = it, networkData = it))
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("createNewPlaylist()", e, this) }

    override suspend fun deletePlaylist(id: String) = flow {
        emit(Resource.Loading(true))
        api.deletePlaylist(
            authKey = authToken(),
            playlistId = id
        ).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
                emit(Resource.Success(data = Any(), networkData = Any()))
            } else {
                throw Exception("error getting a response from deletePlaylist call")
            }
        }
        dao.deletePlaylist(id)
        dao.clearPlaylistSongs(id)
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("deletePlaylist()", e, this) }

    private fun songListToCommaSeparatedIds(items: List<Song>) =
        items.joinToString(separator = ",") { it.mediaId }

    private fun trackPositionsCommaSeparated(items: List<Song>) =
        ArrayList<Int>().apply {
            for(i in 1..items.size) {
                add(i)
            }
        }.joinToString(separator = ",")
    
    override suspend fun editPlaylist(
        playlistId: String,
        playlistName: String?,
        items: List<Song>,
        owner: String?,
        tracks: String?, // will be calculated if null
        playlistType: PlaylistType
    ) = flow {
        emit(Resource.Loading(true))

        val commaSeparatedIds = when(items.size) {
            0 -> null
            else -> songListToCommaSeparatedIds(items)
        }
        val commaSeparatedTracks = if (tracks.isNullOrBlank()) {
            when(items.size) {
                0 -> null
                else -> trackPositionsCommaSeparated(items)
            }
        } else tracks

        api.editPlaylist(
            authKey = authToken(),
            playlistId = playlistId,
            items = commaSeparatedIds,
            tracks = commaSeparatedTracks,
            name = playlistName
        ).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {

                // insert changes into database
                dao.clearPlaylistSongs(playlistId)
                getCurrentCredentials().let { cred ->
                    dao.insertPlaylistSongs(PlaylistSongEntity.newEntries(
                        songs = items,
                        playlistId = playlistId,
                        username = cred.username,
                        serverUrl = cred.serverUrl
                    ))
                }

                emit(Resource.Success(data = Any(), networkData = Any()))
            } else {
                throw Exception("error getting a response from editPlaylist call")
            }
        }

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("editPlaylist()", e, this) }

    private suspend fun cacheSinglePlaylist(
        playlist: Playlist,
        songs: List<Song>,
        clearBeforeAdd: Boolean = true
    ) = getCurrentCredentials().let { cred ->
        dao.insertPlaylists(listOf(playlist.toPlaylistEntity(username = cred.username, serverUrl = cred.serverUrl)))
        if (clearBeforeAdd) {
            dao.clearPlaylistSongs(playlist.id)
        }
        dao.insertPlaylistSongs(PlaylistSongEntity.newEntries(songs, playlist.id, username = cred.username, serverUrl = cred.serverUrl))
    }

    override suspend fun addSongsToPlaylist(
        playlist: Playlist,
        newSongs: List<Song>
    ) = flow {
        emit(Resource.Loading(true))

        if (!Constants.config.playlistAddNewEnable) {
            val plResult = addSongsToPlaylistFallback(playlist, songsToAdd = newSongs)
            cacheSinglePlaylist(plResult, newSongs,false)
            emit(Resource.Success(data = plResult, networkData = plResult))
            emit(Resource.Loading(false))
            return@flow
        }

        // Get old version of the playlist
        val existingSongs = LinkedHashSet(playlistsDbDataSource.getSongsFromPlaylist(playlist))
            .ifEmpty {
                L.e("addSongsToPlaylist() list of songs from db is empty")
                // if playlist not stored locally, fetch a new version
                val response = api.getSongsFromPlaylist(authToken(), albumId = playlist.id)
                response.error?.let { throw(MusicException(it.toError())) }
                LinkedHashSet(response.songs!!.map { songDto -> songDto.toSong() }) // will throw exception if songs null
            }
        val existingPlusNewSongs = ArrayList(existingSongs).apply { addAll(newSongs) }.toList()

        // try new method, include every song (use songList)
        val playlistEdited = try {
            editPlaylistNewApi(playlist = playlist, songList = existingPlusNewSongs)
        } catch (e: Exception) {
            L.e(e)
            // fallback to old method, add one by one, only new songs (use songsToAdd)
            val songsToAdd = ArrayList(newSongs).apply {
                // remove all existing songs from new songs to add to the playlist
                removeAll(existingSongs)
            }
            addSongsToPlaylistFallback(playlist = playlist, songsToAdd = songsToAdd)
        }

        cacheSinglePlaylist(playlistEdited, existingPlusNewSongs)
        emit(Resource.Success(data = playlistEdited, networkData = playlistEdited))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("editPlaylist()", e, this) }

    /**
     * Fallback method for when the new method is not supported
     * Add one by one, only new songs (use songsToAdd)
     */
    private suspend fun addSongsToPlaylistFallback(
        playlist: Playlist,
        songsToAdd: List<Song>
    ): Playlist {
        for (song in songsToAdd) {
            try {
                addSingleSongToPlaylist(playlistId = playlist.id, songId = song.mediaId)
            } catch (e: Exception) {
                L.e(e)
                // TODO handle error
            }
        }
        // TODO is this call necessary? can't I just return the passed playlist?
        return api.getPlaylist(authToken(), playlist.id).toPlaylist()
    }

    override suspend fun createNewPlaylistAddSongs(
        name: String,
        playlistType: PlaylistType,
        songsToAdd: List<Song>
    ) = flow {
        emit(Resource.Loading(true))
        // create new playlist
        val playlist = api.createNewPlaylist(
            authKey = authToken(),
            name = name,
            playlistType = playlistType
        ).toPlaylist() // TODO no error check

        val playlistEdited = try {
            editPlaylistNewApi(playlist = playlist, songsToAdd)
        } catch (e: Exception) {
            // fallback to old method, add one by one
            addSongsToPlaylistFallback(playlist = playlist, songsToAdd = songsToAdd)
        }

        cacheSinglePlaylist(playlistEdited, songsToAdd, clearBeforeAdd = false)
        emit(Resource.Success(data = playlistEdited, networkData = playlistEdited))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("createNewPlaylistAddSongs()", e, this) }

    @Throws(Exception::class)
    private suspend fun editPlaylistNewApi(
        playlist: Playlist,
        songList: List<Song>
    ) = api.editPlaylist(
        authKey = authToken(),
        playlistId = playlist.id,
        items = when(songList.size) {
            0 -> null
            else -> songListToCommaSeparatedIds(songList)
        },
        tracks = trackPositionsCommaSeparated(songList),
        owner = playlist.owner
        // TODO: playlist type is null in nextcloud, removed from call
        // playlistType = playlist.type ?: throw Exception("addSongsToPlaylist problem with playlist type NULL")
    ).run {
        error?.let { throw(MusicException(it.toError())) }
        if (success != null) {
            val updatedPlaylist = api.getPlaylist(authToken(), playlist.id).toPlaylist()
            // check if any of the new songs got added
            val playlistItems = playlist.items ?: 0
            if ((updatedPlaylist.items == null) || (playlist.items == null) ||
                (updatedPlaylist.items <= playlistItems)) {
                throw Exception("The size of the edited playlist and the size of the new playlist are the same. Something went wrong")
            }
            updatedPlaylist
        } else {
            throw Exception("error getting a response from editPlaylist call")
        }
    }

    /**
     * this method is only available in the dev version of API6 (as of Feb 15 2024)
     * will not work with any previous api versions of the server
     */
    @Throws(Exception::class)
    private suspend fun createNewPlaylistAddSongsNewApi(
        name: String,
        playlistType: PlaylistType,
        songsToAdd: List<Song>
    ): Playlist {
        // create new playlist
        val playlist = api.createNewPlaylist(
            authKey = authToken(),
            name = name,
            playlistType = playlistType
        ).toPlaylist() // TODO no error check

        return editPlaylistNewApi(playlist, songsToAdd)
    }

    /**
     * this method is only available in the dev version of API6 (as of Feb 15 2024)
     * will not work with any previous api versions of the server
     */
    @Throws(Exception::class)
    private suspend fun addSongsToExistingPlaylistNewApi(
        playlist: Playlist,
        songsToAdd: List<Song>
    ): Playlist {
        // Get old version of the playlist
        val response = api.getSongsFromPlaylist(authToken(), albumId = playlist.id)
        response.error?.let { throw(MusicException(it.toError())) }
        val songList = response.songs!!.map { songDto -> songDto.toSong() }.toMutableList() // will throw exception if songs null
        songList.addAll(songsToAdd)

        return editPlaylistNewApi(playlist, songsToAdd)
    }
}
