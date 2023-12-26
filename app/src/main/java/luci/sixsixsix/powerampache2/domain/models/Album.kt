package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.common.md5


@Parcelize
data class Album(
    val id: String = "",
    val name: String = "",
    val basename: String = "",
    val artist: MusicAttribute = MusicAttribute.emptyInstance(),
    val artists: List<MusicAttribute> = listOf(),
    val time: Int = 0,
    val year: Int = 0,
    val tracks: List<Song> = listOf(),
    val songCount: Int = 0,
    val diskCount: Int = 0,
    val genre: List<MusicAttribute> = listOf(),
    val artUrl: String = "",
    val flag: Int = 0,
    val rating: Int = 0,
    val averageRating: Int = 0,
): Comparable<Album>, Parcelable {
    override fun compareTo(other: Album): Int = id.compareTo(other.id)
}

fun Album.totalTime(): String {
    val minutes = time / 60
    val seconds = time % 60
    return "${minutes}m ${seconds}s"
}

// LISTS PERFORMANCE . urls contain the token, do not rely only on id
fun Album.key(): String = "${id}${artUrl}"//.md5()