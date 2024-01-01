package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Playlist(
    val id: String,
    val name: String,
    val owner: String? = null,
    val items: Int? = 0,
    val type: String? = null,
    val artUrl: String? = null,
): Parcelable {

@Parcelize
class RecentPlaylist: Playlist(id = "", name = "Recently Played Songs")
@Parcelize
class FrequentPlaylist: Playlist(id = "", name = "Frequently Played Songs")
@Parcelize
class HighestPlaylist: Playlist(id = "", name = "Highest Rated Songs")
@Parcelize
class FlaggedPlaylist: Playlist(id = "", name = "Flagged Songs")
}