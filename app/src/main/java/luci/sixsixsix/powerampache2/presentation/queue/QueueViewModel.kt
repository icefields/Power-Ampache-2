package luci.sixsixsix.powerampache2.presentation.queue

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class QueueViewModel  @Inject constructor(
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    fun onEvent(event: QueueEvent) {
        when(event) {
            is QueueEvent.OnSongSelected -> {
                L("QueueEvent.OnSongSelected", event.song)
                playlistManager.moveToSongInQueue(event.song)
            }

            QueueEvent.OnPlayQueue -> {
                L("QueueEvent.OnPlayQueue")
                playlistManager.startRestartQueue()
            }
        }
    }
}


