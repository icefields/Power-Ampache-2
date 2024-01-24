package luci.sixsixsix.powerampache2.presentation.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.AlbumDetailState
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
        mutableStateOf(LocalSettings.defaultSettings())
    }

    var userState by mutableStateOf<User?>(null)

    init {
        observeSettings()

        viewModelScope.launch {
            musicRepository.userLiveData.observeForever {
                it?.let { user ->
                    userState = user
                }
            }
        }
    }

    private fun observeSettings() {
        settingsRepository.settingsLiveData.observeForever { localSettings ->
            localSettings?.let { updatedSettings ->
                if (updatedSettings != state)
                    state = updatedSettings
            }
        }
    }

    fun setTheme(theme: PowerAmpTheme) {
        viewModelScope.launch {
            settingsRepository.saveLocalSettings(settingsRepository.getLocalSettings().copy(theme = theme))
        }
    }
}
