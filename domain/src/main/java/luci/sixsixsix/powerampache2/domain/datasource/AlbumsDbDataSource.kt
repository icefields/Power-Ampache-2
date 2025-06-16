package luci.sixsixsix.powerampache2.domain.datasource

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist

interface AlbumsDbDataSource {
    val recommendedFlow: Flow<List<Album>>

    suspend fun saveAlbumsToDb(username: String, serverUrl: String, albums: List<Album>)
}
