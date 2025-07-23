package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.PluginRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import javax.inject.Inject

class IsLyricsPluginInstalledUseCase @Inject constructor(
    private val repository: PluginRepository
) {
    operator fun invoke() =
        repository.isLyricsPluginInstalled()
}
