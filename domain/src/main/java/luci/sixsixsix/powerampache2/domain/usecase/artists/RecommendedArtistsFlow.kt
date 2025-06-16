package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendedArtistsFlow @Inject constructor(
    private val artistsRepository: ArtistsRepository
) {
    operator fun invoke() = artistsRepository.recommendedFlow
}
