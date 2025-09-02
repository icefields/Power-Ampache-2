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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var musicRepository: MusicRepository

    @Inject
    lateinit var errorHandler: ErrorHandler

    @Inject
    lateinit var applicationCoroutineScope: CoroutineScope


    private var pingJob: Job? = null
    private var sleepTimerJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            //val message = intent?.getStringExtra("MESSAGE")
            //L("AlarmReceiver.onReceive", intent?.action ?: "intent or action NULL")

            when(intent?.action) {
                ACTION_SLEEP_TIMER ->
                    println("aaaa SLEEP TIMER TRIGGERED ----***------***-----")
                ACTION_PING -> performPing()
            }
        } catch (e: Exception) {
            L.e(e)
        }
    }

    /**
     * Ping action will refresh the auth token.
     */
    private fun performPing() {
        pingJob?.cancel()
        pingJob = applicationCoroutineScope.launch {
            L("PING from AlarmReceiver")
            val ping = musicRepository.ping()
            //errorHandler.logError(Gson().toJson(ping))
            L(ping)
        }
    }
}
