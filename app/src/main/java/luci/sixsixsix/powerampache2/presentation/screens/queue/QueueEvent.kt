package luci.sixsixsix.powerampache2.presentation.screens.queue

import luci.sixsixsix.powerampache2.presentation.models.SongUI

sealed class QueueEvent {
    data class OnSongSelected(val song: SongUI): QueueEvent()
    data class OnSongRemove(val song: SongUI): QueueEvent()
    data object OnPlayQueue: QueueEvent()
    data object OnClearQueue: QueueEvent()
}
