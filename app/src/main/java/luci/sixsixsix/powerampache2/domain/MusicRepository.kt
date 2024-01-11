package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent

interface MusicRepository {

    val sessionLiveData: LiveData<Session?>
    suspend fun userLiveData(): LiveData<User?>
    suspend fun ping(): Resource<Pair<ServerInfo, Session?>>
    suspend fun autoLogin(): Flow<Resource<Session>>
    suspend fun logout(): Flow<Resource<Boolean>>
    suspend fun authorize(username:String, password:String, serverUrl: String, authToken: String, force: Boolean = true): Flow<Resource<Session>>
}
