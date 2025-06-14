package luci.sixsixsix.powerampache2.domain.usecase.settings

import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import javax.inject.Inject

class GetLocalSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(username: String? = null): LocalSettings {
        return settingsRepository.getLocalSettings(
            username ?: musicRepository.getUsername()
        )
    }
}
