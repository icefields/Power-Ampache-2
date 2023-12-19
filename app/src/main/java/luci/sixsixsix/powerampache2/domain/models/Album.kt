package luci.sixsixsix.powerampache2.domain.models


data class Album(
    val id: String = "",
    val name: String = "",
    val basename: String = "",
    val artist: MusicAttribute = MusicAttribute.emptyInstance(),
    val artists: List<MusicAttribute> = listOf(),
    val time: Int = 0,
    val year: Int = 0,
    val tracks: List<Any> = listOf(),
    val songCount: Int = 0,
    val diskCount: Int = 0,
    val genre: List<MusicAttribute> = listOf(),
    val artUrl: String = "",
    val flag: Int = 0,
    val rating: Int = 0,
    val averageRating: Int = 0,
)
