package luci.sixsixsix.powerampache2.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.AuthDto
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toSession
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
import retrofit2.HttpException
import java.io.IOException
import java.lang.NullPointerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val dateMapper: DateMapper,
): MusicRepository {
    private var session: Session? = null

    override suspend fun authorize(force: Boolean): Resource<Session> =
        try {
            if (session == null || session!!.isTokenExpired() || force) {
                val auth = api.authorize()
                auth.error?.let {
                    throw(MusicException(it.toError()))
                }

                auth.auth?.let {
                    session = auth.toSession(dateMapper)
                }
            }

            session?.let {
                Log.d("aaaa", it.sessionExpire.toString())
                Resource.Success(it)
            } ?: run {
                Resource.Error(
                    message = "cannot load data",
                    exception = NullPointerException("Session still null after init")
                )
            }
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }

    override suspend fun getSongs(fetchRemote: Boolean, query: String): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))

        authorize(false)

        val remoteSongs = try {
            val resp1 = api.getAllSongs(session!!.auth, filter = query)
            resp1.error?.let {
                Log.d("aaaa", "ERROR "+it.toString())
                throw(MusicException(it.toError()))
            }

            val response = resp1.songs?.map { it.toSong() }
            Log.d("aaaa", "SONGS "+response?.size)
            response
        } catch (e: IOException) {
            Log.d("aaaa", e.toString())
            emit(Resource.Error(message = "cannot load data", exception = e))
            null
        } catch (e: HttpException) {
            Log.d("aaaa", e.toString())
            emit(Resource.Error(message = "cannot load data", exception = e))
            null
        } catch (e: Exception) {
            Log.d("aaaa", "exc " + e.toString())
            emit(Resource.Error(message = "cannot load data", exception = e))
            null
        }

        remoteSongs?.let { songs ->
            emit(Resource.Success(songs))
        } ?: run {
            emit(Resource.Error(message = "song list is null", exception = NullPointerException("song list is null")))
        }
        emit(Resource.Loading(false))
    }
}
