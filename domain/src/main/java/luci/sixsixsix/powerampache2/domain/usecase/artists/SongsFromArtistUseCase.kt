package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import javax.inject.Inject

class SongsFromArtistUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {
    suspend operator fun invoke(artistId: String, fetchRemote: Boolean = true) =
        artistsRepository.getSongsFromArtist(artistId = artistId, fetchRemote = fetchRemote)
}
