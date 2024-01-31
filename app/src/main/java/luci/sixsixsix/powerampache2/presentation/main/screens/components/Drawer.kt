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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants

val drawerItems = listOf(
    MainContentMenuItem.Home,
    MainContentMenuItem.Library,
    MainContentMenuItem.Offline,
    MainContentMenuItem.Settings,
    MainContentMenuItem.Logout
)

@Composable
fun DrawerHeader(user: String) {
    Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (BuildConfig.DOGMAZIC_USER != user ||
                (!BuildConfig.DEBUG && user != Constants.ERROR_STRING)) {
                user
            } else stringResource(id = R.string.app_name),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

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
