package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import javax.inject.Inject

class LikeArtistUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {
    suspend operator fun invoke(artistId: String, like: Boolean = true) =
        artistsRepository.likeArtist(artistId, like)
}
