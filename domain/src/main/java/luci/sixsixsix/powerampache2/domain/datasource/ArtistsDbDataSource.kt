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
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song

interface ArtistsDbDataSource {
    val recommendedFlow: Flow<List<Artist>>

    suspend fun getArtist(artistId: String): Artist?
    suspend fun getArtists(query: String = ""): List<Artist>
    suspend fun getArtistsByGenre(genreName: String): List<Artist>
    suspend fun likeArtist(id: String, like: Boolean): Boolean
    suspend fun getMostPlayedArtists(): List<Artist>
    suspend fun getSongsFromArtist(artistId: String): List<Song>
    suspend fun getRecommendedArtists(baseArtistId: String): List<Artist>

    suspend fun saveArtistsToDb(username: String, serverUrl: String, artists: List<Artist>)
    suspend fun saveRecommendedArtistsToDb(
        username: String,
        serverUrl: String,
        baseArtistId: String,
        artists: List<Artist>
    )
}
