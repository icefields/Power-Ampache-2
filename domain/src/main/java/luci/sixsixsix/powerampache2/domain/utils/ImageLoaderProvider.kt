package luci.sixsixsix.powerampache2.domain.utils

import coil.ImageLoader

interface ImageLoaderProvider {
    fun getImageLoaderBuilder(): ImageLoader.Builder
}
