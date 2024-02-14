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

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

@Composable
fun PowerAmpCheckBox(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes subtitle: Int? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit),
) {
    Row(
        modifier = modifier.alpha(if (!enabled) { 0.5f } else { 1f }),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithSubtitle(
            modifier = Modifier.weight(1f),
            title = title, subtitle = subtitle,
            onClick = { onCheckedChange(!checked) }
        )
        Checkbox(
            modifier = Modifier.padding(start = 10.dp),
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = true
        )
    }
}

@Preview
@Composable
fun PowerAmpCheckBoxPreview() {
    PowerAmpCheckBox(
        title = R.string.settings_enableDebugLogging_title,
        subtitle = R.string.settings_enableDebugLogging_subtitle,
        checked = false,
        enabled = false,
        onCheckedChange = {},
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.settings_padding_horizontal_item)).background(
            Color.White)
    )
}
