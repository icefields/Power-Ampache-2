package luci.sixsixsix.powerampache2.presentation.screens.playlists

import luci.sixsixsix.powerampache2.domain.models.Playlist

sealed class PlaylistEvent {
    data object Refresh: PlaylistEvent()
    data class OnSearchQueryChange(val query: String): PlaylistEvent()
    data class OnPlaylistDelete(val playlist: Playlist): PlaylistEvent()
    data object OnRemovePlaylistDismiss: PlaylistEvent()
    data class OnBottomListReached(val currentIndex: Int): PlaylistEvent()
}
