package luci.sixsixsix.powerampache2.presentation.songs

sealed class SongsEvent {
    data object Refresh: SongsEvent()
    data class OnSearchQueryChange(val query: String): SongsEvent()
}
