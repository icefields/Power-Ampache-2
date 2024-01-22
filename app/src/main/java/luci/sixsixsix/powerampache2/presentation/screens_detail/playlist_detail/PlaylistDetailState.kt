package luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail

import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song

data class PlaylistDetailState (
    val playlist: Playlist = Playlist("", ""),
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPlaylistRemoveLoading: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false,
    val isUserOwner: Boolean = false
)
