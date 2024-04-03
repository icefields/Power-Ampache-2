/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.data.remote

import kotlinx.coroutines.runBlocking
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
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

@Singleton
class AmpacheInterceptor @Inject constructor(private val musicDatabase: MusicDatabase) : Interceptor {

    private fun isErrorReportUrl(url: String) = url == BuildConfig.URL_ERROR_LOG

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        var request = chain.request()

        // if reporting an error no need to manipulate the url
        if (isErrorReportUrl(request.url.toString())) {
            return@runBlocking chain.proceed(request)
        }

        val baseUrl = musicDatabase.dao.getCredentials()?.serverUrl

        if (!baseUrl.isNullOrBlank()) {
            val host = MainNetwork.buildServerUrl(baseUrl)?.toHttpUrlOrNull()
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
            try {
                chain.proceed(request)
            } catch (e: Exception) {
                Response.Builder()
                    .body("{ \"exception\" : \"${e.localizedMessage}\" }".toResponseBody(null))
                    .protocol(Protocol.HTTP_2)
                    .message("{ \"exception\" : \"${e.localizedMessage}\" }")
                    .request(chain.request())
                    .code(404)
                    .build()
            }
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
