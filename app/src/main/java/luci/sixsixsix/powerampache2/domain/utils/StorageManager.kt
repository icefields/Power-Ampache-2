package luci.sixsixsix.powerampache2.domain.utils

import luci.sixsixsix.powerampache2.domain.models.Song
import java.io.InputStream

interface StorageManager {
    @Throws(Exception::class) suspend fun saveSong(song: Song, inputStream: InputStream): String
    @Throws(Exception::class) suspend fun deleteSong(song: Song): Boolean
    @Throws(Exception::class) suspend fun deleteAll(): Boolean
}
