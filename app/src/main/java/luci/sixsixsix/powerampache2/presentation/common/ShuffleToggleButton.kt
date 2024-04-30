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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

@Composable
fun ShuffleToggleButton(
    isGlobalShuffleOn: Boolean,
    showStroke: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (isGlobalShuffleOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val containerColor = /*if (isGlobalShuffleOn) MaterialTheme.colorScheme.onSurfaceVariant else*/ Color.Transparent
    val iconImage = if (isGlobalShuffleOn) R.drawable.ic_shuffle_all_active else R.drawable.ic_shuffle_all
    val strokeColor = if (!showStroke) {
        Color.Transparent
    } else {
        if (isGlobalShuffleOn) Color.Transparent
        else MaterialTheme.colorScheme.outline
    }

    OutlinedIconButton(
        border = BorderStroke(width = if (isGlobalShuffleOn || !showStroke) 0.dp else 2.dp, color = strokeColor),
        modifier = Modifier.size(48.dp).padding(8.dp).wrapContentSize(),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconImage),
            contentDescription = "shuffle play",
            tint = contentColor
        )
    }
}

@Composable
@Preview
fun ShuffleToggleButtonPreview() {
    ShuffleToggleButton(isGlobalShuffleOn = true, showStroke = false) { }
}
