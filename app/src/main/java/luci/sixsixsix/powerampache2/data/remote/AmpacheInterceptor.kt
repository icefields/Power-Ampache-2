package luci.sixsixsix.powerampache2.data.remote

import kotlinx.coroutines.runBlocking
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.domain.errors.MusicError
import luci.sixsixsix.powerampache2.domain.errors.ServerUrlNotInitializedException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

const val EMPTY_URL_PLACEHOLDER = "luci.sixsixsix.powerampache2.data.remote.EMPTY_URL_PLACEHOLDER"

@Singleton
class AmpacheInterceptor @Inject constructor(private val musicDatabase: MusicDatabase) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        var request = chain.request()
        var hostStr = musicDatabase.dao.getCredentials()?.serverUrl

        if (!hostStr.isNullOrBlank()) {
            if (hostStr?.contains("/server") == false) {
                hostStr += "/server"
            }
            if (hostStr?.contains("http://") == false &&
                hostStr?.contains("https://") == false) {
                hostStr = "https://$hostStr"
            }

            val host = hostStr?.toHttpUrlOrNull()
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
            L(request.url)
            chain.proceed(request)
        } else {
            Response.Builder()
                .body("{ \"error\" : $errorStr}".toResponseBody(null))
                .protocol(Protocol.HTTP_2)
                .message("{ \"error\" : $errorStr}")
                .request(chain.request())
                .code(200)
                .build()
        }
    }
}

private val errorStr = ServerUrlNotInitializedException().musicError.toJson()
