package luci.sixsixsix.powerampache2

import android.app.Application.MODE_PRIVATE
import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import luci.sixsixsix.powerampache2.data.remote.AmpacheOkHttpClientBuilder
import luci.sixsixsix.powerampache2.domain.common.Constants.TIMEOUT_CONNECTION_S
import luci.sixsixsix.powerampache2.domain.common.Constants.TIMEOUT_READ_S
import luci.sixsixsix.powerampache2.domain.common.Constants.TIMEOUT_WRITE_S
import luci.sixsixsix.powerampache2.domain.utils.ImageLoaderProvider
import java.util.concurrent.TimeUnit

class ImageLoaderProviderImpl constructor(
    @ApplicationContext private val context: Context,
    private val imageLoaderOkHttpClient: AmpacheOkHttpClientBuilder
): ImageLoaderProvider {
    override fun getImageLoaderBuilder(): ImageLoader.Builder = ImageLoader(context).newBuilder()
        .crossfade(200)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .okHttpClient(
            imageLoaderOkHttpClient(true)
                //.retryOnConnectionFailure(true)
                .connectTimeout(TIMEOUT_CONNECTION_S, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ_S, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE_S, TimeUnit.SECONDS)
                .build()
        )
        //.respectCacheHeaders(false)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.08)
                .strongReferencesEnabled(false)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .maxSizePercent(0.10)
                .directory(context.getDir("paimages", MODE_PRIVATE))
                .build()

        }
}
