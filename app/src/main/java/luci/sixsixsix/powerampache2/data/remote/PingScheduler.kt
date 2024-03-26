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
    context: Context
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
