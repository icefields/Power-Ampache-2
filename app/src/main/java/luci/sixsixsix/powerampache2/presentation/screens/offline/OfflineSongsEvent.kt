package luci.sixsixsix.powerampache2.presentation.screens.offline

import luci.sixsixsix.powerampache2.presentation.models.SongUI

sealed class OfflineSongsEvent {
    data class OnSongSelected(val song: SongUI): OfflineSongsEvent()
}