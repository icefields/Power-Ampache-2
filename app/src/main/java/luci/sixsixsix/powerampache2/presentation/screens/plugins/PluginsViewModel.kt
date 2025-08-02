package luci.sixsixsix.powerampache2.presentation.screens.plugins

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsInfoPluginInstalled
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsLyricsPluginInstalledUseCase
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class PluginsViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val savedStateHandle: SavedStateHandle,
    private val lyricsPluginInstalledUseCase: IsLyricsPluginInstalledUseCase,
    private val infoPluginInstalled: IsInfoPluginInstalled
) : ViewModel() {

    var state =
        mutableStateOf(PluginsState(
            isLyricsPluginInstalled = lyricsPluginInstalledUseCase(),
            isMetadataPluginInstalled = infoPluginInstalled()
        ))
}


@Parcelize
data class PluginsState(
    val isLoading: Boolean = false,
    val isLyricsPluginInstalled: Boolean = false,
    val isChromecastPluginInstalled: Boolean = false,
    val isAndroidAutoPluginInstalled: Boolean = false,
    val isMetadataPluginInstalled: Boolean = false,
    val isExternalDataSourcePluginInstalled: Boolean = false,
): Parcelable
