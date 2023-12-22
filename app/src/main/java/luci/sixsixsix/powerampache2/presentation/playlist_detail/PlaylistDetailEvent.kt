package luci.sixsixsix.powerampache2.presentation.playlist_detail

sealed class PlaylistDetailEvent {
    data object Refresh: PlaylistDetailEvent()
    data class Fetch(val playlistId: String): PlaylistDetailEvent()
}
