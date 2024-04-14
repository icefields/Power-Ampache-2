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
package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.SongEntity
import luci.sixsixsix.powerampache2.data.local.entities.toArtist
import luci.sixsixsix.powerampache2.data.local.entities.toArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@Singleton
class ArtistsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val errorHandler: ErrorHandler
): BaseAmpacheRepository(api, db, errorHandler), ArtistsRepository {
    override suspend fun getArtist(
        artistId: String,
        fetchRemote: Boolean,
    ): Flow<Resource<Artist>> = flow {
        emit(Resource.Loading(true))

        if (isOfflineModeEnabled()) {
            dao.generateOfflineArtist(artistId)?.let { artistEntity ->
                L(artistEntity.name)
                val data = artistEntity.toArtist()
                emit(Resource.Success(data = data, networkData = data))
                emit(Resource.Loading(false))
                return@flow
            } ?: throw Exception("OFFLINE ARTIST does not exist")
        }

        dao.getArtist(artistId)?.let { artistEntity ->
            emit(Resource.Success(data = artistEntity.toArtist() ))
            if(!fetchRemote) {  // load cache only?
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val cred = getCurrentCredentials()
        val response = api.getArtistInfo(authKey = authToken(), artistId = artistId)
        val artist = response.toArtist()  //will throw exception if artist null

//        if (CLEAR_TABLE_AFTER_FETCH) { dao.clearArtists() }

        dao.insertArtists(listOf(artist.toArtistEntity(username = cred.username, serverUrl = cred.serverUrl)))
        // stick to the single source of truth pattern despite performance deterioration
        dao.getArtist(artistId)?.let { artistEntity ->
            emit(Resource.Success(data = artistEntity.toArtist(), networkData = artist ))
        }

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    override suspend fun getArtists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))

        if (isOfflineModeEnabled()) {
            val generatedArtists = dao.generateOfflineArtists(getUsername()!!) //let it go to exception if no username
            //val offlineAlbums = dao.getOfflineAlbums()
            emit(Resource.Success(data = generatedArtists.map { it.toArtist() }))
            emit(Resource.Loading(false))
            return@flow
        }

        if (offset == 0) {
            val localArtists = dao.searchArtist(query)
            val isDbEmpty = localArtists.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localArtists.map { it.toArtist() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val response = api.getArtists(authToken(), filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val artists = response.artists!!.map { it.toArtist() } //will throw exception if artist null

        if (query.isNullOrBlank() && offset == 0 && Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearArtists()
        }

        val cred = getCurrentCredentials()
        dao.insertArtists(artists.map { it.toArtistEntity(username = cred.username, serverUrl = cred.serverUrl) })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchArtist(query).map { it.toArtist() }, networkData = artists))

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    override suspend fun getArtistsByGenre(
        genre: Genre,
        fetchRemote: Boolean,
        offset: Int
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
            val localArtists = dao.searchArtistByGenre(genre.name)
            val isDbEmpty = localArtists.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localArtists.map { it.toArtist() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val response = api.getArtistsByGenre(authToken(), filter = genre.id, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val artists = response.artists!!.map { it.toArtist() } //will throw exception if artist null

        if (Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearArtists()
        }

        val cred = getCurrentCredentials()
        dao.insertArtists(artists.map { it.toArtistEntity(username = cred.username, serverUrl = cred.serverUrl) })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchArtistByGenre(genre.name).map { it.toArtist() }, networkData = artists))

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    override suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.artist)

    override suspend fun getMostPlayedArtists(): List<Artist> = if (isOfflineModeEnabled()) {
        generateMostPlayedArtists(dao.getMostPlayedOfflineArtists(), dao.getOfflineSongHistory().map { it.toSong() })
    } else {
        generateMostPlayedArtists(dao.getMostPlayedArtists(), dao.getSongHistory().map { it.toSong() })
    }

    private fun generateMostPlayedArtists(
        mostPlayedArtistsDb: List<ArtistEntity>,
        mostPlayedSongsDb: List<Song>
    ) = if (mostPlayedArtistsDb.isEmpty()) {
            HashMap<String, Artist>().apply {
                mostPlayedSongsDb.forEach {
                    put(it.artist.id, Artist(
                        id = it.artist.id,
                        name = it.artist.name,
                        artUrl = it.imageUrl,
                        genre = it.genre
                    ))
                }
            }.values.toList()
        } else {
            mostPlayedArtistsDb.map { it.toArtist() }
        }
}

