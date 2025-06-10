package luci.sixsixsix.powerampache2.domain.usecase.songs

import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

class SongShareLinkUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(song: Song) =
        songsRepository.getSongShareLink(song)
}
