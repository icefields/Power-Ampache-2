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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainContentTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchVisibility: MutableState<Boolean>,
    title: String,
    modifier: Modifier = Modifier,
    isOfflineMode: Boolean,
    showOfflineSwitch: Boolean,
    isQueueEmpty: Boolean,
    isNotificationQueueEmpty: Boolean,
    isFabLoading: Boolean,
    isGenreSubScreen: Boolean,
    floatingActionVisible: Boolean,
    onGenreScreenBackClick: () -> Unit,
    onMagicPlayClick: () -> Unit,
    onOfflineModeSwitch: () -> Unit,
    onNavigationIconClick: (MainContentTopAppBarEvent) -> Unit
) {
    // val interactionSource = remember { MutableInteractionSource() }
    val transitionState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    TopAppBar(
        modifier = modifier,
        title = {
            if (!showOfflineSwitch || !isOfflineMode) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(isGenreSubScreen) {
                        CircleBackButton(
                            background = Color.Transparent,
                            onClick =  onGenreScreenBackClick)
                    }
                    AnimatedVisibility(visibleState = transitionState) {
                        Text(
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .basicMarquee(),
                            text = title,
                            maxLines = 1
                        )
                    }
                }
            }
            AnimatedVisibility(visible = (isOfflineMode && showOfflineSwitch)) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .wrapContentSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.offlineMode_switch_title),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        modifier = Modifier.padding(start = 4.dp),
                        checked = true,
                        onCheckedChange = {
                            onOfflineModeSwitch()
                        },
                        enabled = true
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                onNavigationIconClick(MainContentTopAppBarEvent.OnLeftDrawerIconClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            AnimatedVisibility(!isNotificationQueueEmpty) {
                IconButton(
                    onClick = {
                        onNavigationIconClick(MainContentTopAppBarEvent.OnNotificationsIconClick)
                    }) {
                    Icon(
                        imageVector = Icons.Default.NotificationImportant,
                        contentDescription = "Notifications",
                        //tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = {
                    searchVisibility.value = !searchVisibility.value
                }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    //tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(!isQueueEmpty) {
                IconButton(
                    onClick = {
                        onNavigationIconClick(MainContentTopAppBarEvent.OnPlaylistIconClick)
                    }) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = stringResource(id = R.string.queue)
                    )
                }
            }

            AnimatedVisibility(!floatingActionVisible) {
                if (isFabLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(46.dp)
                            .padding(6.dp))
                } else {
                    Icon(modifier = Modifier
                        .size(46.dp)
                        .padding(horizontal = 6.dp)
                        .clickable {
                            onMagicPlayClick()
                        },
                        painter = painterResource(id = R.drawable.ic_play_speaker),
                        contentDescription = "Quick Play",
                       // tint = MaterialTheme.colorScheme.tertiary
                    )
                }
//                IconButton(onClick = onMagicPlayClick) {
//                    Icon(
//                        painterResource(id = R.drawable.ic_play_speaker),
//                        contentDescription = "Magic Play"
//                    )
//                }
            }
        }
    )
}

sealed class MainContentTopAppBarEvent {
    data object OnLeftDrawerIconClick: MainContentTopAppBarEvent()
    data object OnPlaylistIconClick: MainContentTopAppBarEvent()
    data object OnNotificationsIconClick: MainContentTopAppBarEvent()
}
