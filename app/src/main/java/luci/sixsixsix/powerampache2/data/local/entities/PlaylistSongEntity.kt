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
package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.models.Song

@Entity
data class PlaylistSongEntity(
    @PrimaryKey
    val id: String,
    val songId: String,
    val playlistId: String,
    val position: Int,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String
) {
    companion object {
        fun newEntry(
            songId: String,
            playlistId: String,
            position: Int,
            username: String,
            serverUrl: String
        ) = PlaylistSongEntity(
            id = "$songId$playlistId${multiuserDbKey(username, serverUrl)}",
            songId = songId,
            playlistId = playlistId,
            position = position,
            multiUserId = multiuserDbKey(username, serverUrl)
        )

        fun newEntries(
            songs: List<Song>,
                       playlistId: String,
                       username: String,
                       serverUrl: String
        ) = mutableListOf<PlaylistSongEntity>().apply {
            songs.map { it.mediaId }.forEachIndexed { position,  songId ->
                add(newEntry(songId = songId,
                    playlistId = playlistId,
                    position = position,
                    username = username,
                    serverUrl = serverUrl)
                )
            }
        }
    }
}
