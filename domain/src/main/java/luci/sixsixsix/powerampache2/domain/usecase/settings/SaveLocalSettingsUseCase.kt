package luci.sixsixsix.powerampache2.domain.usecase.settings

import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import javax.inject.Inject

class SaveLocalSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(localSettings: LocalSettings) {
        settingsRepository.saveLocalSettings(localSettings)
    }
}
