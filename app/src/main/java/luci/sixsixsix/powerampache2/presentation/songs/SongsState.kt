package luci.sixsixsix.powerampache2.presentation.songs

import luci.sixsixsix.powerampache2.domain.models.Song

data class SongsState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false
)
