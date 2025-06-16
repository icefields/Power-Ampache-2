package luci.sixsixsix.powerampache2.domain.usecase.artists

import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import javax.inject.Inject

class RecommendedArtistsUseCase @Inject constructor(
    private val artistsRepository: ArtistsRepository,
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(
        baseArtistId: String,
        fetchRemote: Boolean = true,
        offset: Int = 0
    ) =
        artistsRepository.getRecommendedArtists(
            fetchRemote = fetchRemote,
            shouldGenerateIfEmpty = (musicRepository.serverInfoStateFlow.value.isNextcloud == true),
            baseArtistId = baseArtistId,
            offset = offset
        )
}
