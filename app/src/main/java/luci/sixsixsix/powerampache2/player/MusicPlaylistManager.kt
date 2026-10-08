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
import luci.sixsixsix.powerampache2.presentation.models.PlayableUI
import luci.sixsixsix.powerampache2.presentation.models.distinctByKey
import luci.sixsixsix.powerampache2.presentation.models.reduceList
import luci.sixsixsix.powerampache2.presentation.models.SongUI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlaylistManager @Inject constructor() {
    private val _currentItemState = MutableStateFlow<PlayableUI?>(null)
    val currentItemState: StateFlow<PlayableUI?> = _currentItemState

    private val _currentSearchQuery = MutableStateFlow("")
    val currentSearchQuery: StateFlow<String> = _currentSearchQuery

    private val _currentQueueState = MutableStateFlow(listOf<PlayableUI>())
    val currentQueueState: StateFlow<List<PlayableUI>> = _currentQueueState

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

    private fun List<PlayableUI>.indexOfKey(item: PlayableUI?): Int =
        item?.let { indexOfFirst { queueItem -> queueItem.key == it.key } } ?: -1

    /**
     * assign the new song state, remove the song from the queue if exists and re-add it after the
     * one that is currently playing. Add a list of song to the queue state,if no song is currently
     * set as state, automatically set the first song of the queue
     */
    fun addToCurrentQueueUpdateTopSong(newSong: PlayableUI, newQueue: List<PlayableUI>) {
        // add the current song on top of the queue
        val updatedQueue = _currentQueueState.value.filterNot { it.key == newSong.key }.toMutableList()
        // add song next to the one that is currently playing, at the top if nothing is playing
        updatedQueue.add(updatedQueue.indexOfKey(_currentItemState.value) + 1, newSong)
        _currentQueueState.value = (updatedQueue + newQueue.reduceList()).distinctByKey()
        _currentItemState.value = newSong

        checkCurrentSong()
    }

    /**
     * used in the callback when music player goes to the next song in the playlist
     */
    fun updateCurrentSong(newSong: PlayableUI?) {
        L( "MusicPlaylistManager updateCurrentSong", newSong)
        _currentItemState.value = newSong
    }

    fun replaceCurrentQueue(newQueue: List<PlayableUI>) {
        L( "MusicPlaylistManager replaceCurrentQueue", newQueue.size)
        _currentQueueState.value = newQueue.reduceList()
        checkCurrentSong()
    }

    fun replaceQueuePlaySong(newQueue: List<PlayableUI>, songToPlay: PlayableUI) {
        _currentQueueState.value = newQueue.reduceList()
        _currentItemState.value = songToPlay
    }

    /**
     * add a list of song to the queue state
     * if no song is currently set as state, automatically set the first song of the queue
     */
    fun addToCurrentQueue(newQueue: List<PlayableUI>) {
        L( "MusicPlaylistManager addToCurrentQueue", newQueue.size)
        _currentQueueState.value = (_currentQueueState.value + newQueue)
            .distinctByKey()
            .reduceList()
        checkCurrentSong()
    }

    /**
     * adds the song to the current queue if the song is not null
     */
    fun addToCurrentQueue(newSong: PlayableUI?) = newSong?.let {
        L( "MusicPlaylistManager addToCurrentQueue", newSong)
        addToCurrentQueue(listOf(newSong))
    }

    /**
     * removes a list of songs from the current queue
     */
    fun removeFromCurrentQueue(songsToRemove: List<PlayableUI>) {
        val keysToRemove = songsToRemove.map { it.key }.toSet()
        _currentQueueState.value = _currentQueueState.value
            .distinctByKey()
            .filterNot { it.key in keysToRemove }
        // if the queue is empty after this operation also remove the current song
        if (_currentQueueState.value.isEmpty()) {
            _currentItemState.value = null
        }
        checkCurrentSong()
    }

    /**
     * remove a single song from queue
     */
    fun removeFromCurrentQueue(songToRemove: PlayableUI) =
        removeFromCurrentQueue(listOf(songToRemove))

    /**
     * add items to the current queue as next in queue
     */
    fun addToCurrentQueueNext(list: List<PlayableUI>) {
        L( "MusicPlaylistManager addToCurrentQueueNext", list.size)
        // remove all songs except the current
        val currentKey = _currentItemState.value?.key
        val listWithoutCurrentSong = list.filterNot { it.key == currentKey }
        val keysToMove = listWithoutCurrentSong.map { it.key }.toSet()
        val queue = _currentQueueState.value.filterNot { it.key in keysToMove }.toMutableList()
        // find current index, new songs will be added after that
        val currentSongIndex = queue.indexOfKey(_currentItemState.value)
        queue.addAll(if (queue.size > currentSongIndex + 1) { currentSongIndex + 1 } else { queue.size }, listWithoutCurrentSong)
        replaceCurrentQueue(queue)
    }

    fun addToCurrentQueueTop(list: List<PlayableUI>) {
        L( "MusicPlaylistManager addToCurrentQueueTop", list.size)
        replaceCurrentQueue(list + currentQueueState.value)
    }

    /**
     * if no song is currently set as state, automatically set the first song of the queue
     */
    private fun checkCurrentSong() {
        if (currentQueueState.value.isNotEmpty() && currentItemState.value == null) {
            updateTopSong(currentQueueState.value[0])
        }
    }

    /**
     * assign the new song state, remove the song from the queue if exists and re-add it on top
     */
    fun updateTopSong(newSong: PlayableUI) {
        L("MusicPlaylistManager updateTopSong", newSong)
        _currentItemState.value = newSong
        // add the current song on top of the queue
        _currentQueueState.value = listOf(newSong) + _currentQueueState.value.filterNot { it.key == newSong.key }
    }

    fun addToCurrentQueueNext(song: PlayableUI?) = song?.let {
        L( "MusicPlaylistManager addToCurrentQueueNext", song)
        addToCurrentQueueNext(listOf(song))
    }

    fun startRestartQueue() {
        _currentItemState.value = currentQueueState.value[0]
    }

    /**
     * remove all songs except the currently playing one if any
     */
    fun clearQueue(isPlaying: Boolean) = if (!isPlaying) {
        replaceCurrentQueue(listOf())
        _currentItemState.value = null
    } else {
        replaceCurrentQueue(listOfNotNull(currentItemState.value))
    }

    fun reset() {
        _currentItemState.value = null
        updateSearchQuery(searchQuery= "")
        replaceCurrentQueue(listOf())
    }
}
