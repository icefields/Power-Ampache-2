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
package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDismissItem(
    item: T,
    foregroundView: @Composable () -> Unit,
    enableSwipeToRemove: Boolean = false,
    iconLeft: ImageVector = Icons.Default.Delete,
    iconRight: ImageVector = Icons.Default.PlaylistAdd,
    onRemove: (T) -> Unit = { },
    onRightToLeftSwipe: (T) -> Unit = { }
) {
    val currentItem by rememberUpdatedState(item)
    if (enableSwipeToRemove) {
        var show by remember { mutableStateOf(true) }
        var rightShow by remember { mutableStateOf(true) }
        val dismissState = rememberDismissState(
            confirmValueChange = {
                if (it == DismissValue.DismissedToEnd) {
                    show = false
                    false
                } else if (it == DismissValue.DismissedToStart) {
                    rightShow = false
                    false
                } else false
            },
            positionalThreshold = { 250.dp.toPx() }
        )

//        AnimatedVisibility(
//            visible = show,
//            exit = fadeOut(spring())
//        ) {
        SwipeToDismiss(
            state = dismissState,
            modifier = Modifier,
            background = { SwipeToDismissBackground(
                    dismissState = dismissState,
                    iconLeft = iconLeft,
                    iconRight = iconRight
            ) },
            dismissContent = {
                foregroundView()
            }
        )
        //}
        LaunchedEffect(show) {
            if (!show) {
                onRemove(currentItem)
                show = true
            }

        }
        LaunchedEffect(rightShow) {
            if (!rightShow) {
                onRightToLeftSwipe(currentItem)
                rightShow = true
            }
        }

    } else {
        foregroundView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBackground(
    dismissState: DismissState,
    iconLeft: ImageVector = Icons.Default.Delete,
    iconRight: ImageVector = Icons.Default.PlaylistAdd
) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.StartToEnd -> MaterialTheme.colorScheme.errorContainer
        DismissDirection.EndToStart -> MaterialTheme.colorScheme.primaryContainer
        null -> Color.Transparent
    }
    val direction = dismissState.dismissDirection
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (direction == DismissDirection.StartToEnd) {
            Icon(iconLeft, contentDescription = "delete")
        }
        Spacer(modifier = Modifier)
        if (direction == DismissDirection.EndToStart) {
            Icon(iconRight, contentDescription = "Add to playlist")
        }
    }
}
