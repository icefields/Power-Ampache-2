package luci.sixsixsix.powerampache2.domain.usecase.songs

import luci.sixsixsix.powerampache2.domain.SongsRepository
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(fetchRemote: Boolean = true, query: String = "", offset: Int = 0) =
        songsRepository.getSongs(fetchRemote = fetchRemote, query = query, offset = offset)
}
