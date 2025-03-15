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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder
import luci.sixsixsix.powerampache2.domain.models.SortOrder
import luci.sixsixsix.powerampache2.presentation.common.SortDirectionDropdownMenu

@Composable
fun AlbumsGridHeader(
    isHeaderVisible: Boolean,
    isLoading: Boolean,
    currentSortSelection: AlbumSortOrder,
    currentDirection: SortOrder,
    sortMenuExpanded: Boolean,
    sortDirectionExpanded: Boolean,
    onSortSelection: (AlbumSortOrder) -> Unit,
    onDirectionSelection: (SortOrder) -> Unit,
    onSortExpandedChange: (Boolean) -> Unit,
    onDirectionExpandedChange: (Boolean) -> Unit,
    onDismissMenu: () -> Unit
) {
    AnimatedVisibility(
        visible = isHeaderVisible,
        exit = slideOutVertically(),
        enter = fadeIn(spring(stiffness = Spring.StiffnessLow))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SortDropdownMenu(
                modifier = Modifier.fillMaxWidth(0.30f),
                currentSelection = currentSortSelection,
                expanded = sortMenuExpanded,
                onExpandedChange = onSortExpandedChange,
                onDismissMenu = onDismissMenu,
                onSelection = onSortSelection
            )

            if (!isLoading) {
                Box(modifier = Modifier
                    .width(150.dp)
                    .background(Color.Blue)
                )
            } else {
                Box(modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterVertically)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(8.dp).align(Alignment.Center)
                    )
                }
            }

            SortDirectionDropdownMenu(
                //modifier = Modifier.fillMaxWidth(0.30f),
                currentDirection = currentDirection,
                expanded = sortDirectionExpanded,
                onExpandedChange = onDirectionExpandedChange,
                onDismissMenu = onDismissMenu,
                onSelection = onDirectionSelection
            )
        }
    }
}
