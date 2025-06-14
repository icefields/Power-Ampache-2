package luci.sixsixsix.powerampache2.domain.usecase.albums

import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendedAlbumsFlow @Inject constructor(
    private val albumsRepository: AlbumsRepository
) {
    operator fun invoke() = albumsRepository.recommendedFlow
}
