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
package luci.sixsixsix.powerampache2.presentation.dialogs.info.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoDialogText(key: String? = null, text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp, horizontal = 6.dp)) {
        key?.let {
            Text(
                color = MaterialTheme.colorScheme.secondary,
                text = it,
                modifier = Modifier
                    .wrapContentSize(Alignment.CenterStart),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            modifier = Modifier.padding(start = if (key == null || key.isBlank() == true) 0.dp else 16.dp)
                .wrapContentSize(Alignment.CenterStart),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        )
    }
}

@Composable @Preview
fun InfoDialogTextPreview() {
    InfoDialogText("Author","Necrophagist")
}
