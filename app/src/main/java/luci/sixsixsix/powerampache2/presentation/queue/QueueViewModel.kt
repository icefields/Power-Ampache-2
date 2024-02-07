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
package luci.sixsixsix.powerampache2.presentation.queue

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class QueueViewModel  @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    //var queueState by savedStateHandle.saveable { mutableStateOf(listOf<Song>()) }
    var queueState by mutableStateOf(listOf<Song>())

    init {
        viewModelScope.launch {
            playlistManager.currentQueueState.collect { q ->
                val queue = q.filterNotNull()
                queueState = queue
            }
        }
    }

    fun onEvent(event: QueueEvent) {
        when(event) {
            is QueueEvent.OnSongSelected ->
                playlistManager.moveToSongInQueue(event.song)
            QueueEvent.OnPlayQueue ->
                playlistManager.startRestartQueue()
            QueueEvent.OnClearQueue ->
                playlistManager.clearQueue()
            is QueueEvent.OnSongRemove ->
                playlistManager.removeFromCurrentQueue(event.song)
        }
    }
}
