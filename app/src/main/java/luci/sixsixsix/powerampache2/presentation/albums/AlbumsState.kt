package luci.sixsixsix.powerampache2.presentation.albums

import luci.sixsixsix.powerampache2.domain.models.Album

data class AlbumsState (
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
