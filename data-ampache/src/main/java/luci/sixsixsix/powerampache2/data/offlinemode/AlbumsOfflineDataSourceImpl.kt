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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.toAlbum
import luci.sixsixsix.powerampache2.di.OfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.common.sanitizeSortTerm
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.errors.NullDataException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder
import luci.sixsixsix.powerampache2.domain.models.SortOrder
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@OfflineModeDataSource
@Singleton
class AlbumsOfflineDataSourceImpl @Inject constructor(db: MusicDatabase): AlbumsOfflineDataSource {
    private val dao = db.dao

    // TODO: implement query for offline mode
    override val recommendedFlow: Flow<List<Album>> = flow { emit(emptyList()) }

    override suspend fun getAlbums(
        username: String, query: String, sort: AlbumSortOrder, order: SortOrder
    ): List<Album> {
        // TRY using cached data instead of downloaded song info if available
        val albumsList = mutableListOf<Album>()
        val dbAlbumsMap = HashMap<String, AlbumEntity>().apply {
            dao.getOfflineAlbums().forEach { ae ->
                put(ae.id, ae)
            }
        }

        dao.generateOfflineAlbums(username).forEach { dae ->
            albumsList.add(
                if (dbAlbumsMap.containsKey(dae.id)) {
                    // keep the offline art url, even when using album object from non-downloaded album table
                    (dbAlbumsMap[dae.id]?.copy(artUrl = dae.artUrl) ?: dae)
                } else { dae }.toAlbum()
            )
        }

        sortAlbums(albumsList, sort, order)

        return albumsList.toList()
    }

    @Throws(NullDataException::class)
    override suspend fun getAlbumFromId(id: String): Album = dao.generateOfflineAlbum(id)?.toAlbum()
        ?: throw NullDataException("getAlbumFromId OFFLINE DATA, cannot find album data in internal db")

    private fun sortAlbums(albumsList: MutableList<Album>, sort: AlbumSortOrder, order: SortOrder) {
        when(sort) {
            AlbumSortOrder.NAME -> albumsList.sortBy { sanitizeSortTerm(it.name) }
            AlbumSortOrder.YEAR -> albumsList.sortBy { it.year }
            AlbumSortOrder.ARTIST -> albumsList.sortBy { sanitizeSortTerm(it.artist.name.lowercase()) }
            AlbumSortOrder.RATING -> albumsList.sortBy { it.rating }
            AlbumSortOrder.AVERAGE_RATING -> albumsList.sortBy { it.averageRating }
        }

        if (order == SortOrder.DESC) { albumsList.reverse() }
    }
}
