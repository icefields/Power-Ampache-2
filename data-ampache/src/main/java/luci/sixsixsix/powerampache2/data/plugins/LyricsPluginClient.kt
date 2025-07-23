package luci.sixsixsix.powerampache2.data.plugins

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import luci.sixsixsix.mrlog.BuildConfig
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.PluginSongLyrics
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

const val PLUGIN_LYRICS_ID = "luci.sixsixsix.powerampache2.lyricsplugin"
const val PLUGIN_LYRICS_SERVICE_ID = "luci.sixsixsix.powerampache2.lyricsplugin.LyricsFetcherService"



@Singleton
class LyricsPluginClient @Inject constructor(
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
            if (isLyricsPluginInstalled()) {
                context.bindService(Intent().apply {
                    component = ComponentName(PLUGIN_LYRICS_ID, PLUGIN_LYRICS_SERVICE_ID)
                }, this, Context.BIND_AUTO_CREATE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        println("aaaa service bound")
        isBound = true
        serviceMessenger = Messenger(service)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        serviceMessenger = null
        isBound = false
    }

    suspend fun fetchLyricsPlugin(
        songTitle: String,
        albumTitle: String,
        artistName: String
    ): PluginSongLyrics? = suspendCancellableCoroutine { continuation ->
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
            // val response = JSONObject(json)
            //response.optString("lyricsUrl", "")
            println("aaaa "+json)
            val lyrics = gson.fromJson<PluginSongLyrics>(json, PluginSongLyrics::class.java)
            continuation.resume(lyrics)
            true
        }

        val replyMessenger = Messenger(incomingHandler)

        val requestJson = JSONObject().apply {
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

    fun isLyricsPluginInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(PLUGIN_LYRICS_ID, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            false
        }
    }
}
