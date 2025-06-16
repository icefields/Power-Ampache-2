package luci.sixsixsix.powerampache2.data.local.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toAlbum
import luci.sixsixsix.powerampache2.data.local.entities.toAlbumEntity
import luci.sixsixsix.powerampache2.di.LocalDataSource
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsDbDataSource
import luci.sixsixsix.powerampache2.domain.models.Album
import javax.inject.Inject
import javax.inject.Singleton

@LocalDataSource
@Singleton
class AlbumsDbDataSourceImpl @Inject constructor(db: MusicDatabase): AlbumsDbDataSource {
    private val dao = db.dao

    override val recommendedFlow: Flow<List<Album>>
        get() = dao.getRecommendedAlbums().mapNotNull { list -> list.map { it.toAlbum() } }

    override suspend fun saveAlbumsToDb(username: String, serverUrl: String, albums: List<Album>) {
        dao.insertAlbums(albums.map { it.toAlbumEntity(username = username, serverUrl = serverUrl) })
    }
}
