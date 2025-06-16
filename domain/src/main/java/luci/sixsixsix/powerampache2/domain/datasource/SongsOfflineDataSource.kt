package luci.sixsixsix.powerampache2.domain.datasource

import luci.sixsixsix.powerampache2.domain.models.Song

interface SongsOfflineDataSource {
    suspend fun getRecentSongs(): List<Song>
}
