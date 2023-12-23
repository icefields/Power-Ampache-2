package luci.sixsixsix.powerampache2.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Constants.CLEAR_TABLE_AFTER_FETCH
import luci.sixsixsix.powerampache2.common.Constants.HOME_MAX_SONGS
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.sha256
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toAlbum
import luci.sixsixsix.powerampache2.data.local.entities.toAlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.toArtist
import luci.sixsixsix.powerampache2.data.local.entities.toArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylist
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.local.entities.toSessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.local.entities.toSongEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
import luci.sixsixsix.powerampache2.data.remote.dto.toBoolean
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toMusicAttribute
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.data.remote.dto.toServerInfo
import luci.sixsixsix.powerampache2.data.remote.dto.toSession
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import retrofit2.HttpException
import java.io.IOException
import java.lang.StringBuilder
import java.time.Instant
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
class MusicRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val dateMapper: DateMapper,
    private val db: MusicDatabase,
    private val playlistManager: MusicPlaylistManager
): MusicRepository {
    private var serverInfo: ServerInfo? = null
    private val dao = db.dao

    private suspend fun <T> handleError(label:String = "", e: Throwable, fc: FlowCollector<Resource<T>>) {
        //val sb = StringBuilder(label)
        StringBuilder(label)
            .append(if (label.isBlank())"" else " - ")
            .append( when(e) {
                    is IOException -> "cannot load data IOException $e"
                    //fc.emit(Resource.Error<T>(message = "cannot load data IOException $e", exception = e))
                    is HttpException -> "cannot load data HttpException $e"
                    //fc.emit(Resource.Error<T>(message = "cannot load data HttpException $e", exception = e))
                    is MusicException -> e.musicError.toString()
                    //fc.emit(Resource.Error<T>(message = e.musicError.toString(), exception = e))
                    else -> "generic exception $e"
                    //fc.emit(Resource.Error<T>(message = "generic exception $e", exception = e))
                }
            ).toString().apply {
                fc.emit(
                    Resource.Error<T>(
                        message = this,
                        exception = e
                    )
                )
                // TODO DEBUG this is just for debugging
                playlistManager.updateErrorMessage(this)
            }
    }

    private suspend fun getSession(): Session? {
        L("GET_SESSION ${dao.getSession()?.toSession()}")
        return dao.getSession()?.toSession()
    }

    private suspend fun setSession(se: Session) {
        dao.updateSession(se.toSessionEntity())
    }

    private suspend fun getCredentials(): CredentialsEntity? {
        return dao.getCredentials()
    }

    private suspend fun setCredentials(se: CredentialsEntity) {
        L("setCredentials $se")
        dao.updateCredentials(se)
    }

    override suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading(true))
        val resp = getSession()?.auth?.let {
            api.goodbye(it)
        }

        dao.clearCredentials()
        dao.clearSession()
        dao.clearAlbums()
        dao.clearSongs()
        dao.clearArtists()
        dao.clearPlaylists()
        L( "LOGOUT $resp")

        if (resp?.toBoolean() == true) {
            emit(Resource.Success(true))
        } else {
            throw Exception("there is an error in the logout response")
        }

        emit(Resource.Loading(false))
    }.catch { e -> handleError("logout()", e, this) }

    override suspend fun ping(): Resource<Pair<ServerInfo, Session?>> =
        try {
            val dbSession = getSession()
            val pingResponse = api.ping(dbSession?.auth ?: "")

            // Updated session only valid of previous session exists, authorize otherwise
            dbSession?.let {
                try {
                    // add credentials to the new session
                    pingResponse.toSession(dateMapper)
                } catch (e: Exception) {
                    dao.clearSession()
                    null
                }?.let { se ->
                    se.auth?.let {
                        // save new session if auth is not null
                        setSession(se)
                    }
                }
            }

            // server info always available
            serverInfo = pingResponse.toServerInfo()
            Resource.Success(Pair(serverInfo!!, getSession()))
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }

    override suspend fun autoLogin(): Flow<Resource<Session>> {
        val credentials = getCredentials()
        // authorization with empty string will fail
        return authorize(
            credentials?.username ?: "",
            credentials?.password ?: "",
            credentials?.serverUrl ?: "",
            true
        )
    }

    override suspend fun authorize(
        username: String,
        sha256password: String,
        serverUrl: String,
        force: Boolean
    ): Flow<Resource<Session>> = flow {
        emit(Resource.Loading(true))
        //   Save current credentials, so they can be picked up by the interceptor,
        // and for future autologin, this has to be first line of code before any network call
        setCredentials(CredentialsEntity(username = username, password = sha256password, serverUrl = serverUrl))
        L("authorize CREDENTIALS ${getCredentials()}")
        val auth = authorize2(username, sha256password, serverUrl, force)
        emit(Resource.Success(auth))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("authorize()", e, this) }

    @Throws(Exception::class)
    private suspend fun authorize2(
        username:String,
        sha256password:String,
        serverUrl:String,
        force: Boolean
    ): Session {
        var session = getSession()
        if (session == null || session.isTokenExpired() || force) {
            val timestamp = Instant.now().epochSecond
            val authHash = "$timestamp$sha256password".sha256()

            L("hashed password:${serverUrl} $sha256password \ntimestamp: ${timestamp}\ntimestamp+hashedPass: $timestamp${sha256password} \nauthHash: $authHash")

            val auth = api.authorize(authHash = authHash, user = username, timestamp = timestamp)
            auth.error?.let {
                throw (MusicException(it.toError()))
            }
            auth.auth?.let {
                L("NEW auth $auth")
                setSession(auth.toSession(dateMapper))
                session = getSession()
                L("auth token was null or expired ${session?.sessionExpire}, \nisTokenExpired? ${session?.isTokenExpired()}, \nnew auth ${session?.auth}")
            }
        }
        return getSession()!! // will throw exception if session null
    }

    @Throws(Exception::class)
    private suspend fun getHomeScreenSongs(auth: Session): List<Song> {
        val flagged = api.getSongsFlagged(authKey = auth.auth).songs!!.map { it.toSong() }
        val recent = api.getSongsRecent(authKey = auth.auth).songs!!.map { it.toSong() }
        val highest = api.getSongsHighest(authKey = auth.auth).songs!!.map { it.toSong() }
        val frequent = api.getSongsFrequent(authKey = auth.auth).songs!!.map { it.toSong() }
        val newest = api.getSongsNewest(authKey = auth.auth).songs!!.map { it.toSong() }
        val random = api.getSongsRandom(authKey = auth.auth).songs!!.map { it.toSong() }

        return LinkedHashSet<Song>().apply {
            addAll(recent)
            addAll(newest)
            addAll(highest)
            addAll(flagged)
            addAll(frequent)
            addAll(random)
        }.toList()
    }


    /**
     *
     * TODO BREAKING_RULE single source of truth
     *
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

        // network
        val auth = getSession()!!//authorize2(false)
        val hashSet = LinkedHashSet<Song>()
        val songs = if (query.isNullOrBlank()) {
            api.getSongsNewest(authKey = auth.auth).songs?.let { dto ->
                val newest = dto.map { it.toSong() }
                hashSet.addAll(newest)
                if (newest.isNotEmpty()) {
                    emit(Resource.Success(data = hashSet.toList()))
                }
            }

            api.getSongsRecent(authKey = auth.auth).songs?.let { dto ->
                val recent = dto.map { it.toSong() }
                hashSet.addAll(recent)
                if (recent.isNotEmpty()) {
                    emit(Resource.Success(data = hashSet.toList()))
                }
            }

            api.getSongsHighest(authKey = auth.auth).songs?.let { dto ->
                val highest = dto.map { it.toSong() }
                hashSet.addAll(highest)
                if (highest.isNotEmpty()) {
                    emit(Resource.Success(data = hashSet.toList()))
                }
            }

            api.getSongsFrequent(authKey = auth.auth).songs?.let { dto ->
                val frequent = dto.map { it.toSong() }
                hashSet.addAll(frequent)
                if (frequent.isNotEmpty()) {
                    emit(Resource.Success(data = hashSet.toList()))
                }
            }

            api.getSongsFlagged(authKey = auth.auth).songs?.let { flaggedDto ->
                val flagged = flaggedDto.map { it.toSong() }
                hashSet.addAll(flagged)
                if (flagged.isNotEmpty()) {
                    emit(Resource.Success(data = hashSet.toList()))
                }
            }

            val randomL = api.getSongsRandom(
                authKey = auth.auth,
                limit = HOME_MAX_SONGS - hashSet.size
            ).songs!!.map { it.toSong() }
            hashSet.addAll(randomL)
            if (randomL.isNotEmpty()) {
                emit(Resource.Success(data = hashSet.toList()))
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
        if (query.isNullOrBlank() && offset == 0 && CLEAR_TABLE_AFTER_FETCH) {
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

        if (query.isNullOrBlank() && offset == 0 && CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearAlbums()
        }
        dao.insertAlbums(albums.map { it.toAlbumEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchAlbum(query).map { it.toAlbum() }, networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getAlbums()", e, this) }

    override suspend fun getArtists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
            val localArtists = dao.searchArtist(query)
            val isDbEmpty = localArtists.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localArtists.map { it.toArtist() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!! //authorize2(false)
        val response = api.getArtists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val artists = response.artists!!.map { it.toArtist() }
        //emit(Resource.Success(artists)) // will throw exception if songs null

        if (query.isNullOrBlank() && offset == 0 && CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearArtists()
        }
        dao.insertArtists(artists.map { it.toArtistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchArtist(query).map { it.toArtist() }, networkData = artists))

        emit(Resource.Loading(false))
    }.catch { e -> handleError("getArtists()", e, this) }

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

        val auth = getSession()!!//authorize2(false)
        val response = api.getPlaylists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val playlists = response.playlist!!.map { it.toPlaylist() } // will throw exception if playlist null

        if (query.isNullOrBlank() && offset == 0 && CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearPlaylists()
        }
        dao.insertPlaylists(playlists.map { it.toPlaylistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchPlaylists(query).map { it.toPlaylist() }, networkData = playlists))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getPlaylists()", e, this) }

    private suspend fun getAlbumsFromArtistsDb(artistId: String): List<Album> {
        val localAlbums = dao.searchAlbum("").map { it.toAlbum() }
        val list = LinkedHashSet<Album>()
        for (album in localAlbums) {
            if(album.artist.id == artistId) {
                list.add(album)
            }
            for (artist in album.artists) {
                if(artist.id == artistId) {
                    list.add(album)
                }
            }
        }

        return ArrayList(list).sortedWith(comparator = object : Comparator<Album> {
            override fun compare(o1: Album?, o2: Album?): Int {
                return o1?.let {
                    o2?.year?.compareTo(it.year)
                } ?: run {
                    0
                }
            }
        })
    }

    override suspend fun getAlbumsFromArtist(
        artistId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        L("repo getAlbumsFromArtist $artistId")

        val localAlbums = getAlbumsFromArtistsDb(artistId)
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
        emit(Resource.Success(data = getAlbumsFromArtistsDb(artistId), networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getAlbumsFromArtist()", e, this) }

    private suspend fun getSongsFromAlbumDb(albumId: String): List<Song> {
        val localSongs = dao.searchSong("").map { it.toSong() }
        val list = LinkedHashSet<Song>()
        for (song in localSongs) {
            if(song.album.id == albumId) {
                list.add(song)
            }
        }

        return ArrayList(list).sortedWith(comparator = object : Comparator<Song> {
            override fun compare(o1: Song?, o2: Song?): Int {
                return o2?.let {
                    o1?.trackNumber?.compareTo(it.trackNumber)
                } ?: run {
                    //tracks with no track number go last
                    Int.MAX_VALUE
                }
            }
        })
    }

    /**
     * TODO BREAKING_RULE: inconsistent data in the response, must use network response
     *
     * USE Network response for this, database not reliable for unknown reason
     * use the cache to just preload some data
     */
    override suspend fun getSongsFromAlbum(
        albumId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        L("repo getSongsFromAlbum $albumId")

        val localSongs = getSongsFromAlbumDb(albumId)
        val isDbEmpty = localSongs.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localSongs))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        val auth = getSession()!!//authorize2(false)
        val response = api.getSongsFromAlbum(auth.auth, albumId = albumId)
        response.error?.let { throw(MusicException(it.toError())) }
        val songs = response.songs!!.map { songDto -> songDto.toSong() } // will throw exception if songs null

        L("songs from web ${songs.size}")

        dao.insertSongs(songs.map { it.toSongEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = getSongsFromAlbumDb(albumId), networkData = songs))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getSongsFromAlbum()", e, this) }


    /**
     * TODO BREAKING_RULE: Implement cache for playlist songs
     * This method only fetches from the network, breaking one of the rules defined in the
     * documentation of this class.
     */
    override suspend fun getSongsFromPlaylist(
        playlistId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        L("repo getSongsFromPlaylist $playlistId")

        val auth = getSession()!!//authorize2(false)
        val response = api.getSongsFromPlaylist(auth.auth, albumId = playlistId)
        response.error?.let { throw(MusicException(it.toError())) }
        val songs = response.songs!!.map { songDto -> songDto.toSong() } // will throw exception if songs null

        emit(Resource.Success(data = songs, networkData = songs))
        emit(Resource.Loading(false))
    }.catch { e -> handleError("getSongsFromPlaylist()", e, this) }
}
