package luci.sixsixsix.powerampache2.presentation.screens.songs

import luci.sixsixsix.powerampache2.presentation.models.SongUI

data class SongsState(
    val songs: List<SongUI> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false
) {
    fun getSongList(): List<SongUI> = songs
}
