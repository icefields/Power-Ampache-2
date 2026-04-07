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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import luci.sixsixsix.mrlog.BuildConfig
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.plugins.ACTION_GET_SONGS_ALBUM
import luci.sixsixsix.powerampache2.data.plugins.ACTION_GET_SONGS_PLAYLIST
import luci.sixsixsix.powerampache2.data.plugins.ACTION_SONGS_ALBUM
import luci.sixsixsix.powerampache2.data.plugins.ACTION_SONGS_PLAYLIST
import luci.sixsixsix.powerampache2.data.plugins.KEY_ACTION
import luci.sixsixsix.powerampache2.data.plugins.KEY_ID
import luci.sixsixsix.powerampache2.data.plugins.MAX_AUTO_DATA_SIZE
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


@Singleton
class AutoPluginClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songsRepository: SongsRepository,
    private val playlistRepository: PlaylistsRepository,
    private val applicationCoroutineScope: CoroutineScope
): ServiceConnection {
    private val gson = Gson()
    private var serviceMessenger: Messenger? = null
    private var isBound = false

    init {
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
        val songs = playlistRepository.getSongsFromPlaylist(playlist)
            .filter { it is Resource.Success }
            .map { (it as Resource.Success).data }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .first()
        sendSongsToPlugin(songs, key = ACTION_SONGS_PLAYLIST, playlistId = playlistId)
    }

    private suspend fun onAlbumSongsRequestReceived(albumId: String) {
        val songs = songsRepository.getSongsFromAlbum(albumId = albumId)
            .filter { it is Resource.Success }
            .map { (it as Resource.Success).data }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .first()
        sendAlbumSongsToPlugin(songs, albumId)
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
        serviceMessenger?.send(registerMsg)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        serviceMessenger = null
        isBound = false
    }

    suspend fun sendPlaylistsToPlugin(playlists: List<Playlist>) {
        sendDataToPlugin(playlists, "playlists")
    }

    suspend fun sendFavouriteAlbumsToPlugin(albums: List<Album>) {
        sendAlbumsToPlugin(albums, "favourite_albums")
    }

    suspend fun sendLatestAlbumsToPlugin(albums: List<Album>) {
        sendAlbumsToPlugin(albums, "latest_albums")
    }

    suspend fun sendRecentAlbumsToPlugin(albums: List<Album>) {
        sendAlbumsToPlugin(albums, "recent_albums")
    }

    suspend fun sendHighestAlbumsToPlugin(albums: List<Album>) {
        sendAlbumsToPlugin(albums, "highest_albums")
    }

    suspend fun sendAlbumsToPlugin(albums: List<Album>, key: String): Boolean =
        sendDataToPlugin(albums, key)


    suspend fun sendQueueToPlugin(queue: List<Song>): Boolean =
        sendSongsToPlugin(songs = queue, key = "queue")

    suspend fun sendAlbumSongsToPlugin(songs: List<Song>, albumId: String): Boolean =
        sendSongsToPlugin(songs, key = ACTION_SONGS_ALBUM, playlistId = albumId)

    /**
     * Sends a list of songs to the plugin.
     * @param songs The list of songs to send.
     * @param key The key to use for the action.
     * @param playlistId Playlist or Album id.
     */
    suspend fun sendSongsToPlugin(songs: List<Song>, key: String, playlistId: String? = null): Boolean =
        sendDataToPlugin(removeLyricsFromSongs(songs), key, playlistId)

    /**
     * Sends a list of songs to the plugin.
     * @param dataList The list of data to send.
     * @param key The key to use for the action.
     * @param playlistId Playlist or Album id.
     */
    private suspend fun <T: AmpacheModel> sendDataToPlugin(dataList: List<T>, key: String, playlistId: String? = null): Boolean =
        suspendCancellableCoroutine { continuation ->
            if (!isBound) {
                bindIfInstalled()
                // if service not bound this will create the bind. The first lyric will be skipped
                // because since it takes time, serviceMessenger will still be null in the next check.
            }

            if (serviceMessenger == null) {
                // this usually means that the service is not initialed
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            val incomingHandler = Handler(Looper.getMainLooper()) { msg ->
                continuation.resume(msg.data.getBoolean(KEY_RESPONSE_SUCCESS))
                true
            }

            val replyMessenger = Messenger(incomingHandler)

            val msg = Message.obtain().apply {
                replyTo = replyMessenger
                data = Bundle().apply {
                    // Avoid android.os.TransactionTooLargeException: data parcel size xxx bytes by
                    // reducing the size of the queue to MAX_AUTO_DATA_SIZE.
                    putString(KEY_REQUEST_JSON, gson.toJson(
                        PluginData<T>(
                            data = if (dataList.size > MAX_AUTO_DATA_SIZE)
                                dataList.subList(0, MAX_AUTO_DATA_SIZE)
                            else dataList
                        )
                    ))
                    putString(KEY_ACTION, key)
                    if (playlistId != null) putString(KEY_ID, playlistId)
                }
            }

            serviceMessenger?.send(msg)
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
