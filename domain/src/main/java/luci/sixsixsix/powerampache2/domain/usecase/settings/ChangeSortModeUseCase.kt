package luci.sixsixsix.powerampache2.domain.usecase.settings

import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.settings.SortMode
import javax.inject.Inject

class ChangeSortModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(sortMode: SortMode) = settingsRepository.changeSortMode(sortMode)
}
