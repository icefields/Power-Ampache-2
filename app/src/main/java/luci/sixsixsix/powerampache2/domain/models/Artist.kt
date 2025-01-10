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
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.common.Constants.LOADING_STRING

typealias ArtistId = String

@Parcelize
data class Artist(
    override val id: String,
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
): Parcelable, AmpacheModel {
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

        fun empty(): Artist = Artist(
            id = ERROR_STRING,
            name = "",
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


val Artist.genresString
    get() = genre.mapNotNull { it.name }.joinToString(", ")
