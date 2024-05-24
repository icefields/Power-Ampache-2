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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Constants

fun MainViewModel.observeSession() {
    viewModelScope.launch {
        musicRepository.sessionLiveData.distinctUntilChanged().collect {
            synchronized(mainLock) {
                val oldToken = authToken
                authToken = it?.auth ?: ""
                logToErrorLogs(" old toke $oldToken, new one: $authToken")
                if (authToken.isNotBlank()) {
                    // refresh the playlist with new urls with the new token
                    // only if a queue exists
                    if (oldToken != authToken && playlistManager.currentQueueState.value.isNotEmpty()) {
                        if (Constants.config.queueResetOnNewSession && !isPlaying) {
                            // new session and not playing and RESET_QUEUE_ON_NEW_SESSION == true
                            logToErrorLogs("REFRESH AUTH !isPlaying")
                            playlistManager.reset()
                            resetCachedQueueState()
                            stopMusicService()
                        } else {
                            logToErrorLogs("REFRESH AUTH LOAD SONGS DATA")
                            restoreQueueState()
                        }
                    }
                } else {
                    // if sessions is null, stop service and invalidate queue and current song
                    if (currentSong() == null) {
                        logToErrorLogs(" && state.song == null")
                        if (!isPlaying) {
                            logToErrorLogs("nothing playing, tate.song == null, stop service")
                        }
                    }
                }
            }
        }
    }
}

private fun MainViewModel.restoreQueueState() {
    // TODO this is not working, there is no queue state to restore because the queue is in
    //  PlaylistManager

    logToErrorLogs("restoreQueueState")
    // restore song and queue if saved in state handle
    // the observed lived data might call loadSongData()
    //    state.song?.let { playlistManager.updateCurrentSong(it) }
    //    if (state.queue.isNotEmpty()) { playlistManager.replaceCurrentQueue(state.queue) }
    restoredSong?.let {
        logToErrorLogs("restoreQueueState.updateCurrentSong(it)")
        playlistManager.updateCurrentSong(it) }
    if (restoredQueue.isNotEmpty()) { playlistManager.replaceCurrentQueue(restoredQueue) }
    resetCachedQueueState()

}

private fun MainViewModel.resetCachedQueueState() {
    restoredSong = null
    restoredQueue = listOf()
}
