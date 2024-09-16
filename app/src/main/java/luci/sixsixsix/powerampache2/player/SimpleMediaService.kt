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
package luci.sixsixsix.powerampache2.player

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import luci.sixsixsix.mrlog.L
import javax.inject.Inject

@UnstableApi @AndroidEntryPoint
class SimpleMediaService: MediaSessionService() {
    @Inject
    lateinit var mediaSession: MediaSession

    @Inject
    lateinit var notificationManager: SimpleMediaNotificationManager

    override fun onCreate() {
        super.onCreate()
        L("SERVICE- onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        L("SERVICE- onStartCommand")
        notificationManager.startNotificationService(
            mediaSessionService = this,
            mediaSession = mediaSession
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        L("SERVICE- DESTROY")
        mediaSession.run {
            if (player.playbackState != Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
            release()
        }
        notificationManager.stopNotificationService(this)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

// ===========================
// === BINDING THE SERVICE ===

    private val binder = MediaServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        L("SERVICE- onUnbind")
        return super.onUnbind(intent)
    }

    // Binder class to interact with the service
    inner class MediaServiceBinder : Binder() {
        fun getService(): SimpleMediaService = this@SimpleMediaService
    }
}
