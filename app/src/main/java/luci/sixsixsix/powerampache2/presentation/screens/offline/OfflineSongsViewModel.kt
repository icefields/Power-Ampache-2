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
package luci.sixsixsix.powerampache2.presentation.screens.offline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class OfflineSongsViewModel @Inject constructor(
    private val repository: SongsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    var state by mutableStateOf(OfflineSongsState())

    init {
        state = state.copy(isLoading = true)
        repository.offlineSongsLiveData.observeForever { songs ->
            state = state.copy(songs = songs, isLoading = false)
            // TODO check consistency of downloaded songs and database entries every time, delete data accordingly
        }
    }

    fun onEvent(event: OfflineSongsEvent) {
        when(event) {
            is OfflineSongsEvent.OnSongSelected -> {
                //if (playlistManager.currentQueueState.value.isEmpty()) {
                    playlistManager.addToCurrentQueue(state.songs)
                //}
                playlistManager.updateTopSong(event.song) }
        }
    }
}
