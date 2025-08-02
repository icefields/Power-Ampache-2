/**
 * Copyright (C) 2025  Antonio Tari
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
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_LYRICS_ID
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_LYRICS_SERVICE_ID
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.PluginSongLyrics
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

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
