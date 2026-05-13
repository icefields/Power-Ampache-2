package luci.sixsixsix.powerampache2.data.plugins.auto

import luci.sixsixsix.powerampache2.data.plugins.KEY_REQUEST_JSON
import luci.sixsixsix.powerampache2.data.plugins.KEY_RESPONSE_SUCCESS

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import luci.sixsixsix.mrlog.BuildConfig
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.plugins.ACTION_ALBUMS
import luci.sixsixsix.powerampache2.data.plugins.ACTION_ARTISTS
import luci.sixsixsix.powerampache2.data.plugins.ACTION_GET_ALBUMS
import luci.sixsixsix.powerampache2.data.plugins.ACTION_GET_ALBUMS_ARTIST
import luci.sixsixsix.powerampache2.data.plugins.ACTION_GET_ARTISTS
import luci.sixsixsix.powerampache2.data.plugins.ACTION_GET_SONGS_ALBUM
import luci.sixsixsix.powerampache2.data.plugins.ACTION_GET_SONGS_PLAYLIST
import luci.sixsixsix.powerampache2.data.plugins.ACTION_SONGS_ALBUM
import luci.sixsixsix.powerampache2.data.plugins.ACTION_SONGS_PLAYLIST
import luci.sixsixsix.powerampache2.data.plugins.KEY_ACTION
import luci.sixsixsix.powerampache2.data.plugins.KEY_ID
import luci.sixsixsix.powerampache2.data.plugins.KEY_QUERY
import luci.sixsixsix.powerampache2.data.plugins.MAX_AUTO_DATA_SIZE
import luci.sixsixsix.powerampache2.data.plugins.THROTTLE_TIMEOUT_MS
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_AUTO_ID
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_AUTO_SERVICE_ID
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Playlist

import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.plugin.models.PluginData
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.sequences.chunked
import kotlin.sequences.forEach


@Singleton
class AutoPluginClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songsRepository: SongsRepository,
    private val playlistRepository: PlaylistsRepository,
    private val artistsRepository: ArtistsRepository,
    private val albumsRepository: AlbumsRepository,
    private val applicationCoroutineScope: CoroutineScope
): ServiceConnection {
    private val gson = Gson()
    private var serviceMessenger: Messenger? = null
    private var isBound = false
    private val sendMutex = Mutex()

    init {
        initialize()
    }

    fun initialize() {
        bindIfInstalled()

        if (isAutoPluginInstalled()) {
            applicationCoroutineScope.launch {
                playlistRepository.playlistsFlow
                    .filterNotNull()
                    .filter { it.isNotEmpty() }
                    .distinctUntilChanged()
                    .collect { playlists ->
                        sendPlaylistsToPlugin(playlists)
                    }
            }

            applicationCoroutineScope.launch {
                albumsRepository.flaggedAlbumsFlow
                    .filterNotNull()
                    .filter { it.isNotEmpty() }
                    .distinctUntilChanged()
                    .collect { albums ->
                        sendFavouriteAlbumsToPlugin(albums)
                    }
            }

            applicationCoroutineScope.launch {
                albumsRepository.highestRatedAlbumsFlow
                    .filterNotNull()
                    .filter { it.isNotEmpty() }
                    .distinctUntilChanged()
                    .collect { albums ->
                        sendHighestAlbumsToPlugin(albums)
                    }
            }

            applicationCoroutineScope.launch {
                albumsRepository.recentlyPlayedAlbumsFlow
                    .filterNotNull()
                    .filter { it.isNotEmpty() }
                    .distinctUntilChanged()
                    .collect { albums ->
                        sendRecentAlbumsToPlugin(albums)
                    }
            }

            applicationCoroutineScope.launch {
                albumsRepository.randomAlbumsFlow
                    .filterNotNull()
                    .filter { it.isNotEmpty() }
                    .distinctUntilChanged()
                    .collect { albums ->
                        sendLatestAlbumsToPlugin(albums)
                    }
            }
        }
    }

    private suspend fun <T: AmpacheModel> sendChunkedDataToPlugin(
        dataList: List<T>,
        key: String,
        playlistId: String? = null
    ) {
        dataList.asSequence()
            .chunked(MAX_AUTO_DATA_SIZE)
            .forEach { chunk ->
                sendDataToPlugin(chunk, key, playlistId)
            }
    }

    private val clientMessenger = Messenger(object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val action = msg.data.getString(KEY_ACTION) ?: return
            //println("aaaa handleMessage $action")
            when (action) {
                ACTION_GET_SONGS_ALBUM -> {
                    val albumId = msg.data.getString(KEY_ID) ?: return
                    applicationCoroutineScope.launch {
                        onAlbumSongsRequestReceived(albumId)
                    }
                }
                ACTION_GET_SONGS_PLAYLIST -> {
                    val playlistId = msg.data.getString(KEY_ID) ?: return
                    applicationCoroutineScope.launch {
                        onPlaylistSongsRequestReceived(playlistId)
                    }
                }
                ACTION_GET_ARTISTS -> {
                    val query = msg.data.getString(KEY_QUERY) ?: ""
                    applicationCoroutineScope.launch {
                        onArtistsRequestReceived(query)
                    }
                }
                ACTION_GET_ALBUMS -> {
                    val query = msg.data.getString(KEY_QUERY) ?: ""
                    applicationCoroutineScope.launch {
                        onAlbumsRequestReceived(query)
                    }
                }
                ACTION_GET_ALBUMS_ARTIST -> {
                    val artistId = msg.data.getString(KEY_ID) ?: return
                    applicationCoroutineScope.launch {
                        onArtistAlbumsRequestReceived(artistId)
                    }
                }
            }
        }
    })

    private fun bindIfInstalled() {
        try {
            if (isAutoPluginInstalled()) {
                context.bindService(Intent().apply {
                    component = ComponentName(PLUGIN_AUTO_ID, PLUGIN_AUTO_SERVICE_ID)
                }, this, Context.BIND_AUTO_CREATE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun onPlaylistSongsRequestReceived(playlistId: String) {
        val playlist = playlistRepository.getPlaylistFlow(playlistId).filterNotNull().first()
        playlistRepository.getSongsFromPlaylist(playlist)
            .filter { it is Resource.Success }
            .map { (it as Resource.Success).networkData }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .collect { songs ->
                sendSongsToPlugin(songs, key = ACTION_SONGS_PLAYLIST, playlistId = playlistId)
            }
    }

    private suspend fun onAlbumsRequestReceived(query: String = "") {
        albumsRepository.getAlbums(fetchRemote = true, query = query)
            .filter { it is Resource.Success }
            .map { (it as Resource.Success).data }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .collect { albums ->
                sendChunkedDataToPlugin(albums, ACTION_ALBUMS, query)
            }
        println("aaaaa onAlbumsRequestReceived END $query")
    }

    private suspend fun onArtistAlbumsRequestReceived(artistId: String) {
        albumsRepository.getAlbumsFromArtist(fetchRemote = true, artistId = artistId)
            .filter { it is Resource.Success }
            .map { (it as Resource.Success).data }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .collect { albums ->
                sendChunkedDataToPlugin(albums, ACTION_ALBUMS, artistId)
            }
        println("aaaaa onArtistAlbumsRequestReceived END $artistId")
    }

    private suspend fun onArtistsRequestReceived(query: String = "") {
        artistsRepository.getArtists(fetchRemote = true, query = query)
            .filter { it is Resource.Success }
            .map { (it as Resource.Success).data }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .collect { artists ->
                artists.asSequence()
                    .chunked(MAX_AUTO_DATA_SIZE)
                    .forEach { chunk ->
                        sendDataToPlugin(chunk, key = ACTION_ARTISTS, playlistId = query)
                    }
            }
        println("aaaaa onArtistRequestReceived END $query")
    }

    private suspend fun onAlbumSongsRequestReceived(albumId: String) {
        songsRepository.getSongsFromAlbum(albumId = albumId)
            .filter { it is Resource.Success }
            .map { (it as Resource.Success).networkData }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .collect { songs ->
                sendAlbumSongsToPlugin(songs, albumId)
            }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        isBound = true
        serviceMessenger = Messenger(service)

        // Send registration so service knows our persistent messenger
        val registerMsg = Message.obtain().apply {
            data = Bundle().apply {
                putString(KEY_ACTION, "register_client")
            }
            replyTo = clientMessenger
        }
        try {
            serviceMessenger?.send(registerMsg)
        } catch (e: RemoteException) {
            L.e(e.stackTraceToString())
            e.printStackTrace()
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        serviceMessenger = null
        isBound = false
    }

    suspend fun sendPlaylistsToPlugin(playlists: List<Playlist>) {
        sendChunkedDataToPlugin(playlists, "playlists")
    }

    suspend fun sendFavouriteAlbumsToPlugin(albums: List<Album>) {
        sendChunkedDataToPlugin(albums, "favourite_albums")
    }

    suspend fun sendLatestAlbumsToPlugin(albums: List<Album>) {
        sendChunkedDataToPlugin(albums, "latest_albums")
    }

    suspend fun sendRecentAlbumsToPlugin(albums: List<Album>) {
        sendChunkedDataToPlugin(albums, "recent_albums")
    }

    suspend fun sendHighestAlbumsToPlugin(albums: List<Album>) {
        sendChunkedDataToPlugin(albums, "highest_albums")
    }

    suspend fun sendQueueToPlugin(queue: List<Song>) =
        sendSongsToPlugin(songs = queue, key = "queue")

    suspend fun sendAlbumSongsToPlugin(songs: List<Song>, albumId: String) =
        sendSongsToPlugin(songs, key = ACTION_SONGS_ALBUM, playlistId = albumId)

    /**
     * Sends a list of songs to the plugin.
     * @param songs The list of songs to send.
     * @param key The key to use for the action.
     * @param playlistId Playlist or Album id.
     */
    suspend fun sendSongsToPlugin(songs: List<Song>, key: String, playlistId: String? = null) =
        sendChunkedDataToPlugin(removeLyricsFromSongs(songs), key, playlistId)

    /**
     * Sends a list of songs to the plugin.
     * @param dataList The list of data to send.
     * @param key The key to use for the action.
     * @param playlistId Playlist or Album id.
     */
    private suspend fun <T: AmpacheModel> sendDataToPlugin(
        dataList: List<T>,
        key: String,
        playlistId: String? = null
    ): Boolean = sendMutex.withLock {
        delay(THROTTLE_TIMEOUT_MS)

        withTimeoutOrNull(30000) {
            suspendCancellableCoroutine { continuation ->
                if (!isBound) {
                    bindIfInstalled()
                    // if service not bound this will create the bind. The first lyric will be skipped
                    // because since it takes time, serviceMessenger will still be null in the next check.
                }

                val messenger = serviceMessenger
                if (messenger == null) {
                    // this usually means that the service is not initialed
                    continuation.resume(false)
                    return@suspendCancellableCoroutine
                }

                val incomingHandler = Handler(Looper.getMainLooper()) { msg ->
                    if (continuation.isActive) { // guard against double resume
                        continuation.resume(msg.data.getBoolean(KEY_RESPONSE_SUCCESS))
                    }
                    true
                }

                val replyMessenger = Messenger(incomingHandler)

                val msg = Message.obtain().apply {
                    replyTo = replyMessenger
                    data = Bundle().apply {
                        // Avoid android.os.TransactionTooLargeException: data parcel size xxx bytes by
                        // reducing the size of the queue to MAX_AUTO_DATA_SIZE.
                        putString(
                            KEY_REQUEST_JSON,
                            gson.toJson(PluginData<T>(
                                data = if (dataList.size > MAX_AUTO_DATA_SIZE)
                                    dataList.subList(0, MAX_AUTO_DATA_SIZE)
                                else dataList)
                            )
                        )
                        putString(KEY_ACTION, key)
                        if (playlistId != null) putString(KEY_ID, playlistId)
                    }
                }

                try {
                    messenger.send(msg)
                } catch (e: RemoteException) {
                    if (continuation.isActive) {
                        continuation.resume(false)
                    }
                }
                //messenger?.send(msg)

                // Ensure handler doesn't fire after cancellation
                continuation.invokeOnCancellation {
                    incomingHandler.removeCallbacksAndMessages(null)
                }
            }
        } ?: run {
            L.e("aaaa sendDataToPlugin timeout")
            false
        }
    }

    /**
     * Will remove the lyrics from the songs in the queue, to reduce the amount of data sent
     * to the plugin.
     */
    private fun removeLyricsFromSongs(queue: List<Song>) = queue.map { it.copy(lyrics = "") }

    fun isAutoPluginInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(PLUGIN_AUTO_ID, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            if (BuildConfig.DEBUG) { L("Auto Plugin not installed, caught ${e.localizedMessage}") }
            false
        }
    }
}
