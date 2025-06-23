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
package luci.sixsixsix.powerampache2.data.offlinemode

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylist
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.di.OfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.mapNotNull

@OfflineModeDataSource
class PlaylistsOfflineDataSourceImpl @Inject constructor(
    db: MusicDatabase,
): PlaylistsOfflineDataSource {
    private val dao = db.dao

    override val playlistsFlow: Flow<List<Playlist>> = dao.playlistsFlow().map { entities ->
        entities.filter { isPlaylistOffline(it.id) }
    }.mapNotNull { list -> list.map { it.toPlaylist() } }

    override suspend fun getPlaylists(query: String) =
        dao.searchPlaylists(query).filter { isPlaylistOffline(it.id) }.map { it.toPlaylist() }

    private suspend fun isPlaylistOffline(playlistId: String) =
        dao.getOfflineSongsFromPlaylist(playlistId).isNotEmpty()

    override suspend fun getSongsFromPlaylist(playlistId: String): List<Song> =
        dao.getOfflineSongsFromPlaylist(playlistId).map { it.toSong() }
}