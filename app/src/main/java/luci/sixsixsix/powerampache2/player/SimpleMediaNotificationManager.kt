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

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider.DEFAULT_CHANNEL_ID
import androidx.media3.session.DefaultMediaNotificationProvider.DEFAULT_NOTIFICATION_ID
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.MainActivity
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

// Values from DefaultMediaNotificationProvider TODO: custom notification channels will cause double notification to show
@SuppressLint("UnsafeOptInUsageError")
private const val NOTIFICATION_ID = DEFAULT_NOTIFICATION_ID // 666
@SuppressLint("UnsafeOptInUsageError")
private const val NOTIFICATION_CHANNEL_ID = DEFAULT_CHANNEL_ID //"default_channel_id" "powerAmp.channel.666"

const val NOTIFICATION_INTENT_REQUEST_CODE = 3214
private const val NOTIFICATION_CHANNEL_NAME = "powerAmp.channel.666"

@Singleton
@UnstableApi
class SimpleMediaNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playerManager: PlayerManager
) {
    private var notificationManager = NotificationManagerCompat.from(context)
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var isForegroundService = false

    init {
        createNotificationChannel()
    }

    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildNotification(mediaSession, mediaSessionService)
        //startForegroundNotification(mediaSessionService)
    }

    private fun buildNotification(mediaSession: MediaSession, mediaSessionService: MediaSessionService) {
        val weakMediaService = WeakReference<MediaSessionService>(mediaSessionService)
        playerNotificationManager = PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            //.setMediaDescriptionAdapter(SimpleMediaNotificationAdapter(context, mediaSession.sessionActivity))
            .setSmallIconResourceId(R.drawable.ic_notification_exo_play) //TODO restore when solve the notification issue(R.drawable.ic_power_ampache_mono)
            .setMediaDescriptionAdapter(SimpleMediaNotificationAdapter(context, notificationPendingIntent(context)))
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing && !isForegroundService) {
                        mediaSessionService.startForeground(notificationId, notification)
                        isForegroundService = true
                    }
                }

                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
//                    mediaSessionService.stopForeground(MediaSessionService.STOP_FOREGROUND_DETACH)
                    weakMediaService.get()?.stopForeground(MediaSessionService.STOP_FOREGROUND_REMOVE)
                    weakMediaService.get()?.stopSelf()
                    isForegroundService = false
                }
            })
            .build()
            .apply {
                //setMediaSessionToken(mediaSession.sessionCompatToken)
                setMediaSessionToken(mediaSession.platformToken)
                setUseFastForwardActionInCompactView(true)
                setUseRewindActionInCompactView(true)
                setUseNextActionInCompactView(true)
                setColorized(true)
                setPriority(NotificationCompat.PRIORITY_LOW)
                setPlayer(playerManager.player)
            }
    }


    /**
     * this is a placeholder. Media Service requires to call startforeground before 5 seconds
     * A placeholder while the actual notification appears is used in this case
     */
    @Deprecated("not using this anymore")
    private fun startForegroundNotification(mediaSessionService: MediaSessionService) {
    /*
        val placeholder = NotificationCompat.Builder(mediaSessionService, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_power_ampache_mono)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(notificationPendingIntent(context))
            .setOngoing(true)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .build()
    */

        val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(notificationPendingIntent(context))
            .build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    fun stopNotificationService(mediaSessionService: MediaSessionService) {
        isForegroundService = false
        playerNotificationManager?.setPlayer(null) // Disconnect player
        playerNotificationManager = null
        mediaSessionService.stopForeground(MediaSessionService.STOP_FOREGROUND_REMOVE)
        notificationManager.cancel(NOTIFICATION_ID)
        notificationManager.cancelAll()
        mediaSessionService.stopSelf()
    }

    private fun createNotificationChannel() = notificationManager.createNotificationChannel(
        NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
    )

    companion object {
        fun notificationPendingIntent(context: Context): PendingIntent = PendingIntent.getActivity(
            context.applicationContext,
            NOTIFICATION_INTENT_REQUEST_CODE,
            Intent(context.applicationContext, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                ),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
