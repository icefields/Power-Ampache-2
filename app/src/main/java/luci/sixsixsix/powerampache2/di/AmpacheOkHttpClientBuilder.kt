package luci.sixsixsix.powerampache2.di

import luci.sixsixsix.powerampache2.common.Constants.AMPACHE_USER_AGENT
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttp
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
class AmpacheOkHttpClientBuilder @Inject constructor(private val sharedPreferencesManager: SharedPreferencesManager) {

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
            chain.proceed(
                original.newBuilder()
                    .addHeader("User-Agent", AMPACHE_USER_AGENT)
                    .build()
            )
        }
    }
}
