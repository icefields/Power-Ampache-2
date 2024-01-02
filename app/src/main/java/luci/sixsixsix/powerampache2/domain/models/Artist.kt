package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.common.Constants.LOADING_STRING

@Parcelize
data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int = 0,
    val songCount: Int = 0,
    val genre: List<MusicAttribute> = listOf(),
    val artUrl: String = "",
    val flag: Int = 0,
    val summary: String? = null,
    val time: Int = 0,
    val yearFormed: Int = 0,
    val placeFormed: String? = null
): Parcelable {
    companion object {
        fun mockArtist(): Artist = Artist(
            id = "883",
            name = "After the Burial",
            albumCount = 7,
            songCount = 76,
            genre = listOf(
                MusicAttribute(id = "4", name = "Metal"),
                MusicAttribute(id = "4", name = "Metal"),
                MusicAttribute(id = "33", name = "Hardcore"),
                MusicAttribute(id = "131", name = "Progressive Metalcore"),
                MusicAttribute(id = "132", name = "Prog. Metalcore")
            ),
            artUrl = "http://192.168.1.100/ampache/public/image.php?object_id=883&object_type=artist",
            time = 19736,
            yearFormed = 0,
            placeFormed = null
        )

        fun loading(): Artist = Artist(
            id = ERROR_STRING,
            name = LOADING_STRING,
            albumCount = ERROR_INT,
            songCount = ERROR_INT,
            genre = listOf(),
            artUrl = ERROR_STRING,
            time = 0,
            yearFormed = 0,
            placeFormed = null
        )
    }
}
