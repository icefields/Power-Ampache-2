package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import javax.inject.Inject

class ArtistsUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {
    suspend operator fun invoke(query: String, offset: Int = 0, fetchRemote: Boolean = true) =
        artistsRepository.getArtists(query = query, fetchRemote = fetchRemote, offset = offset)
}
