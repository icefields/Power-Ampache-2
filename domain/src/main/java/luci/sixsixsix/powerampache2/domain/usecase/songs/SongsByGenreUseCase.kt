package luci.sixsixsix.powerampache2.domain.usecase.songs

import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Genre
import javax.inject.Inject

class SongsByGenreUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(genre: Genre) =
        songsRepository.getSongsByGenre(genre)
}
