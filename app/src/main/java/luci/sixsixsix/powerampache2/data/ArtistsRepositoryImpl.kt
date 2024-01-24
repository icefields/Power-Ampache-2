package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toArtist
import luci.sixsixsix.powerampache2.data.local.entities.toArtistEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Artist
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
class ArtistsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val musicRepository: MusicRepository,
    private val errorHandler: ErrorHandler
): ArtistsRepository {
    private val dao = db.dao

    private fun getSession(): Session? = musicRepository.sessionLiveData.value

    override suspend fun getArtist(
        artistId: String,
        fetchRemote: Boolean,
    ): Flow<Resource<Artist>> = flow {
        emit(Resource.Loading(true))

        dao.getArtist(artistId)?.let { artistEntity ->
            emit(Resource.Success(data = artistEntity.toArtist() ))
            if(!fetchRemote) {  // load cache only?
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!
        val response = api.getArtistInfo(authKey = auth.auth, artistId = artistId)
        val artist = response.toArtist()  //will throw exception if artist null

//        if (CLEAR_TABLE_AFTER_FETCH) { dao.clearArtists() }

        dao.insertArtists(listOf(artist.toArtistEntity()))
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

        val auth = getSession()!!
        val response = api.getArtists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val artists = response.artists!!.map { it.toArtist() } //will throw exception if artist null

        if (query.isNullOrBlank() && offset == 0 && Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearArtists()
        }
        dao.insertArtists(artists.map { it.toArtistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchArtist(query).map { it.toArtist() }, networkData = artists))

        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getArtists()", e, this) }

}