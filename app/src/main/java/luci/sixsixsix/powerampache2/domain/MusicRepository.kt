package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Session
import luci.sixsixsix.powerampache2.domain.models.Song

interface MusicRepository {
    suspend fun authorize(force: Boolean = true): Resource<Session>
    suspend fun getSongs(fetchRemote: Boolean = true, query: String = ""): Flow<Resource<List<Song>>>
}
