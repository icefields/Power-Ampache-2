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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onRemove(currentItem)
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onRightToLeftSwipe(currentItem)
                    false
                }
                SwipeToDismissBoxValue.Settled -> {
                    false
                }
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
        enableDismissFromEndToStart = enableSwipeToRemove,
        enableDismissFromStartToEnd = enableSwipeToRemove,
        backgroundContent = {
            SwipeToDismissBackground(
                dismissState = dismissState,
                iconLeft = iconLeft,
                iconRight = iconRight
            )
        }
    ) { foregroundView() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBackground(
    dismissState: SwipeToDismissBoxState,
    iconLeft: ImageVector = Icons.Default.Delete,
    iconRight: ImageVector = Icons.Default.PlaylistAdd
) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.errorContainer
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
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
        if (direction == SwipeToDismissBoxValue.StartToEnd) {
            Icon(iconLeft, contentDescription = "delete")
        }
        Spacer(modifier = Modifier)
        if (direction == SwipeToDismissBoxValue.EndToStart) {
            Icon(iconRight, contentDescription = "Add to playlist")
        }
    }
}
