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
package luci.sixsixsix.powerampache2.presentation.settings

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import javax.inject.Inject


@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val settingsRepository: SettingsRepository,
    private val musicRepository: MusicRepository,
) : AndroidViewModel(application) {
    //var state by mutableStateOf(LocalSettings.defaultSettings())
    var state by savedStateHandle.saveable {
        mutableStateOf(SettingsState(appVersionInfoStr = getVersionInfoString()))
    }

    init {
        getServerInfo()
        observeSettings()

        viewModelScope.launch {
            musicRepository.userLiveData.observeForever {
                state = state.copy( user = it)
            }
        }
    }

    private fun getServerInfo() = viewModelScope.launch {
        musicRepository.ping().data?.first?.let {
            state = state.copy(serverInfo = it)
        }
    }

    private fun getVersionInfoString() = try {
        val pInfo: PackageInfo =
            application.packageManager.getPackageInfo(application.packageName, 0)
        "${pInfo.versionName} (${pInfo.longVersionCode})"
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }

    private fun observeSettings() {
        settingsRepository.settingsLiveData.observeForever { localSettings ->
            localSettings?.let { updatedSettings ->
                if (updatedSettings != state.localSettings)
                    state = state.copy( localSettings = updatedSettings)
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.OnEnableAutoUpdatesSwitch -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(state.user?.username)
                        .copy(enableAutoUpdates = event.newValue)
                )
            }
            is SettingsEvent.OnEnableRemoteLoggingSwitch -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(state.user?.username)
                        .copy(enableRemoteLogging = event.newValue)
                )
            }
            is SettingsEvent.OnHideDonationButtonSwitch -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(state.user?.username)
                        .copy(hideDonationButton = event.newValue)
                )
            }
            is SettingsEvent.OnSmartDownloadSwitch -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(state.user?.username)
                        .copy(smartDownloadEnabled = event.newValue)
                )
            }
            is SettingsEvent.OnStreamingQualityChange -> viewModelScope.launch {
                settingsRepository.saveLocalSettings(
                    settingsRepository.getLocalSettings(state.user?.username)
                        .copy(streamingQuality = event.newValue)
                )
            }
            is SettingsEvent.OnThemeChange -> setTheme(event.newValue)
        }
    }

    private fun setTheme(theme: PowerAmpTheme) {
        viewModelScope.launch {
            // colours are static and depend on the hash of the object, reset if changing theme
            RandomThemeBackgroundColour.resetColours()
            settingsRepository.saveLocalSettings(
                settingsRepository.getLocalSettings(state.user?.username).copy(theme = theme)
            )
        }
    }
}
