package luci.sixsixsix.powerampache2.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.session.MediaController
import androidx.annotation.OptIn

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.NOTIFICATION_CHANNEL_ID
import luci.sixsixsix.powerampache2.common.Constants.NOTIFICATION_ID

@OptIn(UnstableApi::class)
class MusicNotificationManager(
    private val context: Context,
    sessionToken: android.media.session.MediaSession.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaController(context, sessionToken)

        notificationManager = PlayerNotificationManager
            .Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .setNotificationListener(notificationListener)
            .build()
            .apply {
                setSmallIcon(R.drawable.ic_music)
                // TODO setMediaSessionToken(sessionToken)
            //setMediaSessionToken(MediaSession.Token.fromToken(sessionToken))
            }

//        val mediaController = MediaControllerCompat(context, sessionToken)
//        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
//            context,
//            NOTIFICATION_CHANNEL_ID,
//            R.string.notification_channel_name,
//            R.string.notification_channel_description,
//            NOTIFICATION_ID,
//            DescriptionAdapter(mediaController),
//            notificationListener
//        ).apply {
//            setSmallIcon(R.drawable.ic_music)
//            setMediaSessionToken(sessionToken)
//        }
    }

    fun showNotification(player: Player) = notificationManager.setPlayer(player)

    private inner class DescriptionAdapter(
        private val mediaController: MediaController
    ) : PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): CharSequence {
            newSongCallback()
            return mediaController.metadata?.description?.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? = mediaController.sessionActivity

        override fun getCurrentContentText(player: Player): CharSequence? = mediaController.metadata?.description?.subtitle.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
//            Glide.with(context).asBitmap()
//                .load(mediaController.metadata?.description?.iconUri)
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: Transition<in Bitmap>?
//                    ) {
//                        callback.onBitmap(resource)
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) = Unit
//                })
            return null
        }
    }
}
