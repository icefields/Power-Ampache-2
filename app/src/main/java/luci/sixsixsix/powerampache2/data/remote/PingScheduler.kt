package luci.sixsixsix.powerampache2.data.remote

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_HOUR
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.utils.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PingScheduler @Inject constructor(
    private val context: Context
): AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val pendingIntent = PendingIntent.getBroadcast(
        context,
        665,
        Intent(context, AlarmReceiver::class.java).apply {
            putExtra("MESSAGE", "PING")
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    private var isScheduled = false

    override fun schedule() {
        L("schedule alarm ping")
        if (isScheduled) return

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            INTERVAL_HOUR * 2,
            pendingIntent
        )

        isScheduled = true

//        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//            System.currentTimeMillis() + 10000,
//        PendingIntent.getBroadcast(
//            context,
//            665,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        ))

    }

    override fun cancel() {
        isScheduled = false
        alarmManager.cancel(pendingIntent)
    }
}
