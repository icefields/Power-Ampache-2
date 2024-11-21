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
package luci.sixsixsix.powerampache2.presentation.screens.settings

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.getVersionInfoString
import luci.sixsixsix.powerampache2.common.openLinkInBrowser
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val settingsRepository: SettingsRepository,
    private val musicRepository: MusicRepository,
    private val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application) {
    var state by savedStateHandle.saveable {
        mutableStateOf(SettingsState(appVersionInfoStr = getVersionInfoString(application)))
    }
    val logs by mutableStateOf(mutableListOf<String>())

    val offlineModeStateFlow = settingsRepository.offlineModeFlow
        .map {
            playlistManager.updateUserMessage("")
            it
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val localSettingsStateFlow = settingsRepository.settingsLiveData
        .asFlow()
        .filterNotNull()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalSettings.defaultSettings())

    val userStateFlow = musicRepository.userLiveData
        .filterNotNull().distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val serverInfoStateFlow = musicRepository.serverInfoStateFlow.filterNotNull()

    init {
        // collect all the logs
        viewModelScope.launch {
            playlistManager.errorLogMessageState.collect { errorState ->
                errorState.errorMessage?.let {
                    // do not allow the error log to take too much space
                    if (logs.size > 66) logs.removeLast()
                    logs.add(0, it)
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.OnEnableRemoteLoggingSwitch -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(enableRemoteLogging = event.newValue)
                )
            }
            is SettingsEvent.OnHideDonationButtonSwitch -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(hideDonationButton = event.newValue)
                )
            }
            is SettingsEvent.OnStreamingQualityChange -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(streamingQuality = event.newValue)
                )
            }
            is SettingsEvent.OnThemeChange -> setTheme(event.newValue)
            SettingsEvent.DeleteDownloads -> deleteAllDownloads()
            is SettingsEvent.OnMonoValueChange -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(isMonoAudioEnabled = event.isMono))
            }
            is SettingsEvent.OnNormalizeValueChange -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(isNormalizeVolumeEnabled = event.isVolumeNormalized))
            }
            is SettingsEvent.OnSmartDownloadValueChange -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(isSmartDownloadsEnabled = event.isSmartDownloadEnabled)
                )
            }
            SettingsEvent.UpdateNow -> { }
            is SettingsEvent.OnAutomaticUpdateValueChange -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(enableAutoUpdates = event.isAutoUpdate)
                )
            }

            SettingsEvent.OnOfflineToggle ->  viewModelScope.launch {
                settingsRepository.toggleOfflineMode()
            }

            SettingsEvent.goToWebsite ->
                application.openLinkInBrowser(application.getString(R.string.website))

            is SettingsEvent.OnDownloadsSdCardValueChange -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(userStateFlow.value?.username)
                        .copy(isDownloadsSdCard = event.isDownloadsSdCard)
                )
            }
        }
    }

    private fun deleteAllDownloads() = viewModelScope.launch {
        settingsRepository.deleteAllDownloadedSongs().collect { result -> when(result) {
            is Resource.Success -> { result.data?.let {
                Toast.makeText(application, R.string.settings_deleteDownloads_success, Toast.LENGTH_LONG).show() } }
            is Resource.Error ->
                Toast.makeText(application, R.string.settings_deleteDownloads_fail, Toast.LENGTH_LONG).show()
            is Resource.Loading -> { }
        } }
    }

    private fun setTheme(theme: PowerAmpTheme) {
        viewModelScope.launch {
            // colours are static and depend on the hash of the object, reset if changing theme
            RandomThemeBackgroundColour.resetColours()
            settingsRepository.saveLocalSettings(
                settingsRepository.getLocalSettings(userStateFlow.value?.username).copy(theme = theme)
            )
        }
    }
}
