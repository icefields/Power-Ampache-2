package luci.sixsixsix.powerampache2.presentation.screens.offline

import luci.sixsixsix.powerampache2.domain.models.Song

data class OfflineSongsState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
)
