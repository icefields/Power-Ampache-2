package luci.sixsixsix.powerampache2.presentation.albums

import luci.sixsixsix.powerampache2.presentation.artists.ArtistEvent

sealed class AlbumsEvent {
    data object Refresh: AlbumsEvent()
    data class OnSearchQueryChange(val query: String): AlbumsEvent()
    data class OnBottomListReached(val currentIndex: Int): AlbumsEvent()
}
