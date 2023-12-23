package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.domain.models.Song

data class MainState (
    val searchQuery: String = "",
    val currentSong: Song? = null
)
