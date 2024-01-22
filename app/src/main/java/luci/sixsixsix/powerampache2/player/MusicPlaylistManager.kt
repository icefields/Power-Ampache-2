package luci.sixsixsix.powerampache2.player

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

    fun updateTopSong(newSong: Song?) = newSong?.let {
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
    fun updateCurrentSong(newSong: Song?) = newSong?.let {
        L( "MusicPlaylistManager updateCurrentSong", newSong)
        _currentSongState.value = CurrentSongState(song = newSong)
    }

    fun moveToSongInQueue(newSong: Song?) = newSong?.let {
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
        L( "MusicPlaylistManager replaceCurrentQueue", newPlaylist.size)
        _currentQueueState.value = newPlaylist.filterNotNull()
        checkCurrentSong()
    }

    fun addToCurrentQueue(newQueue: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueue", newQueue.size)
        _currentQueueState.value = LinkedHashSet(_currentQueueState.value)
            .apply { addAll(newQueue) }
            .toList()
        checkCurrentSong()
    }

    fun addToCurrentQueue(newSong: Song?) = newSong?.let {
        L( "MusicPlaylistManager addToCurrentQueue", newSong)
        addToCurrentQueue(listOf(newSong))
    }

    fun removeFromCurrentQueue(songsToRemove: List<Song>) {
        L( "MusicPlaylistManager removeFromCurrentQueue", songsToRemove.size, songsToRemove[0].name)
        _currentQueueState.value = LinkedHashSet(_currentQueueState.value)
            .apply { removeAll(songsToRemove.toSet()) }
            .toList()
        checkCurrentSong()
        L( "MusicPlaylistManager removeFromCurrentQueue", songsToRemove.size, songsToRemove[0].name)
    }

    fun removeFromCurrentQueue(songToRemove: Song) = removeFromCurrentQueue(listOf(songToRemove))

    /**
     * add items to the current queue as next in queue
     */
    fun addToCurrentQueueNext(list: List<Song>) {
        L( "MusicPlaylistManager addToCurrentQueueNext", list.size)
        val queue = ArrayList(_currentQueueState.value)
            .apply {
                // remove all songs except the current
                val listWithoutCurrentSong = ArrayList(list)
                    .apply { remove(currentSongState.value.song) }
                removeAll(listWithoutCurrentSong.toSet())
                // find current index, new songs will be added after that
                val currentSongIndex = indexOf(currentSongState.value.song)
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

    private fun checkCurrentSong() {
        if (currentQueueState.value.isNotEmpty() && getCurrentSong() == null) {
            updateTopSong(currentQueueState.value[0])
        }
    }

    fun addToCurrentQueueNext(song: Song?) = song?.let {
        L( "MusicPlaylistManager addToCurrentQueueNext", song)
        addToCurrentQueueNext(listOf(song))
    }

    fun startRestartQueue() {
        _currentSongState.value = CurrentSongState(song = currentQueueState.value[0])
    }

    fun getCurrentSong(): Song? = currentSongState.value.song

    fun getErrorMessage(): String? = errorMessageState.value.errorMessage

    /**
     * remove all songs except the currently playing one if any
     */
    fun clearQueue() =
        replaceCurrentQueue(listOfNotNull(currentSongState.value.song))

    fun reset() {
        _currentSongState.value = CurrentSongState(song = null)
        updateSearchQuery(searchQuery= "")
        replaceCurrentQueue(listOf())
        // TODO keep error message for now
        // updateErrorMessage(errorMessage= null)
    }
}
