package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.User

interface SettingsRepository {
    val settingsLiveData: LiveData<LocalSettings?>
    suspend fun getLocalSettings(): LocalSettings
    suspend fun getDownloadWorkerId(): String
    suspend fun resetDownloadWorkerId(): String
    suspend fun saveLocalSettings(localSettings: LocalSettings)
}
