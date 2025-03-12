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
package luci.sixsixsix.powerampache2.presentation.screens.albums.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdownMenu(
    currentSelection: AlbumSortOrder,
    expanded: Boolean,
    onSelection: (AlbumSortOrder) -> Unit,
    onDismissMenu: () -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(!expanded) } //{ expanded = !expanded }
    ) {
        OutlinedButton(
            modifier = Modifier.menuAnchor(),
            onClick = {
                onExpandedChange(true)
            }
        ) { Text(text = albumSortOrderToLocalizedString(currentSelection), fontSize = 12.sp) }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissMenu
        ) {
            AlbumSortOrder.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(albumSortOrderToLocalizedString(option)) },
                    onClick = {
                        if (option != currentSelection) onSelection(option)
                        onDismissMenu()
                    }
                )
            }
        }
    }
}

@Composable
private fun albumSortOrderToLocalizedString(currentSelection: AlbumSortOrder) = when(currentSelection) {
    AlbumSortOrder.NAME -> "Name"
    AlbumSortOrder.YEAR -> "Year"
    AlbumSortOrder.ARTIST -> "Artist"
    AlbumSortOrder.RATING -> "Rating"
    AlbumSortOrder.AVERAGE_RATING -> "Average Rating"
}
