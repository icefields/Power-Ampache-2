/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.data.remote.dto.PlaylistDto
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist

@Parcelize
open class Playlist(
    val id: String,
    val name: String,
    val owner: String? = null,
    val items: Int? = 0,
    val type: PlaylistType? = null,
    val artUrl: String? = null,
    val flag: Int = 0,
    val preciseRating: Float = 0.0f,
    var rating: Int = 0,
    val averageRating: Float = 0.0f,
): Parcelable, AmpacheModel {
    companion object {
        fun empty() = Playlist("", "")
        fun mock(): Playlist = Gson().fromJson("{\n" +
                "            \"id\": \"2\",\n" +
                "            \"name\": \"2023-techdeath\",\n" +
                "            \"owner\": \"System\",\n" +
                "            \"items\": 67,\n" +
                "            \"type\": \"public\",\n" +
                "            \"art\": \"https:\\/\\/tari.ddns.net\\/image.php?object_id=2&object_type=playlist&name=art.jpg\",\n" +
                "            \"flag\": true,\n" +
                "            \"rating\": 4,\n" +
                "            \"averagerating\": null\n" +
                "        }", PlaylistDto::class.java).toPlaylist()
    }
}

enum class PlaylistType { public, private }

fun Playlist.isSmartPlaylist() = id.lowercase().startsWith("smart_")
fun Playlist.isOwnerSystem() = owner?.lowercase() == "system"
fun Playlist.isOwnerAdmin() = owner?.lowercase() == "admin"
fun Playlist.isFavourite() = flag == 1


@Parcelize
class RecentPlaylist: Playlist(id = "", name = "Recently Played Songs")

@Parcelize
class FrequentPlaylist: Playlist(id = "", name = "Frequently Played Songs")

@Parcelize
class HighestPlaylist: Playlist(id = "", name = "Highest Rated Songs")

@Parcelize
class FlaggedPlaylist: Playlist(id = "", name = "Favourite Songs")
