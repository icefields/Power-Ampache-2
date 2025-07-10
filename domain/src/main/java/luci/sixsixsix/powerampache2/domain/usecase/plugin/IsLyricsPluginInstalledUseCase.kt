package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.SongsRepository
import javax.inject.Inject

class IsLyricsPluginInstalledUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    operator fun invoke() =
        songsRepository.isLyricsPluginInstalled()
}
