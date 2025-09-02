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

data class PluginSongData(
    val id: String,
    val title: String = "",
    val albumName: String = "",
    val artistName: String = "",
    val description: String = "",
    val shortDescription: String = "",
    val mbId: String = "",
    val language: String = "",
    val lyrics: String = "",
    val albumMbId: String = "",
    val artistMbId: String = "",
    val imageUrl: String = "",
    val year: String = "",
    val url: String,
    val artistAlbum: String,
    val imageAlbum: String,
    val urlAlbum: String,
    val imageArtist: String,
    val urlArtist: String,
    val duration: Int,
    val listeners: Int,
    val playCount: Int,
    val topTags: List<String>,
    val position: Int
) {
    companion object {
        fun emptyPluginSongData(
            songId: String,
            musicBrainzId: String,
            songTitle: String,
            albumTitle: String,
            artistName: String
        ) = PluginSongData(
            id = songId,
            mbId = musicBrainzId,
            title = songTitle,
            albumName = albumTitle,
            artistName = artistName,
            description = "",
            shortDescription = "",
            language = "",
            lyrics = "",
            albumMbId = "",
            artistMbId = "",
            imageUrl = "",
            year = "",
            url = "",
            artistAlbum = "",
            imageAlbum = "",
            urlAlbum = "",
            imageArtist = "",
            urlArtist = "",
            duration = 0,
            listeners = 0,
            playCount = 0,
            topTags = listOf(),
            position = 0
        )
    }
}

fun PluginSongData.totalTime(): String {
    val time = duration / 1000
    val minutes = time / 60
    val seconds = time % 60
    return "${minutes}m ${seconds}s"
}
