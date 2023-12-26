package luci.sixsixsix.powerampache2.presentation.home

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Playlist

data class HomeScreenState (
    val playlists: List<Playlist> = emptyList(),
    val recentAlbums: List<Album> = emptyList(),
    val newestAlbums: List<Album> = emptyList(),
    val highestAlbums: List<Album> = emptyList(),
    val frequentAlbums: List<Album> = emptyList(),
    val flaggedAlbums: List<Album> = emptyList(),
    val randomAlbums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
)
