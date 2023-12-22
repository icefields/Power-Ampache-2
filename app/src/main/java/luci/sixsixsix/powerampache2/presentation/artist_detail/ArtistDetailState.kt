package luci.sixsixsix.powerampache2.presentation.artist_detail

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Song

data class ArtistDetailState (
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFetchingMore: Boolean = false
)
