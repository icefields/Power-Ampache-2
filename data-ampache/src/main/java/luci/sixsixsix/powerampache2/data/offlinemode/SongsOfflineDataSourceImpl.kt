package luci.sixsixsix.powerampache2.data.offlinemode

import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.di.OfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

@OfflineModeDataSource
class SongsOfflineDataSourceImpl @Inject constructor(
    db: MusicDatabase,
): SongsOfflineDataSource {
    private val dao = db.dao

    override suspend fun getRecentSongs(): List<Song> =
        dao.getOfflineSongHistory().map { it.toSong() }
}
