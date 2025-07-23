package luci.sixsixsix.powerampache2.data.plugins

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import luci.sixsixsix.mrlog.BuildConfig
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginAlbumData
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginArtistData
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginSongData
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

const val PLUGIN_INFO_ID = "luci.sixsixsix.powerampache2.infoplugin"
const val PLUGIN_INFO_SERVICE_ID = "${PLUGIN_INFO_ID}.InfoFetcherService"

@Singleton
class InfoPluginClient @Inject constructor(
    @ApplicationContext private val context: Context
): ServiceConnection {
    private val gson = Gson()
    private var serviceMessenger: Messenger? = null
    private var isBound = false

    init {
        bindIfInstalled()
    }

    private fun bindIfInstalled() {
        try {
            if (isInfoPluginInstalled()) {
                context.bindService(Intent().apply {
                    component = ComponentName(PLUGIN_INFO_ID, PLUGIN_INFO_SERVICE_ID)
                }, this, Context.BIND_AUTO_CREATE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        println("aaaa InfoPluginClient service bound")
        isBound = true
        serviceMessenger = Messenger(service)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        serviceMessenger = null
        isBound = false
    }

    suspend fun fetchArtistInfoPlugin(
        artistId: String,
        mbId: String,
        artistName: String
    ): PluginArtistData? = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            bindIfInstalled()
            // if service not bound this will create the bind. The first lyric will be skipped
            // because since it takes time, serviceMessenger will still be null in the next check.
        }

        if (serviceMessenger == null) {
            // this usually means that the service is not initialed
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val incomingHandler = Handler(Looper.getMainLooper()) { msg ->
            val json = msg.data.getString(KEY_REQUEST_JSON) ?: return@Handler true
            println("aaaa artist "+json)
            val artistData = gson.fromJson<PluginArtistData>(json, PluginArtistData::class.java)
            continuation.resume(artistData)
            true
        }

        val replyMessenger = Messenger(incomingHandler)

        val requestJson = JSONObject().apply {
            put(KEY_ACTION, ACTION_ARTIST)
            put(KEY_MBID, mbId)
            put(KEY_ID, artistId)
            put(KEY_REQUEST_ARTIST_NAME, artistName)
        }

        val msg = Message.obtain().apply {
            replyTo = replyMessenger
            data = Bundle().apply {
                putString(KEY_REQUEST_JSON, requestJson.toString())
            }
        }

        serviceMessenger?.send(msg)
    }

    suspend fun fetchAlbumInfoPlugin(
        albumId: String,
        mbId: String,
        albumTitle: String,
        artistName: String
    ): PluginAlbumData? = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            bindIfInstalled()
            // if service not bound this will create the bind. The first lyric will be skipped
            // because since it takes time, serviceMessenger will still be null in the next check.
        }

        if (serviceMessenger == null) {
            // this usually means that the service is not initialed
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val incomingHandler = Handler(Looper.getMainLooper()) { msg ->
            val json = msg.data.getString(KEY_REQUEST_JSON) ?: return@Handler true
            println("aaaa "+json)
            val albumData = gson.fromJson<PluginAlbumData>(json, PluginAlbumData::class.java)
            continuation.resume(albumData)
            true
        }

        val replyMessenger = Messenger(incomingHandler)

        val requestJson = JSONObject().apply {
            put(KEY_ACTION, ACTION_ALBUM)
            put(KEY_MBID, mbId)
            put(KEY_ID, albumId)
            put(KEY_REQUEST_ALBUM_TITLE, albumTitle)
            put(KEY_REQUEST_ARTIST_NAME, artistName)
        }

        val msg = Message.obtain().apply {
            replyTo = replyMessenger
            data = Bundle().apply {
                putString(KEY_REQUEST_JSON, requestJson.toString())
            }
        }

        serviceMessenger?.send(msg)
    }

    suspend fun fetchSongInfoPlugin(
        songId: String,
        mbId: String,
        songTitle: String,
        albumTitle: String,
        artistName: String
    ): PluginSongData? = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            bindIfInstalled()
            // if service not bound this will create the bind. The first lyric will be skipped
            // because since it takes time, serviceMessenger will still be null in the next check.
        }

        if (serviceMessenger == null) {
            // this usually means that the service is not initialed
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val incomingHandler = Handler(Looper.getMainLooper()) { msg ->
            val json = msg.data.getString(KEY_REQUEST_JSON) ?: return@Handler true
            println("aaaa "+json)
            val songData = gson.fromJson<PluginSongData>(json, PluginSongData::class.java)
            continuation.resume(songData)
            true
        }

        val replyMessenger = Messenger(incomingHandler)

        val requestJson = JSONObject().apply {
            put(KEY_ACTION, ACTION_SONG)
            put(KEY_MBID, mbId)
            put(KEY_ID, songId)
            put(KEY_REQUEST_SONG_TITLE, songTitle)
            put(KEY_REQUEST_ALBUM_TITLE, albumTitle)
            put(KEY_REQUEST_ARTIST_NAME, artistName)
        }

        val msg = Message.obtain().apply {
            replyTo = replyMessenger
            data = Bundle().apply {
                putString(KEY_REQUEST_JSON, requestJson.toString())
            }
        }

        serviceMessenger?.send(msg)
    }

    fun isInfoPluginInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(PLUGIN_INFO_ID, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            false
        }
    }
}
