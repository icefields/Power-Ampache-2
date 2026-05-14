package luci.sixsixsix.powerampache2.presentation.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import luci.sixsixsix.powerampache2.presentation.notification.PluginNotificationBuilder.Companion.PLUGIN_CHANNEL_ID
import luci.sixsixsix.powerampache2.presentation.notification.PluginNotificationBuilder.Companion.PLUGIN_CHANNEL_NAME

class PluginSyncService : Service() {

    private val notificationBuilder by lazy { PluginNotificationBuilder(this) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = notificationBuilder.build(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Start serving data to the plugin
        // (kick off your IPC mechanism here — ContentProvider, Messenger, etc.)

        return START_STICKY
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            PLUGIN_CHANNEL_ID,
            PLUGIN_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = "Keeps the service alive for plugin data sync"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_SECRET
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }






    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val NOTIFICATION_ID = 1001

        fun startService(context: Context) {
            val intent = Intent(context, PluginSyncService::class.java)
            context.startForegroundService(intent)
        }
    }
}
