package luci.sixsixsix.powerampache2.domain.datasource

import java.io.InputStream

interface NetworkDataSource {

    @Throws(Exception::class)
    suspend fun downloadSong(songId: String, authKey: String): InputStream
}