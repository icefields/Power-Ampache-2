package luci.sixsixsix.powerampache2.domain.datasource

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.domain.models.Album

interface AlbumsDbDataSource {
    val recommendedFlow: Flow<List<Album>>
}