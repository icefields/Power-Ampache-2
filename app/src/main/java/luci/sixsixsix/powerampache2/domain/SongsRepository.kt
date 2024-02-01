package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Song

interface SongsRepository {
    val offlineSongsLiveData: LiveData<List<Song>>
    suspend fun getSongs(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Song>>>
    suspend fun getSongsFromAlbum(albumId: String, fetchRemote: Boolean = true): Flow<Resource<List<Song>>>
    suspend fun getSongsFromPlaylist(playlistId: String, fetchRemote: Boolean = true): Flow<Resource<List<Song>>>
    suspend fun getRecentSongs(): Flow<Resource<List<Song>>>
    suspend fun getNewestSongs(): Flow<Resource<List<Song>>>
    suspend fun getHighestSongs(): Flow<Resource<List<Song>>>
    suspend fun getFrequentSongs(): Flow<Resource<List<Song>>>
    suspend fun getFlaggedSongs(): Flow<Resource<List<Song>>>
    suspend fun getRandomSongs(): Flow<Resource<List<Song>>>
    suspend fun getSongUri(song: Song): String
    suspend fun downloadSong(song: Song): Flow<Resource<Any>>
    suspend fun downloadSongs(songs: List<Song>)
    suspend fun deleteDownloadedSong(song: Song): Flow<Resource<Any>>
    suspend fun isSongAvailableOffline(song: Song): Boolean
    suspend fun getSongShareLink(song: Song): Flow<Resource<String>>
}
