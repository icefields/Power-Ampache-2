package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.PluginRepository
import javax.inject.Inject

class IsChromecastPluginInstalled @Inject constructor(private val repository: PluginRepository) {
    operator fun invoke() = repository.isChromecastPluginInstalled()
}
