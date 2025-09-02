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
package luci.sixsixsix.powerampache2.presentation.screens.plugins

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsChromecastPluginInstalled
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsInfoPluginInstalled
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsLyricsPluginInstalledUseCase
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class PluginsViewModel @Inject constructor(
    lyricsPluginInstalledUseCase: IsLyricsPluginInstalledUseCase,
    infoPluginInstalled: IsInfoPluginInstalled,
    chromecastPluginInstalled: IsChromecastPluginInstalled
) : ViewModel() {

    var state =
        mutableStateOf(PluginsState(
            isLyricsPluginInstalled = lyricsPluginInstalledUseCase(),
            isMetadataPluginInstalled = infoPluginInstalled(),
            isChromecastPluginInstalled = chromecastPluginInstalled()
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
