package luci.sixsixsix.powerampache2.domain.datasource

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings

interface DbDataSource {
    val settingsFlow: Flow<LocalSettings>
    suspend fun getSongById(songId: String): Song?
    suspend fun clearSession()
    suspend fun getUsername(): String?
    suspend fun getServerUrl(): String?
    suspend fun addDownloadedSong(song: Song, filepath: String)

}
