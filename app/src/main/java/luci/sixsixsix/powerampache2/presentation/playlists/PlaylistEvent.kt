package luci.sixsixsix.powerampache2.presentation.playlists

import luci.sixsixsix.powerampache2.presentation.songs.SongsEvent

sealed class PlaylistEvent {
    data object Refresh: PlaylistEvent()
    data class OnSearchQueryChange(val query: String): PlaylistEvent()
    data class OnBottomListReached(val currentIndex: Int): PlaylistEvent()
}
