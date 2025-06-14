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
import kotlinx.coroutines.flow.mapNotNull
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toArtist
import luci.sixsixsix.powerampache2.data.local.entities.toArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toRecommendedArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.di.LocalDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsDbDataSource
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

@LocalDataSource
@Singleton
class ArtistsDbDataSourceImpl @Inject constructor(
    db: MusicDatabase,
): ArtistsDbDataSource {
    private val dao = db.dao

    override val recommendedFlow: Flow<List<Artist>> =
        dao.getRecommendedArtists().mapNotNull { list -> list.map { it.toArtist() } }

    override suspend fun getArtist(artistId: String): Artist {
        TODO("Not yet implemented")
    }

    override suspend fun getArtists(query: String): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistsByGenre(genreId: Genre): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun likeArtist(id: String, like: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getMostPlayedArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun getSongsFromArtist(artistId: String): List<Song> =
        dao.getSongsFromArtist(artistId).map { it.toSong() }

    override suspend fun getRecommendedArtists(baseArtistId: String): List<Artist> =
        dao.getRecommendedArtists(baseArtistId).map { it.toArtist() }

    override suspend fun saveArtistsToDb(username: String, serverUrl: String, artists: List<Artist>) {
        dao.insertArtists(artists.map { it.toArtistEntity(username = username, serverUrl = serverUrl) })
    }

    override suspend fun saveRecommendedArtistsToDb(
        username: String,
        serverUrl: String,
        baseArtistId: String,
        artists: List<Artist>
    ) {
        saveArtistsToDb(username, serverUrl = serverUrl, artists = artists)
        dao.insertRecommendedArtists(
            artists.map {
                it.toRecommendedArtistEntity(
                    username = username,
                    serverUrl = serverUrl,
                    baseArtistId = baseArtistId
                )
            }
        )
    }
}
