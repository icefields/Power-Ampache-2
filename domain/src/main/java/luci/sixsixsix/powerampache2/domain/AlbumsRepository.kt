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
package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.SortOrder

interface AlbumsRepository {
    val recentlyPlayedAlbumsFlow: Flow<List<Album>>
    val flaggedAlbumsFlow: Flow<List<Album>>
    val highestRatedAlbumsFlow: Flow<List<Album>>
    val randomAlbumsFlow: Flow<List<Album>>
    val frequentAlbumsFlow: Flow<List<Album>>
    val recommendedFlow: Flow<List<Album>>

    suspend fun getAlbums(fetchRemote: Boolean = true, query: String = "", offset: Int = 0, limit: Int = 0, sort: AlbumSortOrder = AlbumSortOrder.NAME, order: SortOrder = SortOrder.ASC): Flow<Resource<List<Album>>>
    suspend fun getAlbumsFromArtist(artistId: String, fetchRemote: Boolean = true): Flow<Resource<List<Album>>>
    suspend fun getRecentAlbums(): Flow<Resource<List<Album>>>
    suspend fun getNewestAlbums(): Flow<Resource<List<Album>>>
    suspend fun getHighestAlbums(): Flow<Resource<List<Album>>>
    suspend fun getFrequentAlbums(): Flow<Resource<List<Album>>>
    suspend fun getFlaggedAlbums(): Flow<Resource<List<Album>>>
    suspend fun getRandomAlbums(fetchRemote: Boolean = true): Flow<Resource<List<Album>>>
    suspend fun getAlbum(albumId: String, fetchRemote: Boolean): Flow<Resource<Album>>
    suspend fun getAlbum(id: String): Flow<Album>
    suspend fun getAlbumShareLink(albumId: String): Flow<Resource<String>>
    suspend fun rateAlbum(albumId: String, rate: Int): Flow<Resource<Any>>
    suspend fun likeAlbum(id: String, like: Boolean): Flow<Resource<Any>>
}
