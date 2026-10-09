package luci.sixsixsix.powerampache2.domain.utils

import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import luci.sixsixsix.powerampache2.domain.models.Song
import java.io.File
import java.io.InputStream

interface StorageManager {
    @Throws(Exception::class) suspend fun saveSong(song: Song, inputStream: InputStream): String
    @Throws(Exception::class) suspend fun saveImage(song: Song, inputStream: InputStream): String
    @Throws(Exception::class) suspend fun saveEpisode(episode: PodcastEpisode, inputStream: InputStream): String
    @Throws(Exception::class) suspend fun saveEpisodeImage(episode: PodcastEpisode, inputStream: InputStream): String
    /** path is the value returned by saveEpisode: an absolute path or a SAF content uri */
    @Throws(Exception::class) suspend fun deleteEpisodeFile(path: String): Boolean
    @Throws(Exception::class) suspend fun deleteSong(song: Song): Boolean
    @Throws(Exception::class) suspend fun deleteAll(): Boolean
    @Throws(Exception::class) suspend fun getAllSongsFromInternalStorages(): List<File>
}
