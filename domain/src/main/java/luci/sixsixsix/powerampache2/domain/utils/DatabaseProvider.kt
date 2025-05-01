package luci.sixsixsix.powerampache2.domain.utils

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings

interface DatabaseProvider {
    val settingsFlow: Flow<LocalSettings>
    suspend fun clearSession()
}
