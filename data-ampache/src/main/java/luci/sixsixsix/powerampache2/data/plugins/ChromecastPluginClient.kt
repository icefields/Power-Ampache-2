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
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_CHROMECAST_ID
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_CHROMECAST_SERVICE_ID
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.plugin.chromecast.PluginChromecastQueue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ChromecastPluginClient @Inject constructor(
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
            if (isChromecastPluginInstalled()) {
                context.bindService(Intent().apply {
                    component = ComponentName(PLUGIN_CHROMECAST_ID, PLUGIN_CHROMECAST_SERVICE_ID)
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

    suspend fun sendQueueToPlugin(queue: List<Song>): Boolean = suspendCancellableCoroutine { continuation ->
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
                // reducing the size of the queue to MAX_CAST_QUEUE.
                val queueNoLyrics = removeLyricsFromQueue(queue)
                putString(KEY_REQUEST_JSON, gson.toJson(
                    PluginChromecastQueue(
                        if (queueNoLyrics.size > MAX_CAST_QUEUE) queueNoLyrics.subList(0,MAX_CAST_QUEUE) else queueNoLyrics
                    )
                ))
            }
        }

        serviceMessenger?.send(msg)
    }

    /**
     * Will remove the lyrics from the songs in the cast queue, to reduce the amount of data sent
     * to the plugin.
     */
    private fun removeLyricsFromQueue(queue: List<Song>) = queue.map { it.copy(lyrics = "") }

    fun isChromecastPluginInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(PLUGIN_CHROMECAST_ID, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            if (BuildConfig.DEBUG) { L("Chromecast Plugin not installed, caught ${e.localizedMessage}") }
            false
        }
    }
}
