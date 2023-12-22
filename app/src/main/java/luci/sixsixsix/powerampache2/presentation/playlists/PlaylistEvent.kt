package luci.sixsixsix.powerampache2.presentation.playlists

sealed class PlaylistEvent {
    data object Refresh: PlaylistEvent()
    data class OnSearchQueryChange(val query: String): PlaylistEvent()
    data class OnBottomListReached(val currentIndex: Int): PlaylistEvent()
}
