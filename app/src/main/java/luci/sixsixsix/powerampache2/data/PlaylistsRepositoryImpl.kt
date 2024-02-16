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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylist
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.SuccessResponse
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
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
class PlaylistsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val musicRepository: MusicRepository,
    private val errorHandler: ErrorHandler
): PlaylistsRepository {
    private val dao = db.dao
    private fun getSession(): Session? = musicRepository.sessionLiveData.value
    override val playlistsLiveData = dao.playlistsLiveData().map { entities ->
        entities.map {
            it.toPlaylist()
        }
    }

    override suspend fun getPlaylists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
            val localPlaylists = dao.searchPlaylists(query)
            val isDbEmpty = localPlaylists.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localPlaylists.map { it.toPlaylist() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!
        val user = dao.getCredentials()?.username
        val response = api.getPlaylists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val playlists = (if (BuildConfig.SHOW_EMPTY_PLAYLISTS) {
            response.playlist!! // will throw exception if playlist null
        } else {
            response.playlist!!.filter { dtoToFilter -> // will throw exception if playlist null
                dtoToFilter.items?.let { itemsCount ->
                    itemsCount > 0 || dtoToFilter.owner == user // edge-case default behaviour, user==null and owner==null will show the playlist
                } ?: (dtoToFilter.owner == user) // if the count is null fallback to show the playlist if the user is the owner
            }
        }).map { it.toPlaylist() }

        if ( // Playlists change too often, clear every time
            query.isNullOrBlank() &&
            offset == 0
            //&& Constants.CLEAR_TABLE_AFTER_FETCH
            ) {
            // if it's just a search do not clear cache
            dao.clearPlaylists()
        }
        dao.insertPlaylists(playlists.map { it.toPlaylistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchPlaylists(query).map { it.toPlaylist() }, networkData = playlists))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylists()", e, this) }

    private suspend fun like(id: String, like: Boolean, type: MainNetwork.Type): Flow<Resource<Any>> = flow {
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
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("likeSong()", e, this) }

    override suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.song)

    override suspend fun likeAlbum(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.album)

    override suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.artist)

    override suspend fun likePlaylist(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.playlist)

    override suspend fun getPlaylistShareLink(playlistId: String) = flow {
        emit(Resource.Loading(true))
        val response = api.createShare(
            getSession()!!.auth,
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
//        val auth = getSession()!!
//        api.addSongToPlaylist(
//            authKey = auth.auth,
//            playlistId = playlistId,
//            songId = songId
//        ).apply {
//            error?.let { throw(MusicException(it.toError())) }
//            if (success != null) {
//                emit(Resource.Success(data = Any(), networkData = Any()))
//            } else {
//                throw Exception("error getting a response from addSongToPlaylist call")
//            }
//        }
        if (addSingleSongToPlaylist(playlistId = playlistId, songId = songId)) {
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
        authKey = getSession()!!.auth,
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
        val auth = getSession()!!
        api.removeSongFromPlaylist(
            authKey = auth.auth,
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
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("removeSongFromPlaylist()", e, this) }

    override suspend fun createNewPlaylist(
        name: String,
        playlistType: PlaylistType
    ) = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.createNewPlaylist(
            authKey = auth.auth,
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
        val auth = getSession()!!
        api.deletePlaylist(
            authKey = auth.auth,
            playlistId = id
        ).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
                emit(Resource.Success(data = Any(), networkData = Any()))
            } else {
                throw Exception("error getting a response from deletePlaylist call")
            }
        }
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
        val auth = getSession()!!
        api.editPlaylist(
            authKey = auth.auth,
            playlistId = playlistId,
            items = commaSeparatedIds,
            tracks = tracks,
            name = playlistName,
            playlistType = playlistType
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

    override suspend fun addSongsToPlaylist(
        playlist: Playlist,
        newSongs: List<Song>
    ) = flow {
        emit(Resource.Loading(true))

        // Get old version of the playlist
        val auth = getSession()!!
        val response = api.getSongsFromPlaylist(auth.auth, albumId = playlist.id)
        response.error?.let { throw(MusicException(it.toError())) }
        val existingSongs = LinkedHashSet(response.songs!!.map { songDto -> songDto.toSong() }) // will throw exception if songs null

        // try new method, include every song (use songList)
        val playlistEdited = try {
            val existingPlusNewSongs = ArrayList(existingSongs).apply { addAll(newSongs) }.toList()
            editPlaylistNewApi(
                playlist = playlist,
                songList =  existingPlusNewSongs
            )
        } catch (e: Exception) {
            L.e(e)
            L(newSongs.size, existingSongs.size)
            // fallback to old method, add one by one, only new songs (use songsToAdd)
            val songsToAdd = ArrayList(newSongs).apply {
                // remove all existing songs from new songs to add to the playlist
                removeAll(existingSongs)
            }
            L(songsToAdd.size)


            for (song in songsToAdd) {
                try {
                    addSingleSongToPlaylist(playlistId = playlist.id, songId = song.mediaId)
                } catch (e: Exception) {
                    L.e(e)

                    // TODO handle error
                }
            }
            // tODO is this call necessary?
            api.getPlaylist(getSession()?.auth!!, playlist.id).toPlaylist()
        }

        emit(Resource.Success(data = playlistEdited, networkData = playlistEdited))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("editPlaylist()", e, this) }

    override suspend fun createNewPlaylistAddSongs(
        name: String,
        playlistType: PlaylistType,
        songsToAdd: List<Song>
    ) = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        // create new playlist
        val playlist = api.createNewPlaylist(
            authKey = auth.auth,
            name = name,
            playlistType = playlistType
        ).toPlaylist() // TODO no error check

        val playlistEdited = try {
            editPlaylistNewApi(playlist = playlist, songsToAdd)
        } catch (e: Exception) {
            // fallback to old method, add one by one
            for (song in songsToAdd) {
                try {
                    addSingleSongToPlaylist(playlistId = playlist.id, songId = song.mediaId)
                }catch (e: Exception) {
                    // TODO handle error
                }
            }
            // tODO is this call necessary?
            api.getPlaylist(getSession()?.auth!!, playlist.id).toPlaylist()
        }
        emit(Resource.Success(data = playlistEdited, networkData = playlistEdited))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("editPlaylist()", e, this) }

    @Throws(Exception::class)
    private suspend fun editPlaylistNewApi(
        playlist: Playlist,
        songList: List<Song>
    ) = api.editPlaylist(
        authKey = getSession()?.auth!!,
        playlistId = playlist.id,
        items = when(songList.size) {
            0 -> null
            else -> songListToCommaSeparatedIds(songList)
        },
        tracks = trackPositionsCommaSeparated(songList),
        owner = playlist.owner,
        playlistType = playlist.type ?: throw Exception("addSongsToPlaylist problem with playlist type")
    ).run {
        error?.let { throw(MusicException(it.toError())) }
        if (success != null) {
            val updatedPlaylist = api.getPlaylist(getSession()?.auth!!, playlist.id)
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
        val auth = getSession()!!
        // create new playlist
        val playlist = api.createNewPlaylist(
            authKey = auth.auth,
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
        val auth = getSession()!!
        val response = api.getSongsFromPlaylist(auth.auth, albumId = playlist.id)
        response.error?.let { throw(MusicException(it.toError())) }
        val songList = response.songs!!.map { songDto -> songDto.toSong() }.toMutableList() // will throw exception if songs null
        songList.addAll(songsToAdd)

        return editPlaylistNewApi(playlist, songsToAdd)
    }
}
