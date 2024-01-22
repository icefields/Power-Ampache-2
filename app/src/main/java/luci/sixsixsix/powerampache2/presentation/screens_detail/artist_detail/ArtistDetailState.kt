package luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist

data class ArtistDetailState (
    val artist: Artist = Artist.loading(),
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFetchingMore: Boolean = false
)
