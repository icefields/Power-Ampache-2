package luci.sixsixsix.powerampache2.presentation.notification

import android.app.Notification
import android.app.Service
import android.content.Context
import androidx.core.app.NotificationCompat
import luci.sixsixsix.powerampache2.R

class PluginNotificationBuilder(
    private val context: Context
) {
    fun build(service: Service): Notification {
        return NotificationCompat.Builder(context, PLUGIN_CHANNEL_ID)
            .setContentTitle("Power Ampache 2")
            .setContentText("Serving data to plugin")
            .setSmallIcon(R.drawable.ic_launcher_coloured_night)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()  // ← no setOngoing at all
    }

    companion object {
        const val PLUGIN_CHANNEL_ID = "plugin_sync_channel"
        const val PLUGIN_CHANNEL_NAME = "Plugin Sync"
    }
}
