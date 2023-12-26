package luci.sixsixsix.powerampache2.domain.models

data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int = 0,
    val songCount: Int = 0,
    val genre: List<MusicAttribute> = listOf(),
    val artUrl: String = "",
    val flag: Int = 0,
    val summary: Any? = null,
    val time: Int = 0,
    val yearFormed: Int = 0,
    val placeFormed: Any? = null
)
