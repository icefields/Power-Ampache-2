package luci.sixsixsix.powerampache2.presentation.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
    private val settingsRepository: SettingsRepository,
) : AndroidViewModel(application) {
    var state by mutableStateOf(LocalSettings.defaultSettings())

    init {
        observeSettings()
    }

    private fun observeSettings() {
        settingsRepository.settingsLiveData.observeForever {
            state = it
        }
    }

    fun setTheme(theme: PowerAmpTheme) {
        viewModelScope.launch {
            settingsRepository.saveLocalSettings(settingsRepository.getLocalSettings().copy(theme = theme))
        }
    }
}
