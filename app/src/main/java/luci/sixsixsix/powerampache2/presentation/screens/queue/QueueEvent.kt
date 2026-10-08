package luci.sixsixsix.powerampache2.presentation.screens.queue

import luci.sixsixsix.powerampache2.presentation.models.PlayableUI

sealed class QueueEvent {
    data class OnSongSelected(val song: PlayableUI): QueueEvent()
    data class OnSongRemove(val song: PlayableUI): QueueEvent()
    data object OnPlayQueue: QueueEvent()
    data object OnClearQueue: QueueEvent()
}
