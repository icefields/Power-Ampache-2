package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.domain.models.Song

data class MainState (
    val searchQuery: String = "",
    val errorMessage: String = "",
    val song: Song? = null,
    val queue: List<Song> = listOf(),
    val isLikeLoading:Boolean = false,
    val isDownloading:Boolean = false
)
