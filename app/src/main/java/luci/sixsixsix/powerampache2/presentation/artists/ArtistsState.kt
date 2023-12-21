package luci.sixsixsix.powerampache2.presentation.artists

import luci.sixsixsix.powerampache2.domain.models.Artist

data class ArtistsState (
    val artists: List<Artist> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false
)
