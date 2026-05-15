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
package luci.sixsixsix.powerampache2.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util.startForegroundService
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.SERVICE_STOP_TIMEOUT
import luci.sixsixsix.powerampache2.domain.SleepTimerEventBus
import luci.sixsixsix.powerampache2.domain.common.WeakContext
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.usecase.settings.SleepTimerEndTimestampFlow
import luci.sixsixsix.powerampache2.domain.usecase.settings.SleepTimerResetUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.SleepTimerWaitSongEnd
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class MusicController @Inject constructor(
    @ApplicationContext private val context: Context,
    val weakContext: WeakContext,
    private val sleepTimerEventBus: SleepTimerEventBus,
    private val sleepTimerWaitSongEnd: SleepTimerWaitSongEnd,
    private val sleepTimerEndTimestampFlow: SleepTimerEndTimestampFlow,
    private val sleepTimerResetUseCase: SleepTimerResetUseCase,
    private val applicationCoroutineScope: CoroutineScope,
    val playlistManager: MusicPlaylistManager,
    private val errorHandler: ErrorHandler
) {
    private val serviceIntent = Intent(context, SimpleMediaService::class.java)
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var startMusicServiceCalled = false // ensure called only once
    private var stopJob: Job? = null

    init {
        if (SimpleMediaService.isRunning) {
            // initialize the controllers if returning from killed state and service running
            initController(context)
        }

        // There are 2 ways to stop the music using the timer, if the setting"sleepTimerWaitSongEnd"
        // is not enabled, stop the music right away, to do so, listen to the alarm event sent
        // through sleepTimerEventBus.sleepTimerEvents.
        // If the setting"sleepTimerWaitSongEnd" is enabled, ignore the callback from the alarm
        // manager and instead listen to the songs state, and every song change check if the timer
        // threshold has passed. If so, stop the music.
        // pauseAndResetOnTimer() is called in both cases. It will reset the setting sleepTimerEndTimestamp,
        // when this variable is set to 0, the alarm manager will cancel the alarm.
        // See PingScheduler.init for details.

        // Listen to sleep timer events
        applicationCoroutineScope.launch {
            sleepTimerEventBus.sleepTimerEvents.collect {
                withContext(Dispatchers.Main) {
                    if (!sleepTimerWaitSongEnd())
                        pauseAndResetOnTimer()
                }
            }
        }

        // Callback triggered every time a new song is being played
        applicationCoroutineScope.launch {
            playlistManager.currentSongState.filterNotNull().collectLatest { newSong ->
                val sleepTimerEndTimestamp = sleepTimerEndTimestampFlow().value
                if (
                    sleepTimerWaitSongEnd()
                    && sleepTimerEndTimestamp > 0
                    && sleepTimerEndTimestamp <= System.currentTimeMillis()
                    ) {
                    pauseAndResetOnTimer()
                }
            }
        }
    }

    private fun pause() = runOnMain { controller?.pause() }

    private suspend fun pauseAndResetOnTimer() = try {
        sleepTimerResetUseCase()
        pause()
        //resetStopMusic()
    } catch (e: Exception) {
        resetStopMusic()
        errorHandler.logError(e)
    }

    @OptIn(UnstableApi::class)
    fun startMusicServiceIfNecessary() {
        if(!SimpleMediaService.isRunning && !startMusicServiceCalled) {
            println("SERVICE- startMusicServiceIfNecessary")
            weakContext.get()?.applicationContext?.let { applicationContext ->
                startForegroundService(applicationContext, serviceIntent)
                initController(applicationContext)
                startMusicServiceCalled = true
            }
        }
    }

    fun initController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, SimpleMediaService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                try {
                    controller = controllerFuture?.get()
                } catch (e: Exception) {
                    L.e(e, "Failed to get MediaController")
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    /**
     * IMPORTANT: run this on Main Thread
     */
    fun releaseController() {
        controller?.release()
        controller = null
        controllerFuture?.cancel(true)
        controllerFuture = null
    }

    @OptIn(UnstableApi::class)
    fun stopService() {
        L("SERVICE- stopMusicService isRunning: ${SimpleMediaService.isRunning}")

        if (!SimpleMediaService.isRunning) return
        releaseController()

        weakContext.get()?.applicationContext?.let { applicationContext ->
            try {
                L("SERVICE- stopMusicService")
                applicationContext.stopService(serviceIntent)
                startMusicServiceCalled = false
            } catch (e: Exception) {
                startMusicServiceCalled = false
                L.e(e, "SERVICE-")
            }
        }
    }
    fun stopMusicService(addDelay: Boolean = true) {
        stopJob?.cancel()
        if (addDelay) {
            stopJob = applicationCoroutineScope.launch {
                delay(SERVICE_STOP_TIMEOUT) // safety net, delay stopping the service in case the application just got restored from background
                withContext(Dispatchers.Main) {
                    stopService()
                }
            }
        } else {
            runOnMain { stopService() }
        }
    }

    fun resetStopMusic() {
        try {
            playlistManager.reset()
            errorHandler.resetMessages()
            stopMusicService()
        } catch (e: Exception) {
            L.e(e)
        }
    }

    private fun runOnMain(block: () -> Unit) {
        Handler(Looper.getMainLooper()).post { block() }
    }
}
