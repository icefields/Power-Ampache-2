package luci.sixsixsix.powerampache2.presentation.screens.offline

import luci.sixsixsix.powerampache2.presentation.models.SongUI

data class OfflineSongsState(
    val songs: List<SongUI> = emptyList(),
    val isLoading: Boolean = false,
)
