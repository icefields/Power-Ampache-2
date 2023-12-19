package luci.sixsixsix.powerampache2.domain.models

data class Playlist(
    val id: String,
    val name: String,
    val owner: String,
    val items: Int? = 0,
    val type: String? = null,
    val artUrl: String? = null,
)
