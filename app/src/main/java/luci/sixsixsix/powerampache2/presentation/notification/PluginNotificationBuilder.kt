/**
 * Copyright (C) 2026  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import luci.sixsixsix.powerampache2.R

class PluginNotificationBuilder(
    private val context: Context
) {
    fun build(): Notification {
        return NotificationCompat.Builder(context, PLUGIN_CHANNEL_ID)
            .setContentTitle("Power Ampache 2")
            .setContentText("Serving data to plugin")
            .setSmallIcon(R.drawable.ic_launcher_coloured_night)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    companion object {
        const val PLUGIN_CHANNEL_ID = "plugin_sync_channel"
        const val PLUGIN_CHANNEL_NAME = "Plugin Sync"
    }
}
