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
package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.domain.models.Song

@Composable
fun InfoDialog(
    info: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                text = info,
                modifier = Modifier
                    .wrapContentSize(Alignment.CenterStart)
                    .padding(textPaddingVertical)
                    .verticalScroll(rememberScrollState())
                    .clickable {
                        onDismissRequest()
                    },
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp
            )
        }
    }
}

@Composable @Preview
fun InfoDialogPreview() {
    InfoDialog(
        Song.mockSong.toDebugString()
    ) {

    }
}
