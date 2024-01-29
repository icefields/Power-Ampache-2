package luci.sixsixsix.powerampache2.data

import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylist
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Session
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
        val response = api.getPlaylists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val playlists = response.playlist!!.map { it.toPlaylist() } // will throw exception if playlist null

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
        playlistType: MainNetwork.PlaylistType
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
        items.joinToString(separator = ", ") { it.mediaId }

    private fun trackPositionsCommaSeparated(items: List<Song>) =
        ArrayList<Int>().apply {
            for(i in 1..items.size) {
                add(i)
            }
        }.joinToString(separator = ", ")

    override suspend fun editPlaylist(
        playlistId: String,
        playlistName: String?,
        items: List<Song>,
        owner: String?,
        tracks: String?,
        playlistType: MainNetwork.PlaylistType
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


    /**
     * TODO waiting for backend fix to use this function, THIS IS a temporary hack
     */
    override suspend fun addSongsToPlaylist(
        playlist: Playlist,
        songs: List<Song>
    ) = flow {
        emit(Resource.Loading(true))

        // Get old version of the playlist
        val auth = getSession()!!
        val response = api.getSongsFromPlaylist(auth.auth, albumId = playlist.id)
        response.error?.let { throw(MusicException(it.toError())) }
        val songList = response.songs!!.map { songDto -> songDto.toSong() }.toMutableList() // will throw exception if songs null
        val songsToAdd = ArrayList(songs).apply {
            removeAll(songList.toSet())
        }

        for (song in songsToAdd) {
            addSingleSongToPlaylist(playlistId = playlist.id, songId = song.mediaId)
        }

        emit(Resource.Success(data = Any(), networkData = Any()))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("editPlaylist()", e, this) }

    override suspend fun createNewPlaylistAddSongs(
        name: String,
        playlistType: MainNetwork.PlaylistType,
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

        for (song in songsToAdd) {
            addSingleSongToPlaylist(playlistId = playlist.id, songId = song.mediaId)
        }

        emit(Resource.Success(data = playlist, networkData = playlist))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("editPlaylist()", e, this) }

    /**
     * TODO waiting for backend fix to use this function, using a temporary hack in the meantime
     */
    suspend fun createNewPlaylistAddSongsNOTWorking(
        name: String,
        playlistType: MainNetwork.PlaylistType,
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

        // generate comma separated list of ids
        val commaSeparatedIds = when(songsToAdd.size) {
            0 -> null
            else -> songListToCommaSeparatedIds(songsToAdd)
        }
        api.editPlaylist(
            authKey = auth.auth,
            playlistId = playlist.id,
            items = commaSeparatedIds,
            owner = playlist.owner,
            playlistType = MainNetwork.PlaylistType.valueOf(
                playlist.type ?:
                throw Exception("addSongsToPlaylist problem with playlist type")
            )
        ).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
                emit(Resource.Success(playlist))
            } else {
                throw Exception("error getting a response from editPlaylist call")
            }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("editPlaylist()", e, this) }

    /**
     * TODO waiting for backend fix to use this function, using a temporary hack in the meantime
     */
    suspend fun addSongsToPlaylistNotWorking(
        playlist: Playlist,
        songsToAdd: List<Song>
    ) = flow {
        emit(Resource.Loading(true))

        // Get old version of the playlist
        val auth = getSession()!!
        val response = api.getSongsFromPlaylist(auth.auth, albumId = playlist.id)
        response.error?.let { throw(MusicException(it.toError())) }
        val songList = response.songs!!.map { songDto -> songDto.toSong() }.toMutableList() // will throw exception if songs null
        songList.addAll(songsToAdd)

        // generate comma separated list of ids
        val commaSeparatedIds = when(songList.size) {
            0 -> null
            else -> songListToCommaSeparatedIds(songList)
        }
        L(commaSeparatedIds)
        L(trackPositionsCommaSeparated(songList))

        api.editPlaylist(
            authKey = auth.auth,
            playlistId = playlist.id,
            items = commaSeparatedIds,
            tracks = trackPositionsCommaSeparated(songList),
            owner = playlist.owner,
            playlistType = MainNetwork.PlaylistType.valueOf(
                playlist.type ?:
                throw Exception("addSongsToPlaylist problem with playlist type")
            )
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
}
