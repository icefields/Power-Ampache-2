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
package luci.sixsixsix.powerampache2.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.presentation.models.reduceList
import luci.sixsixsix.powerampache2.presentation.models.SongUI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlaylistManager @Inject constructor() {
    private val _currentSongState = MutableStateFlow<SongUI?>(null)
    val currentSongState: StateFlow<SongUI?> = _currentSongState //val currentSong = _currentSong.asStateFlow()

    private val _currentSearchQuery = MutableStateFlow("")
    val currentSearchQuery: StateFlow<String> = _currentSearchQuery

    private val _currentQueueState = MutableStateFlow(listOf<SongUI>())
    val currentQueueState: StateFlow<List<SongUI>> = _currentQueueState

    private val _downloadedSongFlow = MutableStateFlow<SongUI?>(null)
    // TODO: is this needed?
    val downloadedSongFlow: StateFlow<SongUI?> = _downloadedSongFlow

    fun updateDownloadedSong(song: SongUI?) {
        _downloadedSongFlow.value = song
    }

    fun updateSearchQuery(searchQuery: String) {
        L( "MusicPlaylistManager updateSearchQuery $searchQuery")
        _currentSearchQuery.value = searchQuery
    }

    /**
     * assign the new song state, remove the song from the queue if exists and re-add it after the
     * one that is currently playing. Add a list of song to the queue state,if no song is currently
     * set as state, automatically set the first song of the queue
     */
    fun addToCurrentQueueUpdateTopSong(newSong: SongUI, newQueue: List<SongUI>) {
        // add the current song on top of the queue
        val updatedQueue = ArrayList(_currentQueueState.value).apply {
            remove(newSong)
            // add song next to the one that is currently playing
            try {
                add(indexOf(_currentSongState.value) + 1, newSong)
            } catch (e: Exception) {
                add(0, newSong)
            }
        }
        _currentQueueState.value = LinkedHashSet(updatedQueue).apply { addAll(newQueue.reduceList()) }.toList()
        _currentSongState.value = newSong

        checkCurrentSong()
    }

    /**
     * used in the callback when music player goes to the next song in the playlist
     */
    fun updateCurrentSong(newSong: SongUI?) {
        L( "MusicPlaylistManager updateCurrentSong", newSong)
        _currentSongState.value = newSong
    }

    /**
     * same as updateCurrentSong but also provides current queue
     * TODO unused function
     */
    //fun moveToSongInQueue(newSong: SongUI?, queue: List<SongUI>) = newSong?.let {
    //    L( "MusicPlaylistManager moveToSongInQueue", newSong)
    //    _currentSongState.value = newSong
    //}

    fun replaceCurrentQueue(newQueue: List<SongUI>) {
        L( "MusicPlaylistManager replaceCurrentQueue", newQueue.size)
        _currentQueueState.value = newQueue.reduceList()
        checkCurrentSong()
    }

    fun replaceQueuePlaySong(newQueue: List<SongUI>, songToPlay: SongUI) {
        _currentQueueState.value = newQueue.reduceList()
        _currentSongState.value = songToPlay
    }

    /**
     * add a list of song to the queue state
     * if no song is currently set as state, automatically set the first song of the queue
     */
    fun addToCurrentQueue(newQueue: List<SongUI>) {
        L( "MusicPlaylistManager addToCurrentQueue", newQueue.size)
        _currentQueueState.value = LinkedHashSet(_currentQueueState.value)
            .apply { addAll(newQueue) }
            .toList()
            .reduceList()
        checkCurrentSong()
    }

    /**
     * adds the song to the current queue if the song is not null
     */
    fun addToCurrentQueue(newSong: SongUI?) = newSong?.let {
        L( "MusicPlaylistManager addToCurrentQueue", newSong)
        addToCurrentQueue(listOf(newSong))
    }

    /**
     * removes a list of songs from the current queue
     */
    fun removeFromCurrentQueue(songsToRemove: List<SongUI>) {
        _currentQueueState.value = LinkedHashSet(_currentQueueState.value)
            .apply { removeAll(songsToRemove.toSet()) }
            .toList()
        // if the queue is empty after this operation also remove the current song
        if (_currentQueueState.value.isEmpty()) {
            _currentSongState.value = null
        }
        checkCurrentSong()
    }

    /**
     * remove a single song from queue
     */
    fun removeFromCurrentQueue(songToRemove: SongUI) =
        removeFromCurrentQueue(listOf(songToRemove))

    /**
     * add items to the current queue as next in queue
     */
    fun addToCurrentQueueNext(list: List<SongUI>) {
        L( "MusicPlaylistManager addToCurrentQueueNext", list.size)
        val queue = ArrayList(_currentQueueState.value)
            .apply {
                // remove all songs except the current
                val listWithoutCurrentSong = ArrayList(list)
                    .apply { remove(currentSongState.value) }
                removeAll(listWithoutCurrentSong.toSet())
                // find current index, new songs will be added after that
                val currentSongIndex = indexOf(currentSongState.value)
                addAll( if (size > currentSongIndex+1) { currentSongIndex+1 } else { size } , listWithoutCurrentSong)
            }

//        val queue = ArrayList<Song>(currentQueueState.value)
//            .apply {
//                val currentSongIndex = indexOf(currentSongState.value.song)
//                addAll( if (size > currentSongIndex+1) { currentSongIndex+1 } else { size } , list)
//            }
        replaceCurrentQueue(queue)
    }

    fun addToCurrentQueueTop(list: List<SongUI>) {
        L( "MusicPlaylistManager addToCurrentQueueTop", list.size)
        val queue = ArrayList<SongUI>(currentQueueState.value).apply {
            addAll(0, list)
        }
        replaceCurrentQueue(queue)
    }

    /**
     * if no song is currently set as state, automatically set the first song of the queue
     */
    private fun checkCurrentSong() {
        if (currentQueueState.value.isNotEmpty() && currentSongState.value == null) {
            updateTopSong(currentQueueState.value[0])
        }
    }

    /**
     * assign the new song state, remove the song from the queue if exists and re-add it on top
     */
    fun updateTopSong(newSong: SongUI) {
        L("MusicPlaylistManager updateTopSong", newSong)
        _currentSongState.value = newSong
        // add the current song on top of the queue
        _currentQueueState.value = ArrayList(_currentQueueState.value).apply {
            remove(newSong)
            add(0, newSong)
        }
    }

    fun addToCurrentQueueNext(song: SongUI?) = song?.let {
        L( "MusicPlaylistManager addToCurrentQueueNext", song)
        addToCurrentQueueNext(listOf(song))
    }

    fun startRestartQueue() {
        _currentSongState.value = currentQueueState.value[0]
    }

    /**
     * remove all songs except the currently playing one if any
     */
    fun clearQueue(isPlaying: Boolean) = if (!isPlaying) {
        replaceCurrentQueue(listOf())
        _currentSongState.value = null
    } else {
        replaceCurrentQueue(listOfNotNull(currentSongState.value))
    }

    fun reset() {
        _currentSongState.value = null
        updateSearchQuery(searchQuery= "")
        replaceCurrentQueue(listOf())
    }
}
