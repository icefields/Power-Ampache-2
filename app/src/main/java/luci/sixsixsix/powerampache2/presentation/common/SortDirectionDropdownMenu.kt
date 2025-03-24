/**
 * Copyright (C) 2025  Antonio Tari
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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import luci.sixsixsix.powerampache2.domain.models.SortOrder

@Composable
fun SortDirectionDropdownMenu(
    modifier: Modifier = Modifier,
    currentDirection: SortOrder,
    expanded: Boolean,
    onSelection: (SortOrder) -> Unit,
    onDismissMenu: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
) {
    val icon = if (expanded) { Icons.Filled.ArrowUpward } else { Icons.Filled.ArrowDownward }

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(
            textAlign = TextAlign.End,
            text = currentDirection.order.uppercase(),
            maxLines = 1) },
        onClick = {
            onSelection(if (currentDirection == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC)
            if (expanded) onDismissMenu() else onExpandedChange(true)
        },
        trailingIcon = { Icon( icon, contentDescription = "Sort direction", tint = MaterialTheme.colorScheme.primary) }
    )
}
