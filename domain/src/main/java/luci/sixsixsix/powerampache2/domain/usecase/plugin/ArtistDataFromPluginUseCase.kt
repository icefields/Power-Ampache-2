package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.models.Artist
import javax.inject.Inject

class ArtistDataFromPluginUseCase @Inject constructor(
    private val artistsRepository: ArtistsRepository
) {
    suspend operator fun invoke(artist: Artist) =
        artistsRepository.getPluginArtistData(
            artistId = artist.id,
            artistMbId = "",
            artistName = artist.name
        )
}
