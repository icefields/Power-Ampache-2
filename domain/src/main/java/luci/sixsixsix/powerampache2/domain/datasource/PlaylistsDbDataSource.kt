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
package luci.sixsixsix.powerampache2.domain.datasource

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistSongItem
import luci.sixsixsix.powerampache2.domain.models.Song

interface PlaylistsDbDataSource {
    val playlistsFlow: Flow<List<Playlist>>

    suspend fun getPlaylists(query: String): List<Playlist>
    fun getPlaylist(id: String): Flow<Playlist>
    suspend fun savePlaylistsToDb(playlists: List<Playlist>, username: String, serverUrl: String, shouldClearBeforeAdding: Boolean)
    suspend fun savePlaylistSongsToDb(songs: List<Song>, playlistId: String, username: String, serverUrl: String)
    suspend fun savePlaylistSongRefsToDb(songRefs: List<PlaylistSongItem>, playlistId: String, username: String, serverUrl: String)
    suspend fun getSongsFromPlaylist(playlist: Playlist): List<Song>
}
