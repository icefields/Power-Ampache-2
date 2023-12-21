package luci.sixsixsix.powerampache2.presentation.artists

import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistEvent

sealed class ArtistEvent {
    data object Refresh: ArtistEvent()
    data class OnSearchQueryChange(val query: String): ArtistEvent()
    data class OnBottomListReached(val currentIndex: Int): ArtistEvent()
}
