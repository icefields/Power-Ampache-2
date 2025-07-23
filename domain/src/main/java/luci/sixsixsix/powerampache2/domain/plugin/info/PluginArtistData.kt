/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.domain.plugin.info

data class PluginArtistData(
    val id: String,
    val artistName: String,
    val description: String,
    val shortDescription: String,
    val mbId: String = "",
    val language: String = "",
    val imageUrl: String = "",
    val year: String,

    val url: String? = null,
    // val links // TODO: add links

    val onTour: String? = "",
    val similar: List<SimilarArtist>,
    val listeners: Int,
    val playCount: Int,
    val tags: List<String>
) {
    companion object {
        fun emptyPluginArtistData(artistId: String, artistName: String, artistMbId: String) = PluginArtistData(
            id = artistId,
            artistName = artistName,
            description = "",
            shortDescription = "",
            mbId = artistMbId,
            language = "",
            imageUrl = "",
            year = "",
            url = "",
            onTour = "",
            similar = listOf(),
            listeners = 0,
            playCount = 0,
            tags = listOf()
        )
    }
}

data class SimilarArtist(
    val name: String,
    val url: String,
    val image: String,
    val mbId: String
)

