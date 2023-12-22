package luci.sixsixsix.powerampache2.presentation.artist_detail

sealed class ArtistDetailEvent {
    data object Refresh: ArtistDetailEvent()
    data class Fetch(val albumId: String): ArtistDetailEvent()
}
