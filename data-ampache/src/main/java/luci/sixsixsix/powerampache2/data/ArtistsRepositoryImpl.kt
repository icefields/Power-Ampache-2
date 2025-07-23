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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.datasource.AlbumsDbDataSourceImpl
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.toArtist
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.di.LocalDataSource
import luci.sixsixsix.powerampache2.di.OfflineModeDataSource
import luci.sixsixsix.powerampache2.di.RemoteDataSource
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsOfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.plugin.info.InfoPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginArtistData
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
    @RemoteDataSource private val artistsRemoteDataSource: ArtistsRemoteDataSource,
    @LocalDataSource private val artistsDbDataSource: ArtistsDbDataSource,
    @LocalDataSource private val albumsDbDataSource: AlbumsDbDataSource,
    @LocalDataSource private val songsDbDataSource: SongsDbDataSource,
    @OfflineModeDataSource private val artistsOfflineDataSource: ArtistsOfflineModeDataSource,
    @OfflineModeDataSource private val songsOfflineDataSource: SongsOfflineDataSource,
    private val infoPluginDataSource: InfoPluginDataSource,
    api: MainNetwork,
    db: MusicDatabase,
    private val errorHandler: ErrorHandler
): BaseAmpacheRepository(api, db, errorHandler), ArtistsRepository {

    override val recommendedFlow: Flow<List<Artist>> = offlineModeFlow.flatMapLatest { isOffline ->
            if (isOffline)
                artistsOfflineDataSource.recommendedFlow
            else
                artistsDbDataSource.recommendedFlow
        }

    override suspend fun getArtist(
        artistId: String,
        fetchRemote: Boolean,
    ): Flow<Resource<Artist>> = flow {
        emit(Resource.Loading(true))

        if (isOfflineModeEnabled()) {
            artistsOfflineDataSource.getArtist(artistId)?.let { data ->
                emit(Resource.Success(data = data, networkData = data))
                emit(Resource.Loading(false))
                return@flow
            } ?: throw Exception("OFFLINE ARTIST does not exist")
        }

        artistsDbDataSource.getArtist(artistId)?.let { artist ->
            emit(Resource.Success(data = artist))
            if(!fetchRemote) {  // load cache only?
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val remoteArtist = artistsRemoteDataSource.getArtist(auth = authToken(), artistId = artistId)
        saveArtistsToDb(listOf(remoteArtist))
        artistsDbDataSource.getArtist(artistId)?.let { artist ->
            emit(Resource.Success(data = artist, networkData = remoteArtist ))
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtist()", e, this) }

    override suspend fun getArtists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))
        val cred = getCurrentCredentials()

        if (isOfflineModeEnabled()) {
            val generatedArtists = artistsOfflineDataSource.getArtists(cred.username, query)
            emit(Resource.Success(data = generatedArtists))
            emit(Resource.Loading(false))
            return@flow
        }

        if (offset == 0) {
            val localArtists = artistsDbDataSource.getArtists(query)
            val isDbEmpty = localArtists.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localArtists))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val fetchAlbumsWithArtist = Constants.config.fetchAlbumsWithArtist
        val remoteArtists = artistsRemoteDataSource.getArtists(
            authToken(),
            query = query,
            offset = offset,
            fetchAlbumsWithArtist = fetchAlbumsWithArtist
        ) { albums ->
            CoroutineScope(Dispatchers.IO).launch {
                L("aaaa saveAlbumsToDb ${albums.size}")
                albumsDbDataSource.saveAlbumsToDb(username = cred.username, serverUrl = cred.serverUrl, albums)
            }
        }

        saveArtistsToDb(remoteArtists)

        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = artistsDbDataSource.getArtists(query), networkData = remoteArtists))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    override suspend fun getRecommendedArtists(
        fetchRemote: Boolean,
        shouldGenerateIfEmpty: Boolean,
        baseArtistId: String,
        offset: Int
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))

        if (isOfflineModeEnabled()) {
            emit(Resource.Success(
                data = artistsOfflineDataSource.getRecommendedArtists(baseArtistId)))
            emit(Resource.Loading(false))
            return@flow
        }

        if (!fetchRemote) {
            // for this specific call, only emit initial db data if fetchRemote is false
            emit(Resource.Success(data = artistsDbDataSource.getRecommendedArtists(baseArtistId)))
            emit(Resource.Loading(false))
            return@flow
        }

        val remoteArtists = artistsRemoteDataSource
            .getRecommendedArtists(authToken(), baseArtistId)
            .ifEmpty {
                mutableListOf<Artist>().apply {
                        val generatedList = generateRecommendedArtistsFromGenres(baseArtistId)
                    if (shouldGenerateIfEmpty) {
                        addAll(if (generatedList.size > 11) generatedList.subList(0, 10) else generatedList)
                    } else {
                        // only add a couple of recommendations if shouldGenerateIfEmpty is false
                        addAll(if (generatedList.size > 3) generatedList.subList(0, 2) else generatedList)
                    }
                }
            }

        saveRecommendedArtistsToDb(baseArtistId = baseArtistId, artists = remoteArtists)
        emit(Resource.Success(data = artistsDbDataSource.getRecommendedArtists(baseArtistId), networkData = remoteArtists))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getRecommendedArtists()", e, this) }

    /**
     * Usually used when no recommendations are available from the backend.
     */
    private suspend fun generateRecommendedArtistsFromGenres(baseArtistId: String) =
        mutableListOf<Artist>().apply {
            artistsDbDataSource.getArtist(baseArtistId)?.genre?.let { genres ->
                for (genre in genres) {
                    addAll(artistsDbDataSource.getArtistsByGenre(genre.name))
                }
            }
            shuffled()
        }.toList()

    private suspend fun saveRecommendedArtistsToDb(baseArtistId: String, artists: List<Artist>) {
        val cred = getCurrentCredentials()
        artistsDbDataSource.saveRecommendedArtistsToDb(
            cred.username,
            serverUrl = cred.serverUrl,
            baseArtistId = baseArtistId,
            artists = artists
        )
    }

    override suspend fun getArtistsByGenre(
        genre: Genre,
        fetchRemote: Boolean,
        offset: Int
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
            val localArtists = artistsDbDataSource.getArtistsByGenre(genre.name)
            val isDbEmpty = localArtists.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localArtists))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val remoteArtists = artistsRemoteDataSource.getArtistsByGenre(authToken(),
            genreId = genre.id,
            offset = offset)
        saveArtistsToDb(remoteArtists)
        emit(Resource.Success(data = artistsDbDataSource.getArtistsByGenre(genre.name), networkData = remoteArtists))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

    override suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>> =
        like(id, like, MainNetwork.Type.artist)

    override suspend fun getMostPlayedArtists(): List<Artist> = if (isOfflineModeEnabled()) {
        generateMostPlayedArtists(artistsOfflineDataSource.getMostPlayedArtists(), songsOfflineDataSource.getRecentSongs())
    } else {
        generateMostPlayedArtists(artistsDbDataSource.getMostPlayedArtists(), songsDbDataSource.getRecentSongs())
    }

    private fun generateMostPlayedArtists(
        mostPlayedArtistsDb: List<Artist>,
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
            mostPlayedArtistsDb
        }

    override suspend fun getSongsFromArtist(artistId: String, fetchRemote: Boolean) = flow {
        emit(Resource.Loading(true))
        val isOfflineMode = isOfflineModeEnabled()
        val localSongs = getDbSongsFromArtist(artistId, isOfflineMode)
        if (!checkEmitCacheData(localSongs, fetchRemote, this) || isOfflineMode) {
            emit(Resource.Loading(false))
            return@flow
        }

        val songs = artistsRemoteDataSource.getSongsFromArtist(authToken(), artistId = artistId)
        cacheSongs(songs)
        emit(Resource.Success(data = getDbSongsFromArtist(artistId, isOfflineMode), networkData = songs))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getSongsFromArtist()", e, this) }

    private suspend fun getDbSongsFromArtist(artistId: String, isOfflineModeEnabled: Boolean ): List<Song> =
        if (isOfflineModeEnabled) {
            artistsOfflineDataSource.getSongsFromArtist(artistId)
        } else {
            artistsDbDataSource.getSongsFromArtist(artistId)
        }

    private suspend fun saveArtistsToDb(remoteArtists: List<Artist>) {
        val cred = getCurrentCredentials()
        artistsDbDataSource.saveArtistsToDb(
            username = cred.username,
            serverUrl = cred.serverUrl,
            artists = remoteArtists
        )
    }

    override suspend fun getPluginArtistData(
        artistId: String,
        artistMbId: String,
        artistName: String
    ): PluginArtistData? =
        infoPluginDataSource.getArtistInfo(
            artistId = artistId,
            musicBrainzId = artistMbId,
            artistName = artistName
        )
}
