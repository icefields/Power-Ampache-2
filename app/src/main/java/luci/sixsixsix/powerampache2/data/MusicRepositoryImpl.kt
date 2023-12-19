package luci.sixsixsix.powerampache2.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.AmpacheBaseResponse
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toServerInfo
import luci.sixsixsix.powerampache2.data.remote.dto.toSession
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song
import retrofit2.HttpException
import java.io.IOException
import java.lang.NullPointerException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val dateMapper: DateMapper,
): MusicRepository {
    private var session: Session? = null
    private var serverInfo: ServerInfo? = null

    override suspend fun ping(): Resource<Pair<ServerInfo, Session?>> =
        try {
            val resp = api.ping(session?.let { it.auth } ?: run { "" })
            Log.d("aaaa", "PING  $resp")
            try {
                resp.toSession(dateMapper)
            } catch (e: Exception) {
                null
            }?.let {se ->
                se.auth?.let {
                    session = se
                }
            }
            serverInfo = resp.toServerInfo()
            Resource.Success(Pair(serverInfo!!, session))
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }


    override suspend fun authorize(force: Boolean): Resource<Session> =
        try {
            Resource.Success(authorize2(force))
        } catch (e: IOException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: HttpException) {
            Resource.Error(message = "cannot load data", exception = e)
        } catch (e: MusicException) {
            Resource.Error(message = e.musicError.toString(), exception = e)
        } catch (e: Exception) {
            Resource.Error(message = "cannot load data", exception = e)
        }

    @Throws(Exception::class)
    private suspend fun authorize2(force: Boolean): Session {
        ping()
        if (session == null || session!!.isTokenExpired() || force) {
            val auth = api.authorize()
            auth.error?.let {
                throw (MusicException(it.toError()))
            }
            auth.auth?.let {
                session = auth.toSession(dateMapper)
                Log.d("aaaa", "token null or expired ${session?.sessionExpire} ${session?.isTokenExpired()}")
            }
        }
        return session!! // will throw exception if session null
    }


    override suspend fun getSongs(fetchRemote: Boolean, query: String): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading(true))
        val auth = authorize2(true)
        val resp1 = api.getAllSongs(auth.auth, filter = query)
        resp1.error?.let { throw(MusicException(it.toError())) }
        emit(Resource.Success(resp1.songs!!.map { it.toSong() })) // will throw exception if songs null
        emit(Resource.Loading(false))
    }.catch { e ->
        when(e) {
            is IOException ->
                emit(Resource.Error(message = "cannot load data IOException $e", exception = e))
            is HttpException ->
                emit(Resource.Error(message = "cannot load data HttpException $e", exception = e))
            is MusicException ->
                emit(Resource.Error(message = e.musicError.toString(), exception = e))
            else ->
                emit(Resource.Error(message = "generic exception $e", exception = e))
        }
    }
}
