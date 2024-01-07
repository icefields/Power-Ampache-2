package luci.sixsixsix.powerampache2.presentation.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

data class CurrentSongState(val song: Song? = null)
data class ErrorMessageState(var errorMessage: String? = null)

@Singleton
class MusicPlaylistManager @Inject constructor() {
    private val _currentSongState = MutableStateFlow(CurrentSongState())
    val currentSongState: StateFlow<CurrentSongState> = _currentSongState //val currentSong = _currentSong.asStateFlow()

    private val _errorMessageState = MutableStateFlow(ErrorMessageState())
    val errorMessageState: StateFlow<ErrorMessageState> = _errorMessageState

    private val _currentSearchQuery = MutableStateFlow("")
    val currentSearchQuery: StateFlow<String> = _currentSearchQuery

    private val _currentQueueState = MutableStateFlow(listOf<Song>())
    val currentQueueState: StateFlow<List<Song>> = _currentQueueState

    fun updateTopSong(newSong: Song?) {
        L( "MusicPlaylistManager updateTopSong", newSong)
        _currentSongState.value = CurrentSongState(song = newSong)
        // add the current song on top of the queue
        _currentQueueState.value = ArrayList(_currentQueueState.value).apply {
            remove(newSong)
            add(0, newSong)
        }
    }

    /**
     * used in the callback when music player goes to the next song in the playlist
     */
    fun updateCurrentSong(newSong: Song?) {
        L( "MusicPlaylistManager updateCurrentSong", newSong)
        _currentSongState.value = CurrentSongState(song = newSong)
    }

    fun moveToSongInQueue(newSong: Song?) {
        L( "MusicPlaylistManager moveToSongInQueue", newSong)
        _currentSongState.value = CurrentSongState(song = newSong)
    }

    fun updateErrorMessage(errorMessage: String?) {
        L("MusicPlaylistManager updateErrorMessage", errorMessage)
        _errorMessageState.value = ErrorMessageState(errorMessage = errorMessage)
    }

    fun updateSearchQuery(searchQuery: String) {
        L( "MusicPlaylistManager updateSearchQuery $searchQuery")
        _currentSearchQuery.value = searchQuery
    }

    fun replaceCurrentQueue(newPlaylist: List<Song>) {
        L( "MusicPlaylistManager replaceCurrentQueue", newPlaylist)
        _currentQueueState.value = newPlaylist
        checkCurrentSong()
    }

    fun addToCurrentQueue(newQueue: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueue", newQueue.size)
        _currentQueueState.value += newQueue
        checkCurrentSong()
    }

    fun addToCurrentQueue(newSong: Song) {
        L( "MusicPlaylistManager addToCurrentQueue", newSong)
        addToCurrentQueue(listOf(newSong))
    }

    fun addToCurrentQueueNext(list: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueueNext", list.size)
        val queue = ArrayList<Song>(currentQueueState.value).apply {
            val currentSongIndex = indexOf(currentSongState.value.song)
            addAll( if (size > currentSongIndex+1) { currentSongIndex+1 } else { size } , list)
        }
        replaceCurrentQueue(queue)
    }

    fun addToCurrentQueueTop(list: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueueTop", list.size)
        val queue = ArrayList<Song>(currentQueueState.value).apply {
            addAll(0, list)
        }
        replaceCurrentQueue(queue)
    }

    private fun checkCurrentSong() {
        if (currentQueueState.value.isNotEmpty() && getCurrentSong() == null) {
            updateTopSong(currentQueueState.value[0])
        }
    }

    fun addToCurrentQueueNext(song: Song) {
        L( "MusicPlaylistManager addToCurrentQueueNext", song)
        addToCurrentQueueNext(listOf(song))
    }

    fun startRestartQueue() {
        _currentSongState.value = CurrentSongState(song = currentQueueState.value[0])
    }

    fun getCurrentSong(): Song? = currentSongState.value.song

    fun getErrorMessage(): String? = errorMessageState.value.errorMessage

    fun reset() {
        updateTopSong(newSong= null)
        updateSearchQuery(searchQuery= "")
        replaceCurrentQueue(listOf())
        // TODO keep error message for now
        // updateErrorMessage(errorMessage= null)
    }
}
