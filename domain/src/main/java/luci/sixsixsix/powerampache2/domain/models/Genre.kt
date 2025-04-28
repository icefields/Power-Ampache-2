package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val id: String,
    val name: String,
    val albums: Int,
    val artists: Int,
    val songs: Int,
    val playlists: Int
): Parcelable
