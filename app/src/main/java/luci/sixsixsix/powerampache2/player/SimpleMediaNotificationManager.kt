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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.MainActivity
import javax.inject.Inject

private const val NOTIFICATION_ID = 666
private const val NOTIFICATION_CHANNEL_NAME = "powerAmp.channel.666"
private const val NOTIFICATION_CHANNEL_ID = "powerAmp.id.666"

@UnstableApi
class SimpleMediaNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val player: ExoPlayer
) {
    private var notificationManager = NotificationManagerCompat.from(context)

    init {
        L("SERVICE- SimpleMediaNotificationManager init")
        createNotificationChannel()
    }

    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        L("SERVICE- SimpleMediaNotificationManager startNotificationService")
        buildNotification(mediaSession)
        startForegroundNotification(mediaSessionService)
    }

    private fun buildNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(SimpleMediaNotificationAdapter(context, mediaSession.sessionActivity))
            .setSmallIconResourceId(R.drawable.ic_power_ampache_mono)
            .build()
            .apply {
                //setMediaSessionToken(mediaSession.sessionCompatToken)
                setMediaSessionToken(mediaSession.platformToken)
                setUseFastForwardActionInCompactView(true)
                setUseRewindActionInCompactView(true)
                setUseNextActionInCompactView(true)
                setColorized(true)
                setPriority(NotificationCompat.PRIORITY_LOW)
                setPlayer(player)
            }
    }

    private fun startForegroundNotification(mediaSessionService: MediaSessionService) {
        val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(
                PendingIntent.getActivity(
                    context.applicationContext,
                    3214,
                    Intent(context.applicationContext, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            ).build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    fun stopNotificationService(mediaSessionService: MediaSessionService) {
        mediaSessionService.stopForeground(MediaSessionService.STOP_FOREGROUND_REMOVE)
        notificationManager.cancel(NOTIFICATION_ID)
        notificationManager.cancelAll()
    }

    private fun createNotificationChannel() = notificationManager.createNotificationChannel(
        NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
    )
}