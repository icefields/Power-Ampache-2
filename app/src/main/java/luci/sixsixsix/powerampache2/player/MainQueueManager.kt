//package luci.sixsixsix.powerampache2.player
//
//import luci.sixsixsix.mrlog.L
//import luci.sixsixsix.powerampache2.domain.models.Song
//import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainState
//
//interface MainQueueManager {
//    fun updateTopSong(mainState: MainState, newSong: Song?) = newSong?.let { song ->
//        L( "MusicPlaylistManager updateTopSong", newSong)
//        mainState.copy(
//            song = song,
//            queue = ArrayList(mainState.queue).apply {
//                remove(newSong)
//                add(0, newSong)
//            }
//        )
//    }
//
//    /**
//     * used in the callback when music player goes to the next song in the playlist
//     */
//    fun updateCurrentSong(mainState: MainState, newSong: Song?) = newSong?.let { song ->
//        L( "MusicPlaylistManager updateCurrentSong", newSong)
//        mainState.copy(song = song)
//    }
//
//    fun moveToSongInQueue(mainState: MainState, newSong: Song?) = newSong?.let { song ->
//        L( "MusicPlaylistManager moveToSongInQueue", newSong)
//        mainState.copy(song = song)
//    }
//
//    fun replaceCurrentQueue(mainState: MainState, newPlaylist: List<Song>) =
//        mainState.copy(queue = newPlaylist.filterNotNull()).run {
//            L( "MusicPlaylistManager replaceCurrentQueue", newPlaylist.size)
//            checkCurrentSong(this)
//        }
//
//    fun addToCurrentQueue(mainState: MainState, newQueue: List<Song>) =
//        mainState.copy(queue = LinkedHashSet(mainState.queue)
//            .apply { addAll(newQueue) }
//            .toList()
//        ).run {
//            L( "MusicPlaylistManager addToCurrentQueue", newQueue.size)
//            checkCurrentSong(this)
//        }
//
//    fun addToCurrentQueue(mainState: MainState, newSong: Song?) = newSong?.let {
//        L( "MusicPlaylistManager addToCurrentQueue", newSong)
//        addToCurrentQueue(mainState, listOf(newSong))
//    }
//
//    fun removeFromCurrentQueue(mainState: MainState, songsToRemove: List<Song>) =  mainState.copy(
//        queue = LinkedHashSet(mainState.queue)
//            .apply { removeAll(songsToRemove.toSet()) }
//            .toList()
//        ).run {
//            L( "MusicPlaylistManager removeFromCurrentQueue", songsToRemove.size, songsToRemove[0].name)
//            checkCurrentSong(this)
//        }
//
//
//    fun removeFromCurrentQueue(mainState: MainState, songToRemove: Song) =
//        removeFromCurrentQueue(mainState, listOf(songToRemove))
//
//    /**
//     * add items to the current queue as next in queue
//     */
//    fun addToCurrentQueueNext(mainState: MainState, list: List<Song>) =  mainState.copy(
//        queue = ArrayList(mainState.queue)
//            .apply {
//                // remove all songs except the current
//                val listWithoutCurrentSong = ArrayList(list)
//                    .apply { remove(mainState.song) }
//                removeAll(listWithoutCurrentSong.toSet())
//                // find current index, new songs will be added after that
//                val currentSongIndex = indexOf(mainState.song)
//                addAll( if (size > currentSongIndex+1) { currentSongIndex+1 } else { size } , listWithoutCurrentSong)
//            }
//    ).run {
//        replaceCurrentQueue(this, queue)
//    }
//
//    fun addToCurrentQueueTop(mainState: MainState, list: List<Song>) = mainState.copy(
//        queue = ArrayList<Song>(mainState.queue).apply {
//            addAll(0, list)
//        }
//    ).run {
//        L( "MusicPlaylistManager addToCurrentQueueTop", list.size)
//        replaceCurrentQueue(this, queue)
//    }
//
//    private fun checkCurrentSong(mainState: MainState) =
//        if (mainState.queue.isNotEmpty() && getCurrentSong(mainState) == null) {
//            updateTopSong(mainState, mainState.queue[0])
//        } else mainState
//
//    fun addToCurrentQueueNext(mainState: MainState, song: Song?) = song?.let {
//        L( "MusicPlaylistManager addToCurrentQueueNext", song)
//        addToCurrentQueueNext(mainState, listOf(song))
//    }
//
//    fun startRestartQueue(mainState: MainState) = mainState.copy(
//        song = mainState.queue[0]
//    )
//
//    fun getCurrentSong(mainState: MainState): Song? = mainState.song
//
//    // fun getErrorMessage(): String? = logMessageUserReadableState.value.errorMessage
//
//    /**
//     * remove all songs except the currently playing one if any
//     */
//    fun clearQueue(mainState: MainState) =
//        replaceCurrentQueue(mainState, listOfNotNull(mainState.song))
//}
