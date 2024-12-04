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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R

@Composable
fun TextWithSubtitle(
    modifier: Modifier = Modifier.fillMaxWidth(),
    @StringRes title: Int,
    @StringRes subtitle: Int? = null,
    trailingIcon: ImageVector? = null,
    trailingIconContentDescription: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = { }
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .alpha(
                if (!enabled) {
                    0.5f
                } else {
                    1f
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(id = title),
                fontSize = 14.sp,
                lineHeight = 14.sp
            )
            subtitle?.let { subtitle ->
                Text(
                    text = stringResource(id = subtitle),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
        trailingIcon?.let {
            Icon(imageVector = it, contentDescription = trailingIconContentDescription)
        }
    }
}

@Composable
@Preview
fun TextWithSubtitlePreview() {
    TextWithSubtitle(
        title = R.string.settings_enableDebugLogging_title,
        subtitle = R.string.settings_enableDebugLogging_title
    )
}
