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
package luci.sixsixsix.powerampache2.presentation.screens.main.screens.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.USER_ERROR_MESSAGE
import luci.sixsixsix.powerampache2.common.toHslColor
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.common.DonateButton
import luci.sixsixsix.powerampache2.presentation.common.DonateButtonPreview
import luci.sixsixsix.powerampache2.presentation.common.TextWithOverline
import java.util.Locale

val drawerItems = listOf(
    MainContentMenuItem.Home,
    MainContentMenuItem.Library,
    MainContentMenuItem.Offline,
    MainContentMenuItem.Genres,
    MainContentMenuItem.Settings,
    MainContentMenuItem.About,
    //MainContentMenuItem.Logout
)

@Composable
fun MainDrawer(
    user: User,
    versionInfo: String,
    hideDonationButtons: Boolean,
    currentItem: MainContentMenuItem,
    items: List<MainContentMenuItem> = drawerItems,
    onItemClick: (MainContentMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    donateButton: @Composable () -> Unit = { DonateButton(
        isTransparent = true,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) }
) {
    ModalDrawerSheet(
        modifier = modifier,
        //modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        DrawerHeader(user) {
            onItemClick(MainContentMenuItem.Logout)
        }
        Divider()
        DrawerBody(
            currentItem = currentItem,
            modifier = Modifier.weight(1f),
            items = items,
            onItemClick = onItemClick
        )
        if (!hideDonationButtons) {
            donateButton()
        }
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .background(
                    if (BuildConfig.DEBUG) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent
                )
            ,
            text = "v$versionInfo",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light,
            color = if (BuildConfig.DEBUG) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DrawerHeader(
    currentUser: User,
    onLogoutAction: () -> Unit
) {
    // if dogmazic user show custom information
    val user = if (BuildConfig.DOGMAZIC_USER == currentUser.username && BuildConfig.DOGMAZIC_EMAIL == currentUser.email) {
        User.demoUser()
    } else {
        currentUser
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        var showFullUserInfo by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable {
                    showFullUserInfo = !showFullUserInfo
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                UserHead(id = user.id,
                    firstName = user.username,
                    lastName = "",
                    imageUrl = user.art
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = user.username,
                            fontSize = 18.sp,
                            maxLines = 1,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(start = 0.dp),
                        text = currentUser.serverUrl.uppercase(),
                        fontSize = 10.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onLogoutAction) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "logout current user"
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedVisibility(visible = showFullUserInfo) {
                UserInfoSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showFullUserInfo = !showFullUserInfo
                        },
                    user = user
                )
            }
        }
    }
}

@Composable
fun DrawerBody(
    currentItem: MainContentMenuItem,
    items: List<MainContentMenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MainContentMenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            NavigationDrawerItem(
                shape = RoundedCornerShape(1.dp),
                selected = currentItem == item,
                label = {
                    Text(text = item.title, style = itemTextStyle)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription
                    )
                },
                onClick = {
                    onItemClick(item)
                },
            )
        }
    }
}

@Composable
fun UserHead(
    id: String,
    firstName: String,
    lastName: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    AnimatedVisibility(visible = !imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.placeholder_album),
            contentDescription = "user avatar image",
        )
    }

    AnimatedVisibility(visible = imageUrl.isNullOrBlank()) {
        Box(modifier.size(size), contentAlignment = Alignment.Center) {
            val color = remember(id, firstName, lastName) {
                val name = listOf(firstName, lastName)
                    .joinToString(separator = "")
                    .uppercase()
                Color("$id / $name".toHslColor())
            }
            val initials = (firstName.take(1) + lastName.take(1)).uppercase()
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(SolidColor(color))
            }
            Text(text = initials, style = textStyle, color = Color.White)
        }
    }
}

@Composable
fun UserInfoSection(
    modifier: Modifier = Modifier,
    user: User
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentHeight()
        ) {
            UserInfoTextWithTitle(title = R.string.settings_userInfo_username, subtitle = user.username)
            if (user.fullNamePublic == 1) {
                UserInfoTextWithTitle(title = R.string.settings_userInfo_fullName, subtitle = user.fullName)
            }
            UserInfoTextWithTitle(title = R.string.settings_userInfo_email, subtitle = user.email)
            UserInfoTextWithTitle(title = R.string.settings_userInfo_website, subtitle = user.website)
            UserInfoTextWithTitle(title = R.string.settings_userInfo_city, subtitle = user.city)
            UserInfoTextWithTitle(title = R.string.settings_userInfo_state, subtitle = user.state)
            UserInfoTextWithTitle(title = R.string.settings_userInfo_id,
                subtitle = if (!user.isError()) user.id else USER_ERROR_MESSAGE)
        }
    }
}

@Composable
private fun UserInfoTextWithTitle(@StringRes title: Int, subtitle: String?) {
    subtitle?.let {
        if (it.isNotBlank()) {
            TextWithOverline(title = title, subtitle = it)
        }
    }
}

@Composable @Preview
fun PreviewDrawer() {
    MainDrawer(
        //modifier = Modifier.weight(1f),
        user = User.mockUser(),
        versionInfo = "0.666-beta (666)",
        hideDonationButtons = false,
        currentItem = drawerItems[1],
        donateButton = {
            DonateButtonPreview()
        },
        onItemClick = { }
    )
}
