/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.data.local

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.data.local.entities.toDownloadedSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.toLocalSettings
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import luci.sixsixsix.powerampache2.domain.datasource.DbDataSource
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbDataSourceImpl @Inject constructor(val db: MusicDatabase): DbDataSource {
    override val settingsFlow: Flow<LocalSettings>
        get() = db.dao.settingsLiveData()
            .asFlow()
            .filterNotNull()
            .map { it.toLocalSettings() }

    override suspend fun getSongById(songId: String): Song? = db.dao.getSongById(songId)?.toSong()

    override suspend fun clearSession() {
        db.dao.clearSession()
    }

    override suspend fun getUsername() = db.dao.getCredentials()?.username

    override suspend fun getServerUrl() = db.dao.getCredentials()?.serverUrl

    @Throws(NullPointerException::class)
    override suspend fun addDownloadedSong(song: Song, filepath: String, imageFilePath: String) {
        db.dao.addDownloadedSong(
            song.toDownloadedSongEntity(
                downloadedSongUri = filepath,
                downloadedImageUri = imageFilePath,
                getUsername() ?: throw NullPointerException("Username is null"),
                serverUrl = getServerUrl() ?: throw NullPointerException("Server Url is null")
            )
        )
    }
}
