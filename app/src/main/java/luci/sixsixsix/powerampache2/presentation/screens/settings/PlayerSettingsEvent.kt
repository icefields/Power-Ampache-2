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

sealed class PlayerSettingsEvent {
    data class OnBackBufferChange(val newValue: Int): PlayerSettingsEvent()
    data object OnResetDefaults: PlayerSettingsEvent()
    data object OnKillApp: PlayerSettingsEvent()
    data class OnMinBufferChange(val newValue: Int): PlayerSettingsEvent()
    data class OnMaxBufferMsChange(val newValue: Int): PlayerSettingsEvent()
    data class OnBufferForPlaybackChange(val newValue: Int): PlayerSettingsEvent()
    data class OnBufferForPlaybackAfterRebufferChange(val newValue: Int): PlayerSettingsEvent()
}
