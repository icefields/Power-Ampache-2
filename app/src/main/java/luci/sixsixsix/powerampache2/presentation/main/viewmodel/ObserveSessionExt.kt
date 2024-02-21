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
package luci.sixsixsix.powerampache2.presentation.main.viewmodel

import androidx.lifecycle.distinctUntilChanged
import luci.sixsixsix.powerampache2.common.Constants

fun MainViewModel.observeSession() {
    // TODO use this observable instead
//    viewModelScope.launch {
//        musicRepository.sessionLiveData.map { it?.auth }.distinctUntilChanged().asFlow().filterNotNull().collectLatest {
//            logToErrorLogs("observeSession asFlow $it")
//        }
//    }


    musicRepository.sessionLiveData.distinctUntilChanged().observeForever {
        //if (lock == null) lock = Any()
        synchronized(mainLock) {
            val oldToken = authToken
            authToken = it?.auth ?: ""
            logToErrorLogs(" old toke $oldToken, new one: $authToken")
            if (authToken.isNotBlank()) {
                // refresh the playlist with new urls with the new token
                // only if a queue exists
                if (oldToken != authToken && state.queue.isNotEmpty()) {
                    // TODO EXPERIMENT remove?
//                    if (!isPlaying) {
//                        logToErrorLogs("nothing playing, new token present, stop service")
//                        stopMusicService()
//                    }
                    //authToken = newToken
                    if (Constants.RESET_QUEUE_ON_NEW_SESSION && !isPlaying) {
                        // new session and not playing and RESET_QUEUE_ON_NEW_SESSION == true
                        logToErrorLogs("REFRESH AUTH !isPlaying")
                        playlistManager.reset()
                        resetCachedQueueState()
                        stopMusicService()
                    } else {
                        logToErrorLogs("REFRESH AUTH LOAD SONGS DATA")
                        //loadSongData()
                        restoreQueueState()
                    }
                } else {
                    // TODO should restore state here? can not-restoring lead to bugs?
                    //restoreQueueState()
                }
                //authToken = newToken
            } else {
                // if sessions is null, stop service and invalidate queue and current song
                if (state.song == null) {
                    logToErrorLogs(" && state.song == null")
                    if (!isPlaying) {
                        logToErrorLogs("nothing playing, tate.song == null, stop service")
                        // TODO EXPERIMENT remove?
                        //stopMusicService()
                    }
                    //playlistManager.reset() // this will trigger the observables in observePlaylistManager() and reset mainviewmodel as well
                }
            }
        }
    }
}

private fun MainViewModel.restoreQueueState() {
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
