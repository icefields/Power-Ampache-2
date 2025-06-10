package luci.sixsixsix.powerampache2.domain.usecase.songs

import luci.sixsixsix.powerampache2.domain.SongsRepository
import javax.inject.Inject

class SongFromIdUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(songId: String) =
        songsRepository.getSongFromId(songId, true)
}
