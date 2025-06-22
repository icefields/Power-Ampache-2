package luci.sixsixsix.powerampache2.domain.datasource

import java.io.InputStream

interface SongsRemoteDataSource {
    @Throws(Exception::class)
    suspend fun downloadSong(songId: String, authKey: String): InputStream

    @Throws(Exception::class)
    suspend fun downloadArt(songId: String, authKey: String): InputStream
}
