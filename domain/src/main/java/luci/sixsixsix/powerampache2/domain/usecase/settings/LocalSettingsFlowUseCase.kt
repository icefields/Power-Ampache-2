package luci.sixsixsix.powerampache2.domain.usecase.settings

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSettingsFlowUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<LocalSettings> =
        settingsRepository.settingsLiveData
            .asFlow()
            .filterNotNull()
            .distinctUntilChanged()
}

