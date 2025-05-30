package luci.sixsixsix.powerampache2.presentation.screens.songs

import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongWrapper

data class SongsState(
    val songs: List<SongWrapper> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false
) {
    fun getSongList(): List<Song> = songs.map { it.song }
}
