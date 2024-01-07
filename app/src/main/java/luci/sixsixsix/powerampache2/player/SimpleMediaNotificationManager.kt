package luci.sixsixsix.powerampache2.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import luci.sixsixsix.powerampache2.R
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
        createNotificationChannel()
    }

    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildNotification(mediaSession)
        startForegroundNotification(mediaSessionService)
    }

    private fun buildNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(SimpleMediaNotificationAdapter(context, mediaSession.sessionActivity))
            .setSmallIconResourceId(R.drawable.ic_playlist)
            .build()
            .apply {
                setMediaSessionToken(mediaSession.sessionCompatToken)
                setUseFastForwardActionInCompactView(true)
                setUseRewindActionInCompactView(true)
                setUseNextActionInCompactView(true)
                setPriority(NotificationCompat.PRIORITY_LOW)
                setPlayer(player)
            }
    }

    private fun startForegroundNotification(mediaSessionService: MediaSessionService) {
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        } else {
            TODO("VERSION SDK < O")
        }
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() =
        notificationManager.createNotificationChannel(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
            } else {
                TODO("VERSION SDK < O")
            }
        )
}