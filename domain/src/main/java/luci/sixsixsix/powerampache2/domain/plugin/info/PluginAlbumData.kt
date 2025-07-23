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

data class PluginAlbumData(
    val id: String,
    val albumName: String,
    val artistName: String,
    val description: String,
    val shortDescription: String,
    val mbId: String,
    val language: String,
    val lyrics: String,
    val artistMbId: String,
    val albumArtistMbId: String,
    val imageUrl: String,
    val year: String,
    val tags: List<String>,
    val rank: Int,
    val url: String,
    val imageArtist: String,
    val urlArtist: String,
    val duration: Int,
    val listeners: Int,
    val playCount: Int,
    val tracks: List<AlbumTrack>,
) {
    companion object {
        fun emptyPluginAlbumData(
            albumId: String,
            musicBrainzId: String,
            albumTitle: String,
            artistName: String
        ) = PluginAlbumData(
            id = albumId, mbId = musicBrainzId, albumName = albumTitle, artistName = artistName,
            description = "",
            shortDescription = "",
            language = "",
            lyrics = "",
            artistMbId = "",
            albumArtistMbId = "",
            imageUrl = "",
            year = "",
            tags = listOf(),
            rank = 0,
            url = "",
            imageArtist = "",
            urlArtist = "",
            duration = 0,
            listeners = 0,
            playCount = 0,
            tracks = listOf()
        )
    }
}

fun PluginAlbumData.totalTime(): String {
    val time = duration
    val minutes = time / 60
    val seconds = time % 60
    return "${minutes}m ${seconds}s"
}

data class AlbumTrack(
    val mbId: String,
    val title: String,
    val duration: Int,
    val url: String,
    val songArtistName: String,
    val songArtistMbId: String
)
