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
package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttributeText(
    modifier: Modifier = Modifier,
    title: String,
    name: String,
    fontSizeTitle: TextUnit = 14.sp,
    fontSizeName: TextUnit = 17.sp,
    fontWeightTitle: FontWeight = FontWeight.Normal,
    fontWeightName: FontWeight = FontWeight.Bold,
) {
    Row(modifier = modifier) {
        Text( // title
            modifier = Modifier.align(Alignment.CenterVertically),
            text = title,
            fontWeight = fontWeightTitle,
            fontSize = fontSizeTitle
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text( // name
            modifier = Modifier.align(Alignment.CenterVertically),
            text = name,
            fontWeight = fontWeightName,
            fontSize = fontSizeName
        )
    }
}
