package luci.sixsixsix.powerampache2.domain.usecase
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

class DownloadSongUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(songId: String): Song? =
        songsRepository.downloadSongAndAddToDb(songId)
}
