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
package luci.sixsixsix.powerampache2.domain.plugin.auto

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song

interface AutoPluginDataSource {
    fun isAutoPluginInstalled(): Boolean
    suspend fun sendQueueToAuto(queue: List<Song>)
    suspend fun sendAlbumsToAuto(albums: List<Album>)
    suspend fun sendArtistsToAuto(artists: List<Artist>)
    suspend fun sendFavouriteAlbumsToAuto(albums: List<Album>)
    suspend fun sendLatestAlbumsToAuto(albums: List<Album>)
    suspend fun sendRecentAlbumsToAuto(albums: List<Album>)
    suspend fun sendHighestAlbumsToAuto(albums: List<Album>)
}
