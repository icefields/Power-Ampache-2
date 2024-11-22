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

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.ADMIN_USERNAME
import luci.sixsixsix.powerampache2.common.Constants.ALWAYS_FETCH_ALL_PLAYLISTS
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylist
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.PlaylistsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.SongsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.Song
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
    private val api: MainNetwork,
    db: MusicDatabase,
    private val errorHandler: ErrorHandler
): BaseAmpacheRepository(api, db, errorHandler), PlaylistsRepository {

    override val playlistsFlow = dao.playlistsLiveData().asFlow().map { entities ->
        val isOfflineModeEnabled = isOfflineModeEnabled()
        entities.filter {
            if (isOfflineModeEnabled) {
                isPlaylistOffline(it.id)
            } else it != null
        }.map {
            it.toPlaylist()
        }
    }

    override suspend fun getPlaylists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading(true))

        val dbList = mutableListOf<PlaylistEntity>()
        if (offset == 0) {
            val isOfflineModeEnabled = isOfflineModeEnabled()
            val localPlaylists = dao.searchPlaylists(query).filter {
                if (isOfflineModeEnabled) {
                    isPlaylistOffline(it.id)
                } else it != null
            }
            dbList.addAll(localPlaylists)

            val isDbEmpty = localPlaylists.isEmpty() && query.isEmpty()
            if (!isDbEmpty || isOfflineModeEnabled) {
                // show empty list if offline mode enabled because this will be the actual result, there won't be an api call
                emit(Resource.Success(data = localPlaylists.map { it.toPlaylist() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly || isOfflineModeEnabled) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val cred = getCurrentCredentials()
        val user = cred.username

        if (query.isNullOrBlank()) {
            // fetch user and admin playlists to quick load user's playlists before everyone else's
            getUserPlaylists(username = user, serverUrl = cred.serverUrl)
        }

        if (Constants.config.playlistsServerAllFetch) {
            val playlistNetwork =
                getPlaylistsNetwork(authToken(), user, offset, query, ALWAYS_FETCH_ALL_PLAYLISTS)
            val playlistNetworkEntities =
                playlistNetwork.map {
                    it.toPlaylistEntity(username = user, serverUrl = cred.serverUrl)
                }

            if (query.isNullOrBlank() &&
                offset == 0 &&
                playlistNetwork.isNotEmpty() && // TODO: check for network errors instead of empty list
                (!AmpacheModel.listsHaveSameElements(playlistNetwork, dbList.map { it.toPlaylist() }))
            ) {
                L("aaaa", "lists don't have same elements")
                // avoid clearing if lists are equal, insert will already replace the old versions
                dao.clearPlaylists()
            }
            dao.insertPlaylists(playlistNetworkEntities)
            // stick to the single source of truth pattern despite performance deterioration
            emit(Resource.Success(
                data = dao.searchPlaylists(query).map { it.toPlaylist() },
                networkData = playlistNetwork)
            )
        } else {
            Constants.config.run {
                smartlistsUserFetch || playlistsUserFetch || playlistsAdminFetch || smartlistsAdminFetch
            }.let { atLeastOneUserPlaylist ->
                // if at least one user playlist present and playlistsServerAllFetch=false
                // remove all playlists that are not user or admin playlists
                if (atLeastOneUserPlaylist)
                    dao.deleteNonUserAdminPlaylist()
            }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylists()", e, this) }

    private suspend fun getUserPlaylists(username: String, serverUrl: String) {
        // first get user playlists and save to database, saving to db will trigger the flow
        if (Constants.config.playlistsUserFetch) {
            try {
                api.getUserPlaylists(authToken()).playlist
                    ?.map { it.toPlaylist() }
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
                    ?.map { it.toPlaylist() }
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
                    ?.map { it.toPlaylist() }
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
                    ?.map { it.toPlaylist() }
                    ?.map { it.toPlaylistEntity(username, serverUrl) }
                    ?.let { userPlaylists ->
                        dao.insertPlaylists(userPlaylists)
                    }
            } catch (e: Exception) {
                L.e(e)
            }
        }
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
            val response = try {
                api.getPlaylists(auth, filter = query, offset = off, limit = Constants.config.playlistFetchLimit)
            } catch (e: Exception) {
                if (!fetchAll) throw e
                PlaylistsResponse(totalCount = 0, playlist = listOf())
            }

            response.error?.let {
                // do not stop in case of exception if fetchAll == true
                if (!fetchAll)
                    throw MusicException(it.toError())
            }

            val responseSize = response.playlist?.size ?: 0
            val totalCount = response.totalCount?.let { tot ->
                // if this field is null or empty in the response, return max possible value
                if (tot > 0) {
                    tot
                } else {
                    Int.MAX_VALUE
                }
            } ?: Int.MAX_VALUE
            off += responseSize

            val playlists = response.playlist?.let { responsePlaylist ->
                (if (BuildConfig.SHOW_EMPTY_PLAYLISTS) {
                    responsePlaylist // will throw exception if playlist null
                } else {
                    responsePlaylist.filter { dtoToFilter -> // will throw exception if playlist null
                        dtoToFilter.items?.let { itemsCount ->
                            itemsCount > 0 || dtoToFilter.owner == username // edge-case default behaviour, user==null and owner==null will show the playlist
                        } ?: (dtoToFilter.owner == username) // if the count is null fallback to show the playlist if the user is the owner
                    }
                })
            }?.map { it.toPlaylist() } ?: listOf()

            //dao.insertPlaylists(playlists.map { it.toPlaylistEntity(username = username, serverUrl = getCurrentCredentials().serverUrl) })
            addAll(playlists)
            // if the current response is not empty there might be more
            // check if the total count data is less than then current offset
            isMoreAvailable = responseSize != 0 && totalCount > off
        } while (fetchAll && isMoreAvailable && ++counter < maxIterations)
    }.toList()

    override fun getPlaylist(id: String) =
        dao.playlistLiveData(id).filterNotNull().mapNotNull { it.toPlaylist() }

    private suspend fun isPlaylistOffline(playlistId: String) =
        dao.getOfflineSongsFromPlaylist(playlistId).isNotEmpty()

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

        val isOfflineMode = isOfflineModeEnabled()
        if (isOfflineMode) {
            // if offline mode enabled, grab all the offline data and emit, then return
            emit(Resource.Success(
                data = dao.getOfflineSongsFromPlaylist(playlistId).map { it.toSong() },
                networkData = null))
            emit(Resource.Loading(false))
            return@flow
        }

        // emit saved data first
        val dbData = dao.getSongsFromPlaylist(playlistId).map { it.toSong() }
        emit(Resource.Success(data = dbData, networkData = null))
        val shouldEmitSteps = dbData.size < Constants.config.playlistSongsFetchLimit

        //else
        val cred = getCurrentCredentials()
        val songs = mutableListOf<Song>()
        var isFinished = false
        var limit = Constants.config.playlistSongsFetchLimit
        var offset = 0
        var lastException: MusicException? = null
        do {
            val response = try {
                api.getSongsFromPlaylist(
                    authToken(),
                    albumId = playlistId,
                    limit = limit,
                    offset = offset
                )
            } catch (e: Exception) {
                errorHandler.logError(e)
                SongsResponse(songs = null).also {
                    // for partial recovery:in case a subset of songs fails, reduce the limit to
                    // try and fetch at least part of the songs in the next iteration
                    limit /= 2
                }
            }

            // save the exception if any
            response.error?.let { lastException = MusicException(it.toError()) }
            // a response exists
            response.songs?.let { songsDto ->
                val partialResponse = songsDto.map { songDto -> songDto.toSong() }
                if (partialResponse.isNotEmpty()) {
                    songs.addAll(partialResponse)
                    if (shouldEmitSteps) {
                        emit(Resource.Success(data = songs, networkData = partialResponse))
                    }

                    // if the result is smaller than then limit, there is no more data to fetch
                    isFinished = partialResponse.size < limit
                } else {
                    // if no more items to fetch finish
                    isFinished = true
                }
            } ?: run {
                // a response DOES NOT exist
                // if there's an error and so songs retrieved just finish
                isFinished = true
            }
            offset += limit
        } while (!isFinished)

        if (!shouldEmitSteps) {
            // TODO: BREAKING_RULE(single source of truth), emitting network data to avoid slow
            //  db operations before getting a result on the UI
            emit(Resource.Success(data = songs.toList(), networkData = songs))
        }

        // DO LENGTHY OPERATIONS AFTER EMITTING DATA
        dao.clearPlaylistSongs(playlistId)
        dao.insertPlaylistSongs(PlaylistSongEntity.newEntries(songs, playlistId, username = cred.username, serverUrl = cred.serverUrl))

// commented because: DO LENGTHY OPERATIONS AFTER EMITTING DATA, this has been moved before the db operations
//        if (!shouldEmitSteps) {
//            emit(Resource.Success(
//                data = dao.getSongsFromPlaylist(playlistId).map { it.toSong() },
//                networkData = songs)
//            )
//        }
        
        emit(Resource.Loading(false))
        // cache songs after emitting success
        // Songs are cached regardless for quick access from SongsScreen and Albums
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
        api.createNewPlaylist(
            authKey = authToken(),
            name = name,
            playlistType = playlistType
        ).run {
            toPlaylist() // TODO no error check
        }.also {
            emit(Resource.Success(data = it, networkData = it))
        }
        emit(Resource.Loading(false))
    }

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
        tracks: String?,
        playlistType: PlaylistType
    ) = flow {
        emit(Resource.Loading(true))
        val commaSeparatedIds = when(items.size) {
            0 -> null
            else -> songListToCommaSeparatedIds(items)
        }
        api.editPlaylist(
            authKey = authToken(),
            playlistId = playlistId,
            items = commaSeparatedIds,
            tracks = tracks,
            name = playlistName
        ).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
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
        val existingSongs = LinkedHashSet(dao.getSongsFromPlaylist(playlist.id).map { songDb -> songDb.toSong() })
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
            editPlaylistNewApi(playlist = playlist, songList =  existingPlusNewSongs)
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
            val updatedPlaylist = api.getPlaylist(authToken(), playlist.id)
            // check if any of the new songs got added
            L(updatedPlaylist.items, playlist.items)
            if ((updatedPlaylist.items == null) || (playlist.items == null) ||
                (updatedPlaylist.items <= playlist.items)) {
                throw Exception("The size of the edited playlist and the size of the new playlist are the same. Something went wrong")
            }
            updatedPlaylist.toPlaylist()
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
