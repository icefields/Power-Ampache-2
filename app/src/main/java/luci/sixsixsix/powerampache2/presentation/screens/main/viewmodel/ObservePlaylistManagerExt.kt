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

import androidx.annotation.OptIn
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R

@OptIn(UnstableApi::class)
fun MainViewModel.observePlaylistManager() {

    // listen to current-song changes
    viewModelScope.launch {
        playlistManager.currentSongState.collectLatest {
            it?.let { songState ->
                startMusicServiceIfNecessary()
                scrobble(songState)
                downloadAfterPlayback(songState)
            } ?: stopMusicService()
        }
    }

    viewModelScope.launch {
        playlistManager.logMessageUserReadableState.collectLatest { logMessageState ->
            logMessageState.logMessage?.let { logMessage ->
                // do not show errors in offline mode unless in cases specified above
                val isOfflineModeEnabled = isOfflineModeEnabledUseCase()
                if (!isOfflineModeEnabled) {
                    state = state.copy(errorMessage = logMessage)
                } else if (logMessage == weakContext.get()?.resources?.getString(R.string.logout_offline_warning)) {
                    L(logMessage)

                    state = state.copy(errorMessage = logMessage)
                } else if (state.errorMessage.isNotBlank()) {
                    state = state.copy(errorMessage = "")
                }
                L(logMessage)
            }
        }
    }

    // listen to queue changes
    viewModelScope.launch {
        playlistManager.currentQueueState.collectLatest { q ->
            val queue = q.filterNotNull()
            if (!queue.isNullOrEmpty()) {
                startMusicServiceIfNecessary()
            } else if (queue.isNullOrEmpty() && currentSong() == null) {
                stopMusicService()
            }
            L("**** observing playlist change queue (before Load song data) :", queue.size)
            loadSongData()
//            if (isChromecastPluginInstalled()) {
//                sendQueueToChromecastUseCase(queue)
//            }
        }
    }
}
