package luci.sixsixsix.powerampache2.data.local.datasource

import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.di.LocalDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsDbDataSource
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

@LocalDataSource
@Singleton
class SongsDbDataSourceImpl @Inject constructor(
    db: MusicDatabase,
): SongsDbDataSource {
    private val dao = db.dao

    override suspend fun getRecentSongs(): List<Song> =
        dao.getSongHistory().map { it.toSong() }
}
