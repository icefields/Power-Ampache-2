package luci.sixsixsix.powerampache2.presentation.albums

sealed class AlbumsEvent {
    data object Refresh: AlbumsEvent()
    data class OnSearchQueryChange(val query: String): AlbumsEvent()
}
