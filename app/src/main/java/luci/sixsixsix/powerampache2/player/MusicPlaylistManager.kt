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
import luci.sixsixsix.powerampache2.domain.models.Song
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class ErrorLogMessageState(var errorMessage: String? = null)
data class LogMessageState(
    var logMessage: String? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val count: Int? = null
)

@Singleton
class MusicPlaylistManager @Inject constructor() {
    private val _currentSongState = MutableStateFlow<Song?>(null)
    val currentSongState: StateFlow<Song?> = _currentSongState //val currentSong = _currentSong.asStateFlow()

    private val _logMessageUserReadableState = MutableStateFlow(LogMessageState())
    val logMessageUserReadableState: StateFlow<LogMessageState> = _logMessageUserReadableState
    var notificationsListStateFlow: MutableStateFlow<List<LogMessageState>> = MutableStateFlow(listOf())
        private set

    private val _errorLogMessageState = MutableStateFlow(ErrorLogMessageState())
    val errorLogMessageState: StateFlow<ErrorLogMessageState> = _errorLogMessageState

    private val _currentSearchQuery = MutableStateFlow("")
    val currentSearchQuery: StateFlow<String> = _currentSearchQuery

    private val _currentQueueState = MutableStateFlow(listOf<Song>())
    val currentQueueState: StateFlow<List<Song>> = _currentQueueState

    private val _downloadedSongFlow = MutableStateFlow<Song?>(null)
    val downloadedSongFlow: StateFlow<Song?> = _downloadedSongFlow

    fun updateUserMessage(logMessage: String?) {
        L("MusicPlaylistManager updateUserMessage", logMessage)
        _logMessageUserReadableState.value = LogMessageState(logMessage = logMessage)

        // add to the list of notifications
        logMessage?.let { lm ->
            if (lm.isNotBlank()) {
                // if already there remove it
                val messages = ArrayList<LogMessageState>(notificationsListStateFlow.value)
                // remove if already present
                var count = 0
                messages.map { it.logMessage }.indexOf(lm).apply {
                    if (this > -1) {
                        count = messages[this].count ?: 0
                        messages.removeAt(this)
                    }
                }

                notificationsListStateFlow.value = messages.apply {
                        add(0, LogMessageState(logMessage = lm, count = ++count))
                    }
            }
        }

        // also log for debug reasons
        updateErrorLogMessage(logMessage)
    }

    /**
     * updates the error log in settings
     */
    fun updateErrorLogMessage(logMessage: String?) {
        L("MusicPlaylistManager updateErrorLogMessage", logMessage)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        _errorLogMessageState.value = ErrorLogMessageState(errorMessage = "$current\n$logMessage")
    }

    fun updateDownloadedSong(song: Song?) {
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
    fun addToCurrentQueueUpdateTopSong(newSong: Song, newQueue: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueueUpdateTopSong", newQueue.size)
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
        _currentQueueState.value = LinkedHashSet(updatedQueue).apply { addAll(newQueue) }.toList()
        _currentSongState.value = newSong

        checkCurrentSong()
    }

    /**
     * used in the callback when music player goes to the next song in the playlist
     */
    fun updateCurrentSong(newSong: Song?) {
        L( "MusicPlaylistManager updateCurrentSong", newSong)
        _currentSongState.value = newSong
    }

    /**
     * same as updateCurrentSong but also provides current queue
     * TODO unused function
     */
    fun moveToSongInQueue(newSong: Song?, queue: List<Song>) = newSong?.let {
        L( "MusicPlaylistManager moveToSongInQueue", newSong)
        _currentSongState.value = newSong
    }

    fun replaceCurrentQueue(newQueue: List<Song>) {
        L( "MusicPlaylistManager replaceCurrentQueue", newQueue.size)
        _currentQueueState.value = newQueue.filterNotNull()
        checkCurrentSong()
    }

    fun replaceQueuePlaySong(newQueue: List<Song>, songToPlay: Song) {
        _currentQueueState.value = newQueue.filterNotNull()
        _currentSongState.value = songToPlay
    }

    /**
     * add a list of song to the queue state
     * if no song is currently set as state, automatically set the first song of the queue
     */
    fun addToCurrentQueue(newQueue: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueue", newQueue.size)
        _currentQueueState.value = LinkedHashSet(_currentQueueState.value)
            .apply { addAll(newQueue) }
            .toList()
        checkCurrentSong()
    }

    /**
     * adds the song to the current queue if the song is not null
     */
    fun addToCurrentQueue(newSong: Song?) = newSong?.let {
        L( "MusicPlaylistManager addToCurrentQueue", newSong)
        addToCurrentQueue(listOf(newSong))
    }

    /**
     * removes a list of songs from the current queue
     */
    fun removeFromCurrentQueue(songsToRemove: List<Song>) {
        _currentQueueState.value = LinkedHashSet(_currentQueueState.value)
            .apply { removeAll(songsToRemove.filterNotNull().toSet()) }
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
    fun removeFromCurrentQueue(songToRemove: Song) =
        removeFromCurrentQueue(listOf(songToRemove))

    /**
     * add items to the current queue as next in queue
     */
    fun addToCurrentQueueNext(list: List<Song>) {
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

    fun addToCurrentQueueTop(list: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueueTop", list.size)
        val queue = ArrayList<Song>(currentQueueState.value).apply {
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
    fun updateTopSong(newSong: Song) {
        L("MusicPlaylistManager updateTopSong", newSong)
        _currentSongState.value = newSong
        // add the current song on top of the queue
        _currentQueueState.value = ArrayList(_currentQueueState.value).apply {
            remove(newSong)
            add(0, newSong)
        }
    }

    fun addToCurrentQueueNext(song: Song?) = song?.let {
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
        updateUserMessage(logMessage = null)
        updateErrorLogMessage(logMessage = null)
    }
}
