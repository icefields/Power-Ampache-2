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
package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.OfflineBolt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainContentMenuItem(
    val id: String, // identifier, because title is subject to translations
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
) {
    companion object {
        /**
         * TODO use reflection instead
         * workaround because we cannot save MainContentMenuItem into rememberSaveable
         */
        fun toMainContentMenuItem(id: String) =
            when (id) {
                "home" -> Home
                "settings" -> Settings
                "library" -> Library
                "logout" -> Logout
                "about" -> About
                "offline" -> Offline
                else -> throw IllegalArgumentException("$id is not a valid id")
            }

    }

    data object Home: MainContentMenuItem(id = "home", title = "Home", icon = Icons.Outlined.Home, contentDescription = "home")
    data object Settings: MainContentMenuItem(id = "settings", title = "Settings", icon = Icons.Outlined.Settings, contentDescription = "Settings")
    data object Library: MainContentMenuItem(id = "library", title = "Library", icon = Icons.Outlined.LibraryMusic, contentDescription = "Library")
    data object Offline: MainContentMenuItem(id = "offline", title = "Offline Songs", icon = Icons.Outlined.OfflineBolt, contentDescription = "Offline Songs")
    data object About: MainContentMenuItem(id = "about", title = "About", icon = Icons.Outlined.Info, contentDescription = "About")
    data object Logout: MainContentMenuItem(id = "logout", title = "Logout", icon = Icons.Outlined.Logout, contentDescription = "Logout")
}
