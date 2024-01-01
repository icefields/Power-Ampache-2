package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent

interface MusicRepository {
    suspend fun ping(): Resource<Pair<ServerInfo, Session?>>
    suspend fun autoLogin(): Flow<Resource<Session>>
    suspend fun logout(): Flow<Resource<Boolean>>
    suspend fun authorize(username:String, password:String, serverUrl: String, authToken: String, force: Boolean = true): Flow<Resource<Session>>
    suspend fun getArtist(artistId: String, fetchRemote: Boolean = true): Flow<Resource<Artist>>
    suspend fun getArtists(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Artist>>>
    suspend fun getPlaylists(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Playlist>>>
}
