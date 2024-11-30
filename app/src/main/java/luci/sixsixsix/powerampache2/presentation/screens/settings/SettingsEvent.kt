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

import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.StreamingQuality

sealed class SettingsEvent {
    data class OnEnableRemoteLoggingSwitch(val newValue: Boolean): SettingsEvent()
    data class OnHideDonationButtonSwitch(val newValue: Boolean): SettingsEvent()
    data class OnMonoValueChange(val isMono: Boolean): SettingsEvent()
    data object OnOfflineToggle: SettingsEvent()
    data class OnDownloadsSdCardValueChange(val isDownloadsSdCard: Boolean): SettingsEvent()
    data object GoToWebsite: SettingsEvent()
    data class OnNormalizeValueChange(val isVolumeNormalized: Boolean): SettingsEvent()
    data class OnSmartDownloadValueChange(val isSmartDownloadEnabled: Boolean): SettingsEvent()
    data class OnAutomaticUpdateValueChange(val isAutoUpdate: Boolean): SettingsEvent()
    data object UpdateNow: SettingsEvent()
    data object DeleteDownloads: SettingsEvent()
    data class OnStreamingQualityChange(val newValue: StreamingQuality): SettingsEvent()
    data class OnThemeChange(val newValue: PowerAmpTheme): SettingsEvent()
}
