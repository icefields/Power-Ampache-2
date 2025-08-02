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
package luci.sixsixsix.powerampache2.data.local.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toAlbum
import luci.sixsixsix.powerampache2.data.local.entities.toAlbumEntity
import luci.sixsixsix.powerampache2.di.LocalDataSource
import luci.sixsixsix.powerampache2.domain.common.normalizeForSearch
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsDbDataSource
import luci.sixsixsix.powerampache2.domain.errors.NullDataException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder
import luci.sixsixsix.powerampache2.domain.models.SortOrder
import javax.inject.Inject
import javax.inject.Singleton

@LocalDataSource
@Singleton
class AlbumsDbDataSourceImpl @Inject constructor(db: MusicDatabase): AlbumsDbDataSource {
    private val dao = db.dao

    override val recommendedFlow: Flow<List<Album>>
        get() = dao.getRecommendedAlbums().mapNotNull { list -> list.map { it.toAlbum() } }

    override suspend fun saveAlbumsToDb(username: String, serverUrl: String, albums: List<Album>) {
        dao.insertAlbums(albums.map { it.toAlbumEntity(username = username, serverUrl = serverUrl) })
    }

    override suspend fun getAlbums(
        query: String,
        sort: AlbumSortOrder,
        order: SortOrder
    ): List<Album> =
        dao.searchAlbum(query.normalizeForSearch()).map { it.toAlbum() }

    override suspend fun getAlbumFromId(id: String): Album = dao.getAlbum(id)?.toAlbum()
        ?: throw NullDataException("getAlbumFromId DB, cannot find album data in internal db")

    override suspend fun getAlbumFromIdFlow(id: String): Flow<Album> =
        dao.getAlbumFlow(id).filterNotNull().map { it.toAlbum() }
}
