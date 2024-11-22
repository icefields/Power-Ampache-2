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

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettings
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettingsEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.SortMode
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    api: MainNetwork,
    db: MusicDatabase,
    private val errorHandler: ErrorHandler,
    private val storageManager: StorageManager
): BaseAmpacheRepository(api, db, errorHandler), SettingsRepository {
    override suspend fun getLocalSettings(username: String?) =
        dao.getSettings()?.toLocalSettings()
            ?: LocalSettings.defaultSettings(username)

    override suspend fun saveLocalSettings(localSettings: LocalSettings) =
        dao.writeSettings(localSettings.toLocalSettingsEntity())

    override suspend fun changeSortMode(sortMode: SortMode) {
        getUsername()?.let { username ->
            saveLocalSettings(getLocalSettings(username).copy(playlistSongsSorting = sortMode))
        }
    }

    override suspend fun toggleGlobalShuffle() =
        getUsername()?.let { username ->
            getLocalSettings(username).apply {
                val newValue = !isGlobalShuffleEnabled
                saveLocalSettings(copy(isGlobalShuffleEnabled = newValue))
            }.isGlobalShuffleEnabled
        } ?: throw Exception("toggleGlobalShuffle, error saving global shuffle")

    override suspend fun toggleOfflineMode() =
        getUsername()?.let { username ->
            getLocalSettings(username).apply {
                val newValue = !isOfflineModeEnabled
                saveLocalSettings(copy(isOfflineModeEnabled = newValue))
            }.isOfflineModeEnabled
        } ?: throw Exception("toggleOfflineMode, error ")

    override suspend fun deleteAllDownloadedSongs() = flow {
        L("deleteAllDownloadedSongs")
        emit(Resource.Loading(true))
        dao.deleteAllDownloadedSong()
        L("after deleteAllDownloadedSongs db")

        storageManager.deleteAll()
        emit(Resource.Success(data = Any(), networkData = Any()))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("deleteAllDownloadedSongs()", e, this) }
}
