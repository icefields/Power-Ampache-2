package luci.sixsixsix.powerampache2.domain.usecase.settings

import luci.sixsixsix.powerampache2.domain.SettingsRepository
import javax.inject.Inject

class IsOfflineModeEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() = settingsRepository.isOfflineModeEnabled()
}
