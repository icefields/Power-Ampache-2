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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainContentTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchVisibility: MutableState<Boolean>,
    title: String,
    modifier: Modifier = Modifier,
    isQueueEmpty: Boolean,
    isFabLoading: Boolean,
    floatingActionVisible: Boolean,
    onMagicPlayClick: () -> Unit,
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
            AnimatedVisibility(visibleState = transitionState) {
                Text(
                    modifier = Modifier
                        .basicMarquee(),
                    text = title,
                    maxLines = 1
                )
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
                        contentDescription = "Queue",
                        //tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            AnimatedVisibility(!floatingActionVisible) {
                if (isFabLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(46.dp)
                            .padding(6.dp))
                } else {
                    Icon(modifier = Modifier.size(46.dp).padding(horizontal = 6.dp)
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
}
