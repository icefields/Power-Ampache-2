package luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail

sealed class ArtistDetailEvent {
    data object Refresh: ArtistDetailEvent()
    data class Fetch(val albumId: String): ArtistDetailEvent()
}
