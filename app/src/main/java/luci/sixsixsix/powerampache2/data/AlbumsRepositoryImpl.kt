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
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toAlbum
import luci.sixsixsix.powerampache2.data.local.entities.toAlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSession
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Session
import javax.inject.Inject
import javax.inject.Singleton

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@Singleton
class AlbumsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val errorHandler: ErrorHandler
): AlbumsRepository {
    private val dao = db.dao

    private suspend fun getSession(): Session? = dao.getSession()?.toSession()
    private suspend fun getCredentials(): CredentialsEntity? = dao.getCredentials()

    override suspend fun getAlbums(
        fetchRemote: Boolean,
        query: String,
        offset: Int,
        limit: Int
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        L("getAlbums - repo getSongs offset $offset")

        if (offset == 0) {
            val localAlbums = dao.searchAlbum(query)
            val isDbEmpty = localAlbums.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localAlbums.map { it.toAlbum() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!//authorize2(false)
        val response = api.getAlbums(auth.auth, filter = query, offset = offset, limit = limit)
        response.error?.let { throw(MusicException(it.toError())) }
        val albums = response.albums!!.map { it.toAlbum() } // will throw exception if songs null
        L("albums from web ${albums.size}")

        if (query.isNullOrBlank() && offset == 0 && Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearAlbums()
        }
        dao.insertAlbums(albums.map { it.toAlbumEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchAlbum(query).map { it.toAlbum() }, networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbums()", e, this) }

    override suspend fun getAlbumsFromArtist(
        artistId: String,
        fetchRemote: Boolean
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        L("repo getAlbumsFromArtist $artistId")

        val localAlbums = dao.getAlbumsFromArtist(artistId).map { it.toAlbum() }
        val isDbEmpty = localAlbums.isEmpty()
        if (!isDbEmpty) {
            emit(Resource.Success(data = localAlbums))
        }
        val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
        if(shouldLoadCacheOnly) {
            emit(Resource.Loading(false))
            return@flow
        }

        val auth = getSession()!!//authorize2(false)
        val response = api.getAlbumsFromArtist(auth.auth, artistId = artistId)
        response.error?.let { throw(MusicException(it.toError())) }

        // some albums come from web with no artists id, or with artist id zero, add the id manually
        // so the database can find it (db is single source of truth)
        val albums = response.albums!!.map { albumDto -> albumDto.toAlbum() } // will throw exception if songs null

        L("albums from web ${albums.size}")

        dao.insertAlbums(albums.map { it.toAlbumEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        val dbUpdatedAlbums = dao.getAlbumsFromArtist(artistId).map { it.toAlbum() }
        emit(Resource.Success(data = dbUpdatedAlbums, networkData = albums))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbumsFromArtist()", e, this) }

    override suspend fun getAlbum(
        albumId: String,
        fetchRemote: Boolean,
    ): Flow<Resource<Album>> = flow {
        emit(Resource.Loading(true))

        dao.getAlbum(albumId)?.let { albumEntity ->
            emit(Resource.Success(data = albumEntity.toAlbum() ))
            if(!fetchRemote) {  // load cache only?
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!
        val response = api.getAlbumInfo(authKey = auth.auth, albumId = albumId)
        val album = response.toAlbum()  //will throw exception if artist null

//        if (CLEAR_TABLE_AFTER_FETCH) { dao.clearArtists() }

        dao.insertAlbums(listOf(album.toAlbumEntity()))
        // stick to the single source of truth pattern despite performance deterioration
        dao.getAlbum(albumId)?.let { albumEntity ->
            emit(Resource.Success(data = albumEntity.toAlbum(), networkData = album ))
        }

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbum()", e, this) }

    // --- HOME PAGE data ---

    private suspend fun getAlbumsStats(statFilter: MainNetwork.StatFilter): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.getAlbumsStats(
            auth.auth,
            username = getCredentials()?.username,
            filter = statFilter
        ).albums?.map { it.toAlbum() }?.let {
            emit(Resource.Success(data = it, networkData = it))
        }?:run {
            // TODO throw exception, without updating the UI error message snackbar! create a MusicException that ErrorHAndler will intercept?
            // throw Exception("error connecting or getting data")
            L.e("error connecting or getting data")
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAlbumsStats()", e, this) }

    override suspend fun getRecentAlbums() = getAlbumsStats(MainNetwork.StatFilter.recent)
    override suspend fun getNewestAlbums() = getAlbumsStats(MainNetwork.StatFilter.newest)
    override suspend fun getHighestAlbums() = getAlbumsStats(MainNetwork.StatFilter.highest)
    override suspend fun getFrequentAlbums() = getAlbumsStats(MainNetwork.StatFilter.frequent)
    override suspend fun getFlaggedAlbums() = getAlbumsStats(MainNetwork.StatFilter.flagged)
    override suspend fun getRandomAlbums() = getAlbumsStats(MainNetwork.StatFilter.random)

    override suspend fun getAlbumShareLink(albumId: String) = flow {
        emit(Resource.Loading(true))
        val response = api.createShare(
            getSession()!!.auth,
            id = albumId,
            type = MainNetwork.Type.album
        )
        response.error?.let { throw(MusicException(it.toError())) }
        response.publicUrl!!.apply {
            emit(Resource.Success(data = this, networkData = this))
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylistShareLink()", e, this) }

    private suspend fun rate(itemId: String, rating: Int, type: MainNetwork.Type): Flow<Resource<Any>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.rate(
            authKey = auth.auth,
            itemId = itemId,
            rating = rating,
            type = type).apply {
            error?.let { throw(MusicException(it.toError())) }
            if (success != null) {
                emit(Resource.Success(data = Any(), networkData = Any()))
            } else {
                throw Exception("error getting a response from FLAG/LIKE call")
            }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("likeSong()", e, this) }

    override suspend fun rateAlbum(albumId: String, rate: Int): Flow<Resource<Any>> = rate(albumId, rate, MainNetwork.Type.album)
}
