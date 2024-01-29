package luci.sixsixsix.powerampache2.presentation.screens.songs

import luci.sixsixsix.powerampache2.presentation.common.SongWrapper

data class SongsState(
    val songs: List<SongWrapper> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false
)
