package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.SongsRepository
import javax.inject.Inject

class LyricsUrlFromGeniusUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(songTitle: String, albumTitle: String, artistName: String) =
        songsRepository.lyricsUrlFromGenius(songTitle, albumTitle, artistName)
}
