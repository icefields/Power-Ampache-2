package luci.sixsixsix.powerampache2.presentation.album_detail


sealed class AlbumDetailEvent {
    data object Refresh: AlbumDetailEvent()
    data class Fetch(val albumId: String): AlbumDetailEvent()
}
