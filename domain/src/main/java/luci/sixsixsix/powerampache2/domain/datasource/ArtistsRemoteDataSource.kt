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

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song

interface ArtistsRemoteDataSource {
    suspend fun getArtist(auth: String, artistId: String): Artist
    suspend fun getArtists(
        auth: String,
        query: String = "",
        offset: Int = 0,
        fetchAlbumsWithArtist: Boolean,
        albumsCallback: (List<Album>) -> Unit
    ): List<Artist>
    suspend fun getArtistsByGenre(auth: String, genreId: String, offset: Int = 0): List<Artist>
    suspend fun likeArtist(auth: String, id: String, like: Boolean): Boolean
    suspend fun getSongsFromArtist(auth: String, artistId: String): List<Song>
    suspend fun getRecommendedArtists(auth: String, baseArtistId: String, offset: Int = 0): List<Artist>
}
