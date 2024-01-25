package luci.sixsixsix.powerampache2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettings
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettingsEntity
import luci.sixsixsix.powerampache2.data.local.entities.toUser
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val errorHandler: ErrorHandler
): SettingsRepository {
    private val dao = db.dao
    override val settingsLiveData: LiveData<LocalSettings?>
        get() = dao.settingsLiveData().map {
                it?.toLocalSettings()
        }

    override suspend fun getDownloadWorkerId() = getLocalSettings().workerId

    override suspend fun resetDownloadWorkerId() =
        UUID.randomUUID().toString().also {
            dao.writeSettings(dao.getSettings().copy(workerId = it))
        }

    private fun userLiveData() = dao.getUserLiveData().map { it?.toUser() }

    override suspend fun getLocalSettings() = try {
        dao.getSettings().toLocalSettings()
    } catch (e: Exception) {
        L(e)
        LocalSettings.defaultSettings().run {
            // if user logged in and no settings saved in db yet, save now, otherwise return default
            dao.getUser()?.let { user ->
                val updatedSettings = copy(username = user.username)
                saveLocalSettings(updatedSettings)
                updatedSettings
            } ?: this
        }
    }


    override suspend fun saveLocalSettings(localSettings: LocalSettings) =
        dao.writeSettings(localSettings.toLocalSettingsEntity())
}
