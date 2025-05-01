package luci.sixsixsix.powerampache2.data.local

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettings
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import luci.sixsixsix.powerampache2.domain.utils.DatabaseProvider
import javax.inject.Inject

class DatabaseProviderImpl @Inject constructor(val db: MusicDatabase): DatabaseProvider {
    override val settingsFlow: Flow<LocalSettings>
        get() = db.dao.settingsLiveData()
            .asFlow()
            .filterNotNull()
            .map { it.toLocalSettings() }

    override suspend fun clearSession() {
        db.dao.clearSession()
    }
}
