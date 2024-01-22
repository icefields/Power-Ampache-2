package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainContentMenuItem(
    val id: String, // identifier, because title is subject to translations
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
) {
    companion object {
        /**
         * workaround because we cannot save MainContentMenuItem into rememberSaveable
         */
        fun toMainContentMenuItem(id: String) =
            when (id) {
                "home" -> Home
                "settings" -> Settings
                "library" -> Library
                "logout" -> Logout
                "offline" -> Offline
                else -> throw IllegalArgumentException("$id is not a valid id")
            }

    }

    data object Home: MainContentMenuItem(id = "home", title = "Home", icon = Icons.Default.Home, contentDescription = "home")
    data object Settings: MainContentMenuItem(id = "settings", title = "Settings", icon = Icons.Default.Settings, contentDescription = "Settings")
    data object Library: MainContentMenuItem(id = "library", title = "Library", icon = Icons.Default.LibraryMusic, contentDescription = "Library")
    data object Offline: MainContentMenuItem(id = "offline", title = "Offline Songs", icon = Icons.Default.OfflineBolt, contentDescription = "Offline Songs")
    data object Logout: MainContentMenuItem(id = "logout", title = "Logout", icon = Icons.Default.Logout, contentDescription = "Logout")
}
