package luci.sixsixsix.powerampache2.presentation.song_detail

import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistEvent

sealed class SongDetailEvent {
    data object Play: SongDetailEvent()
}
