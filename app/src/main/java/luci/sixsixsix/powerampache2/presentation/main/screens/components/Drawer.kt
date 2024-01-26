package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val drawerItems = listOf(
    MainContentMenuItem.Home,
    MainContentMenuItem.Library,
    MainContentMenuItem.Offline,
    MainContentMenuItem.Settings,
    MainContentMenuItem.Logout
)

@Composable
fun DrawerHeader(user: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = user, fontSize = 30.sp)
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
