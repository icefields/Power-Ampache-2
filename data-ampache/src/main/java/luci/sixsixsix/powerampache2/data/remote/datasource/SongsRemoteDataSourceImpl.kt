package luci.sixsixsix.powerampache2.data.remote.datasource

import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.di.RemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsRemoteDataSource
import java.io.InputStream
import java.net.HttpURLConnection.HTTP_OK
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RemoteDataSource
class SongsRemoteDataSourceImpl @Inject constructor(private val api: MainNetwork): SongsRemoteDataSource {
    override suspend fun downloadSong(songId: String, authKey: String): InputStream {
        val response = api.downloadSong(authKey = authKey, songId = songId)
        if (response.code() == HTTP_OK) {
            // save file to disk and register in database
            return response.body()?.byteStream() ?: throw Exception("byteStream null}")
        } else {
            throw Exception("Cannot download received code ${response.code()}")
        }
    }
}
