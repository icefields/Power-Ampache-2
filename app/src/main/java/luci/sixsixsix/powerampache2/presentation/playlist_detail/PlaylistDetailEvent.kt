package luci.sixsixsix.powerampache2.presentation.playlist_detail

import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.songs.SongsEvent

sealed class PlaylistDetailEvent {
    data object Refresh: PlaylistDetailEvent()
    data class Fetch(val playlistId: String): PlaylistDetailEvent()
    data class OnSongSelected(val song: Song): PlaylistDetailEvent()

}
