package luci.sixsixsix.powerampache2.presentation.main

import android.system.Os.remove
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import luci.sixsixsix.powerampache2.common.L
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

    fun updateCurrentSong(newSong: Song?) {
        L( "MusicPlaylistManager updateCurrentSong", newSong)
        _currentSongState.value = CurrentSongState(song = newSong)
        // add the current song on top of the queue
        _currentQueueState.value = ArrayList(_currentQueueState.value).apply {
            remove(newSong)
            add(0, newSong)
        }
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
    }

    fun addToCurrentQueue(newQueue: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueue", newQueue.size)
        _currentQueueState.value += newQueue
    }

    fun addToCurrentQueue(newSong: Song) {
        L( "MusicPlaylistManager addToCurrentQueue", newSong)
        addToCurrentQueue(listOf(newSong))
    }

    fun addToCurrentQueueNext(list: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueueNext", list.size)
        val queue = ArrayList<Song>(currentQueueState.value).apply {
            // remove duplicates
            removeAll(list.toSet())
            addAll(0, list)
//            // remove the current song and add it back on top, in case it has been removed
//            currentSongState.value.song?.let {
//                remove(it)
//                add(0, it)
//            }
        }
        replaceCurrentQueue(queue)
    }

    fun addToCurrentQueueNext(song: Song) {
        L( "MusicPlaylistManager addToCurrentQueueNext", song)
        addToCurrentQueueNext(listOf(song))
    }

    fun getCurrentSong(): Song? = currentSongState.value.song

    fun getErrorMessage(): String? = errorMessageState.value.errorMessage

    fun reset() {
        updateCurrentSong(newSong= null)
        updateSearchQuery(searchQuery= "")
        replaceCurrentQueue(listOf())
        // TODO keep error message for now
        // updateErrorMessage(errorMessage= null)
    }
}
