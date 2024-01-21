package luci.sixsixsix.powerampache2.presentation.screens.artists

sealed class ArtistEvent {
    data object Refresh: ArtistEvent()
    data class OnSearchQueryChange(val query: String): ArtistEvent()
    data class OnBottomListReached(val currentIndex: Int): ArtistEvent()
}
