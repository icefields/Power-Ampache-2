package luci.sixsixsix.powerampache2.presentation.playlists

import luci.sixsixsix.powerampache2.domain.models.Playlist

data class PlaylistsState (
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
