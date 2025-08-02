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

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.getVersionInfoString
import luci.sixsixsix.powerampache2.common.openLinkInBrowser
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.models.settings.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.settings.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.usecase.ServerInfoStateFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.UserFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.DeleteAllDownloadedSongsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.GetLocalSettingsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.LocalSettingsFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.SaveLocalSettingsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ToggleOfflineModeUseCase
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject
import kotlin.math.max
import kotlin.system.exitProcess

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val savedStateHandle: SavedStateHandle,
    private val saveLocalSettingsUseCase: SaveLocalSettingsUseCase,
    private val getLocalSettingsUseCase: GetLocalSettingsUseCase,
    private val toggleOfflineMode: ToggleOfflineModeUseCase,
    private val deleteAllDownloadedSongs: DeleteAllDownloadedSongsUseCase,
    localSettingsFlow: LocalSettingsFlowUseCase,
    offlineModeFlowUseCase: OfflineModeFlowUseCase,
    userFlowUseCase: UserFlowUseCase,
    serverInfoStateFlowUseCase: ServerInfoStateFlowUseCase,
    private val playlistManager: MusicPlaylistManager,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    var state by savedStateHandle.saveable {
        mutableStateOf(SettingsState(appVersionInfoStr = getVersionInfoString(application)))
    }

    private fun playerBuffersInitialState() = PlayerSettingsState(
        backBuffer = sharedPreferencesManager.backBuffer / 1000,
        minBuffer = sharedPreferencesManager.minBufferMs / 1000,
        maxBuffer = sharedPreferencesManager.maxBufferMs / 1000,
        bufferForPlayback = sharedPreferencesManager.bufferForPlaybackMs / 1000,
        bufferForPlaybackAfterRebuffer = sharedPreferencesManager.bufferForPlaybackAfterRebufferMs / 1000,
        useOkHttpExoplayer = sharedPreferencesManager.useOkHttpForExoPlayer,
        cacheSizeMb = sharedPreferencesManager.cacheSizeMb
    )

    var playerSettingsStateFlow = MutableStateFlow(playerBuffersInitialState())
        private set

    val logs by mutableStateOf(
        if (BuildConfig.DEBUG) {
            try {
                mutableListOf<String>(GsonBuilder().setPrettyPrinting().create().toJson(Constants.config))
            } catch (e: Exception) {
                mutableListOf<String>()
            }
        } else {
            mutableListOf<String>()
        }
    )

    val offlineModeStateFlow = offlineModeFlowUseCase().map {
            playlistManager.updateUserMessage("")
            it
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val localSettingsStateFlow = localSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalSettings.defaultSettings())

    val userStateFlow = userFlowUseCase().filterNotNull().distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val serverInfoStateFlow = serverInfoStateFlowUseCase().filterNotNull()

    init {
        // collect all the logs
        viewModelScope.launch {
            playlistManager.errorLogMessageState.collect { errorState ->
                errorState.errorMessage?.let {
                    // do not allow the error log to take too much space
                    if (logs.size > 66) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                            logs.removeLast()
                        } else {
                            // TODO: remove legacy
                        }
                    }
                    logs.add(0, it)
                }
            }
        }
    }

    fun onPlayerEvent(event: PlayerSettingsEvent) {
        when(event) {
            is PlayerSettingsEvent.OnBackBufferChange -> {
                // update the persistent setting. Stored in ms (secs x 1000)
                sharedPreferencesManager.backBuffer = event.newValue * 1000
                // update UI. Show in seconds (value / 1000)
                playerSettingsStateFlow.value = playerSettingsStateFlow.value.copy(
                    backBuffer = sharedPreferencesManager.backBuffer / 1000
                )
            }
            is PlayerSettingsEvent.OnBufferForPlaybackAfterRebufferChange -> {
                // minBufferMs cannot be less than bufferForPlaybackAfterRebufferMs
                val minBufferS = sharedPreferencesManager.minBufferMs / 1000
                val newBufferS = if (minBufferS < event.newValue) {
                    minBufferS
                } else event.newValue

                sharedPreferencesManager.bufferForPlaybackAfterRebufferMs = newBufferS * 1000
                playerSettingsStateFlow.value = playerSettingsStateFlow.value.copy(
                    bufferForPlaybackAfterRebuffer = sharedPreferencesManager.bufferForPlaybackAfterRebufferMs / 1000
                )
            }
            is PlayerSettingsEvent.OnBufferForPlaybackChange -> {
                // minBufferMs > bufferForPlaybackMs always
                val minBufferS = sharedPreferencesManager.minBufferMs / 1000
                val newBufferS = if (minBufferS < event.newValue) {
                    minBufferS
                } else event.newValue

                sharedPreferencesManager.bufferForPlaybackMs = newBufferS * 1000
                playerSettingsStateFlow.value = playerSettingsStateFlow.value.copy(
                    bufferForPlayback = sharedPreferencesManager.bufferForPlaybackMs / 1000
                )
            }
            is PlayerSettingsEvent.OnMaxBufferMsChange -> {
                // maxBufferMs > minBufferMs
                val minBufferS = sharedPreferencesManager.minBufferMs / 1000
                val newBufferS = if (minBufferS > event.newValue) {
                    minBufferS
                } else event.newValue

                sharedPreferencesManager.maxBufferMs = newBufferS * 1000
                playerSettingsStateFlow.value = playerSettingsStateFlow.value.copy(
                    maxBuffer = sharedPreferencesManager.maxBufferMs / 1000
                )
            }
            is PlayerSettingsEvent.OnMinBufferChange -> {
                // minBufferMs cannot be less than bufferForPlaybackMs
                // minBufferMs cannot be less than bufferForPlaybackAfterRebufferMs

                val bufferForPlaybackS = sharedPreferencesManager.bufferForPlaybackMs / 1000
                val bufferForPlaybackAfterRebufferS = sharedPreferencesManager.bufferForPlaybackAfterRebufferMs / 1000
                val maxBufferS = sharedPreferencesManager.maxBufferMs / 1000

                val max = max(bufferForPlaybackS, bufferForPlaybackAfterRebufferS)
                var newMinBufferS = if (event.newValue < max) {
                    max
                } else event.newValue

                if (newMinBufferS > maxBufferS) {
                    newMinBufferS = maxBufferS
                }

                sharedPreferencesManager.minBufferMs = newMinBufferS * 1000
                playerSettingsStateFlow.value = playerSettingsStateFlow.value.copy(
                    minBuffer = sharedPreferencesManager.minBufferMs / 1000
                )
            }

            PlayerSettingsEvent.OnResetDefaults -> {
                sharedPreferencesManager.resetBufferDefaults()
                playerSettingsStateFlow.value = playerBuffersInitialState()
            }

            PlayerSettingsEvent.OnKillApp -> {
                exitProcess(0)
            }

            is PlayerSettingsEvent.OnUseOkHttpExoPlayer -> {
                sharedPreferencesManager.useOkHttpForExoPlayer = event.newValue
                playerSettingsStateFlow.value = playerSettingsStateFlow.value.copy(
                    useOkHttpExoplayer = event.newValue
                )
            }

            is PlayerSettingsEvent.OnPlayerCacheSizeChange -> {
                sharedPreferencesManager.cacheSizeMb = event.newValue
                playerSettingsStateFlow.value = playerSettingsStateFlow.value.copy(
                    cacheSizeMb = event.newValue
                )
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.OnEnableRemoteLoggingSwitch -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(enableRemoteLogging = event.newValue)
                )
            }
            is SettingsEvent.OnHideDonationButtonSwitch -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(hideDonationButton = event.newValue)
                )
            }
            is SettingsEvent.OnStreamingQualityChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(streamingQuality = event.newValue)
                )
            }
            is SettingsEvent.OnThemeChange -> setTheme(event.newValue)
            SettingsEvent.DeleteDownloads -> deleteAllDownloads()
            is SettingsEvent.OnMonoValueChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(isMonoAudioEnabled = event.isMono))
            }
            is SettingsEvent.OnNormalizeValueChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(isNormalizeVolumeEnabled = event.isVolumeNormalized))
            }
            is SettingsEvent.OnSmartDownloadValueChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(isSmartDownloadsEnabled = event.isSmartDownloadEnabled)
                )
            }
            SettingsEvent.UpdateNow -> { }
            is SettingsEvent.OnAutomaticUpdateValueChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(enableAutoUpdates = event.isAutoUpdate)
                )
            }

            SettingsEvent.OnOfflineToggle ->  viewModelScope.launch {
                toggleOfflineMode()
            }

            SettingsEvent.GoToWebsite ->
                application.openLinkInBrowser(application.getString(R.string.website))

            is SettingsEvent.OnDownloadsSdCardValueChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(isDownloadsSdCard = event.isDownloadsSdCard)
                )
            }

            is SettingsEvent.OnDownloadAfterPlayChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(saveSongAfterPlayback = event.isDownload)
                )
            }

            is SettingsEvent.OnDownloadFavouriteAfterPlayChange -> viewModelScope.launch {
                saveLocalSettingsUseCase(
                    getLocalSettingsUseCase(userStateFlow.value?.username)
                        .copy(saveFavouriteSongAfterPlayback = event.isDownloadFavourite)
                )
            }
        }
    }

    private fun deleteAllDownloads() = viewModelScope.launch {
        deleteAllDownloadedSongs().collect { result -> when(result) {
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
            saveLocalSettingsUseCase(
                getLocalSettingsUseCase(userStateFlow.value?.username).copy(theme = theme)
            )
        }
    }

    fun onDismissIntroDialog() {
        sharedPreferencesManager.introDialogContent = Constants.config.introMessage
    }

    fun shouldShowIntroDialog() =
        sharedPreferencesManager.shouldShowIntroDialog(Constants.config.introMessage)
}
