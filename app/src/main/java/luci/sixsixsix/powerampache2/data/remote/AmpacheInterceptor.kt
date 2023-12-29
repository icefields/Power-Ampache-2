package luci.sixsixsix.powerampache2.data.remote

import kotlinx.coroutines.runBlocking
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmpacheInterceptor @Inject constructor(private val musicDatabase: MusicDatabase): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            var request = chain.request()
            val host = musicDatabase.dao.getCredentials()?.serverUrl?.toHttpUrlOrNull()
            host?.let { newHost ->
                try {
                    request.url.newBuilder()
                        .scheme(newHost.scheme)
                        .host(newHost.host)
                        .encodedPath("${newHost.encodedPath}${request.url.encodedPath}")
                        .encodedQuery(request.url.encodedQuery)
                        .build()
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                    null
                }?.let { newUrl ->
                    request = request.newBuilder()
                        .url(newUrl)
                        .build()
                }
            }
            L("INTERCEPTOR request.url ${request.url}")
            chain.proceed(request)
        }
    }
}
