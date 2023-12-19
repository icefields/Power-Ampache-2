package luci.sixsixsix.powerampache2.presentation.artists

sealed class ArtistEvent {
    data object Refresh: ArtistEvent()
    data class OnSearchQueryChange(val query: String): ArtistEvent()
}
