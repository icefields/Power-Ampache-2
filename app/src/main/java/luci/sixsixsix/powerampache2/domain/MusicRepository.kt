package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.dto.SuccessResponse
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.User
import retrofit2.http.Query

interface MusicRepository {

    val sessionLiveData: LiveData<Session?>
    val userLiveData: LiveData<User?>
    suspend fun ping(): Resource<Pair<ServerInfo, Session?>>
    suspend fun autoLogin(): Flow<Resource<Session>>
    suspend fun logout(): Flow<Resource<Boolean>>
    suspend fun authorize(username:String, password:String, serverUrl: String, authToken: String, force: Boolean = true): Flow<Resource<Session>>
    suspend fun getUser(): User?
    suspend fun register(serverUrl: String, username: String, password: String, email: String, fullName: String? = null): Flow<Resource<Any>>
}
