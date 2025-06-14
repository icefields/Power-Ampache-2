package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import javax.inject.Inject

class RecommendedArtistsUseCase @Inject constructor(
    private val artistsRepository: ArtistsRepository
) {
    suspend operator fun invoke(baseArtistId: String, fetchRemote: Boolean = true, offset: Int = 0) =
        artistsRepository.getRecommendedArtists(fetchRemote, baseArtistId, offset)
}
