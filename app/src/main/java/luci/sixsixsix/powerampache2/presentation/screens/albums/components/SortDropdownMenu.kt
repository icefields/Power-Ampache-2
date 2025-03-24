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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdownMenu(
    modifier: Modifier = Modifier,
    currentSelection: AlbumSortOrder,
    expanded: Boolean,
    onSelection: (AlbumSortOrder) -> Unit,
    onDismissMenu: () -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    val icon = if (expanded) { Icons.Filled.KeyboardArrowUp } else { Icons.Filled.KeyboardArrowDown }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { onExpandedChange(!expanded) } //{ expanded = !expanded }
    ) {
        DropdownMenuItem(
            modifier = Modifier.menuAnchor(),
            text = { Text(albumSortOrderToLocalizedString(currentSelection), maxLines = 1) },
            onClick = {
                if (expanded) onDismissMenu() else onExpandedChange(true)
            },
            trailingIcon = { Icon( icon, contentDescription = "", tint = MaterialTheme.colorScheme.primary) }
        )

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
    AlbumSortOrder.NAME -> stringResource(R.string.albums_sortOrder_name)
    AlbumSortOrder.YEAR -> stringResource(R.string.albums_sortOrder_year)
    AlbumSortOrder.ARTIST -> stringResource(R.string.albums_sortOrder_artist)
    AlbumSortOrder.RATING -> stringResource(R.string.albums_sortOrder_rating)
    AlbumSortOrder.AVERAGE_RATING -> stringResource(R.string.albums_sortOrder_averageRating)
}
