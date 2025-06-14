package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import javax.inject.Inject

class MostPlayedArtistsUseCase @Inject constructor(
    private val artistsRepository: ArtistsRepository
) {
    suspend operator fun invoke() = artistsRepository.getMostPlayedArtists()
}
