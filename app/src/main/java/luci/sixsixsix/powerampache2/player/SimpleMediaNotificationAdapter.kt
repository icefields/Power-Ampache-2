package luci.sixsixsix.powerampache2.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest

@UnstableApi
class SimpleMediaNotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?
): PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player) = player.mediaMetadata.title ?: ""
    override fun createCurrentContentIntent(player: Player) = pendingIntent
    override fun getCurrentContentText(player: Player) = player.mediaMetadata.albumTitle ?: ""
    override fun getCurrentSubText(player: Player) = player.mediaMetadata.artist ?: ""

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        val loader = ImageLoader(context)
        val req = ImageRequest.Builder(context)
            .data(player.mediaMetadata.artworkUri)
            .target { result ->
                callback.onBitmap ((result as BitmapDrawable).bitmap)
            }
            .build()

        //val disposable =
            loader.enqueue(req)
        return null
    }
}
