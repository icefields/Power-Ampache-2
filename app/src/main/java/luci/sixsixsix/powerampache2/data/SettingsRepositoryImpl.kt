/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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

    private fun userLiveData() = dao.getUserLiveData().map { it?.toUser() }

    override suspend fun getLocalSettings(username: String?) =
        dao.getSettings()?.toLocalSettings()
            ?: LocalSettings.defaultSettings(username)

    override suspend fun saveLocalSettings(localSettings: LocalSettings) =
        dao.writeSettings(localSettings.toLocalSettingsEntity())
}
