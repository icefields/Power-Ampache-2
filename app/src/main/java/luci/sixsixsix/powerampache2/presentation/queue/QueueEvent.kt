package luci.sixsixsix.powerampache2.presentation.queue

import luci.sixsixsix.powerampache2.domain.models.Song

sealed class QueueEvent {
    data class OnSongSelected(val song: Song): QueueEvent()
    data object OnPlayQueue: QueueEvent()
}
