package luci.sixsixsix.powerampache2.domain.usecase.settings

import luci.sixsixsix.powerampache2.domain.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineModeFlowUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() = settingsRepository.offlineModeFlow
}
