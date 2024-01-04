package luci.sixsixsix.powerampache2

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PowerAmpache2Application : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader = ImageLoader(this).newBuilder()
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.2)
                .strongReferencesEnabled(true)
                .build()
        }
        .diskCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            DiskCache.Builder()
                .maxSizePercent(0.06)
                .directory(cacheDir)
                .build()

        }
        //.logger(DebugLogger()) // TODO change in production
        .build()

}
