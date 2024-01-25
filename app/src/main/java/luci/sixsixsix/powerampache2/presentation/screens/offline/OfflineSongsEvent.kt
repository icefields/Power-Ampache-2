package luci.sixsixsix.powerampache2.presentation.screens.offline

import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.screens.songs.SongsEvent

sealed class OfflineSongsEvent {
    data class OnSongSelected(val song: Song): OfflineSongsEvent()
}