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
package luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.data.remote.worker.SongDownloadWorker

internal fun MainViewModel.observeDownloads(application: Context) {
    WorkManager.getInstance(application).pruneWork()
    viewModelScope.launch {
        WorkManager.getInstance(application)
            .getWorkInfosForUniqueWorkLiveData(SongDownloadWorker.getDownloadWorkerId(application))
            //.getWorkInfosForUniqueWorkFlow(SongDownloadWorker.workerName)
            //.getWorkInfoByIdFlow(requestId).mapNotNull { it.outputData.getString(KEY_RESULT_PATH) }.cancellable()
            .observeForever { workInfoList ->
                L("observeDownloads.observeForever")
                var atLeastOneRunning = false
                var atLeastOneEnqueued = false
                var atLeastOneBlocked = false
                var allCancelled = true // assume all cancelled, reset if if after the loop is still true
                var allFailed = true // assume all failed, reset if if after the loop is still true
                var allFailedOrSucceededOrCancelled = true
                workInfoList.forEach { workInfo ->
                    L(workInfo.state.name)
                    if (!atLeastOneRunning && workInfo.state == WorkInfo.State.RUNNING) {
                        atLeastOneRunning = true
                    }
                    if (!atLeastOneEnqueued && workInfo.state == WorkInfo.State.ENQUEUED) {
                        atLeastOneEnqueued = true
                    }
                    if (!atLeastOneBlocked && workInfo.state == WorkInfo.State.BLOCKED) {
                        atLeastOneBlocked = true
                    }
                    if (allCancelled && workInfo.state != WorkInfo.State.CANCELLED) {
                        allCancelled = false
                    }
                    if (allFailed && workInfo.state != WorkInfo.State.FAILED) {
                        allFailed = false
                    }
                    if (allFailedOrSucceededOrCancelled &&
                        (workInfo.state != WorkInfo.State.FAILED && workInfo.state != WorkInfo.State.SUCCEEDED && workInfo.state != WorkInfo.State.CANCELLED  )
                        ) {
                        allFailedOrSucceededOrCancelled = false
                    }

                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        workInfo?.outputData?.getString(SongDownloadWorker.KEY_RESULT_SONG)?.let { songId ->
                            viewModelScope.launch {
                                if (!emittedDownloads.contains(songId)) {
                                    emittedDownloads = emittedDownloads.toMutableList().apply { add(songId) }
                                    songsRepository.getDownloadedSongById(songId)?.let { finishedSong ->
                                        playlistManager.updateDownloadedSong(finishedSong)
                                        playlistManager.updateUserMessage(
                                            application.getString(R.string.downloaded_snackbar_title, finishedSong.name)
                                        ) //"${finishedSong.name} downloaded")
                                        state = state.copy(isDownloading = false)
                                        //WorkManager.getInstance(application).pruneWork()
                                        L("emitting", finishedSong.name)
                                    }
                                }
                            }
                        }
                    }
//                        it?.progress?.getInt(SongDownloadWorker.KEY_PROGRESS, ERROR_INT)?.let { progress ->
//                            if (progress != ERROR_INT) {
//                                state = state.copy(isDownloading = progress in 0..99)
//                                L(progress)
//                            }
//                        }
                }
                state = state.copy(isDownloading = atLeastOneRunning || atLeastOneEnqueued || atLeastOneBlocked)

                if(workInfoList.isNotEmpty() &&
                    !atLeastOneRunning && !atLeastOneBlocked && !atLeastOneEnqueued && (allCancelled || allFailed || allFailedOrSucceededOrCancelled)
                    ) {
                    // no more work to be done
                    viewModelScope.launch {
                        L("resetDownloadWorkerId(application) ${SongDownloadWorker.getDownloadWorkerId(application)}")
                        SongDownloadWorker.resetDownloadWorkerId(application)
                        delay(200)
                        L("resetDownloadWorkerId(application) AFTER REFRESH ${SongDownloadWorker.getDownloadWorkerId(application)}")

                        observeDownloads(application)
                    }
                }
            }
    }
}
