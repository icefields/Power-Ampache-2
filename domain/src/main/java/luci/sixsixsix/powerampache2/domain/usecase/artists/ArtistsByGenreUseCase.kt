
package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.models.Genre
import javax.inject.Inject

class ArtistsByGenreUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {
    suspend operator fun invoke(genre: Genre) = artistsRepository.getArtistsByGenre(genre)
}
