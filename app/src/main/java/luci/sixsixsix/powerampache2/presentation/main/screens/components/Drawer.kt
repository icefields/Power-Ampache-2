package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val drawerItems = listOf(
    MainContentMenuItem.Home,
    MainContentMenuItem.Library,
    MainContentMenuItem.Settings,
    MainContentMenuItem.Logout
)

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Header", fontSize = 40.sp)
    }
}

@Composable
fun DrawerBody(
    items: List<MainContentMenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MainContentMenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            NavigationDrawerItem(
                label = {
                    Text(text = item.title, style = itemTextStyle)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription
                    )
                },
                selected = false,
                onClick = {
                    onItemClick(item)
                },
            )
        }
    }
}

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
                else -> throw IllegalArgumentException("$id is not a valid id")
            }

    }

    data object Home: MainContentMenuItem(id = "home", title = "Home", icon = Icons.Default.Home, contentDescription = "home")
    data object Settings: MainContentMenuItem(id = "settings", title = "Settings", icon = Icons.Default.Settings, contentDescription = "Settings")
    data object Library: MainContentMenuItem(id = "library", title = "Library", icon = Icons.Default.LibraryMusic, contentDescription = "Library")
    data object Logout: MainContentMenuItem(id = "logout", title = "Logout", icon = Icons.Default.Logout, contentDescription = "Logout")
}
