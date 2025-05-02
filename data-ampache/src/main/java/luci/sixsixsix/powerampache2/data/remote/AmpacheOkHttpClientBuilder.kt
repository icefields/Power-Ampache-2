/**
 * Copyright (C) 2025  Antonio Tari
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

import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Base OkHttp Builder for Ampache, with options to ignore certificates (if defined in settings)
 * and add simple interceptor for the header, if needed.
 */
class AmpacheOkHttpClientBuilder @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val configProvider: ConfigProvider
) {

    operator fun invoke(addDefaultHeaderInterceptor: Boolean = false) = OkHttpClient.Builder().apply {
        if (sharedPreferencesManager.isAllowAllCertificates) {
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory // Create an ssl socket factory with our all-trusting manager
                if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                    sslSocketFactory(sslSocketFactory, trustAllCerts.first() as X509TrustManager)
                    hostnameVerifier { _, _ -> true }
                }
            } catch (e: Exception) {
                e.printStackTrace() // TODO error handler here
            }
        }

        if (addDefaultHeaderInterceptor) addInterceptor(getSimpleHeaderInterceptor())
        retryOnConnectionFailure(true)
    }

    /**
     * This will add a simple header with the app's custom user agent
     * This is only needed if the interceptor is not added already in the builder.
     */
    private fun getSimpleHeaderInterceptor() = Interceptor { chain ->
        chain.request().let { original ->
            L.w(original.url)
            chain.proceed(
                original.newBuilder()
                    .addHeader("User-Agent", configProvider.AMPACHE_USER_AGENT)
                    .build()
            )
        }
    }
}
