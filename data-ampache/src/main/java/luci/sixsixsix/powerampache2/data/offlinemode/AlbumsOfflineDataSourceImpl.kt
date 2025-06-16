package luci.sixsixsix.powerampache2.data.offlinemode

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.di.OfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.models.Album
import javax.inject.Inject
import javax.inject.Singleton

@OfflineModeDataSource
@Singleton
class AlbumsOfflineDataSourceImpl @Inject constructor(db: MusicDatabase): AlbumsOfflineDataSource {
    private val dao = db.dao
    // TODO: implement query for offline mode
    override val recommendedFlow: Flow<List<Album>> = flow { emit(emptyList())}
}
