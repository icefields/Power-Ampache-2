package luci.sixsixsix.powerampache2.presentation.screens.songs

import luci.sixsixsix.powerampache2.domain.models.Song

sealed class SongsEvent {
    data object Refresh: SongsEvent()
    data class OnSearchQueryChange(val query: String): SongsEvent()
    data class OnSongSelected(val song: Song): SongsEvent()
}
