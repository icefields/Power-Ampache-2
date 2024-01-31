package luci.sixsixsix.powerampache2.presentation.queue

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
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
