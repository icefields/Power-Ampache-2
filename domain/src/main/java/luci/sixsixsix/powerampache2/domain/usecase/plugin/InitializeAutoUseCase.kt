package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.PluginRepository
import javax.inject.Inject

class InitializeAutoUseCase @Inject constructor(private val repository: PluginRepository) {
    operator fun invoke() = repository.initializeAuto()
}
