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
import java.util.UUID

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
    val averageRating: Float = 0.0f
): Comparable<Album>, Parcelable {
    override fun compareTo(other: Album): Int = id.compareTo(other.id)

    companion object {
        fun mock() = Album(
            name = "Album title",
            time = 129,
            id = UUID.randomUUID().toString(),
            songCount = 11,
            genre = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Thrash Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Progressive Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Jazz"),
            ),
            artists = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Marty Friedman"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Other people"),
            ),
            year = 1986
        )
    }
}

fun Album.totalTime(): String {
    val minutes = time / 60
    val seconds = time % 60
    return "${minutes}m ${seconds}s"
}

// LISTS PERFORMANCE . urls contain the token, do not rely only on id
fun Album.key(): String = "${id}${artUrl}"//.md5()
