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
package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.SortMode

interface SettingsRepository {
    val settingsLiveData: LiveData<LocalSettings?>
    val offlineModeFlow: Flow<Boolean>

//    val backBuffer: Flow<Int>
//    val minBufferMs: Flow<Int>
//    val maxBufferMs: Flow<Int>
//    val bufferForPlaybackMs: Flow<Int>
//    val bufferForPlaybackAfterRebufferMs: Flow<Int>

    suspend fun getLocalSettings(username: String? = null): LocalSettings
    suspend fun saveLocalSettings(localSettings: LocalSettings)
    suspend fun deleteAllDownloadedSongs(): Flow<Resource<Any>>
    suspend fun changeSortMode(sortMode: SortMode)
    suspend fun toggleGlobalShuffle(): Boolean
    suspend fun toggleOfflineMode(): Boolean
    suspend fun isOfflineModeEnabled(): Boolean
}
