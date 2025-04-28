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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import luci.sixsixsix.powerampache2.data.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.common.toDebugMap
import luci.sixsixsix.powerampache2.domain.models.Song

@Composable
fun InfoDialog(
    info: Map<String, String>,
    onDismissRequest: () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            LazyColumn(modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)) {
                items(info.keys.toList()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(3) { iRow ->
                                when(iRow) {
                                    0 -> Text(
                                        text = it,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clickable {
                                                onDismissRequest()
                                            },
                                        textAlign = TextAlign.Start,
                                        maxLines = 1,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 19.sp
                                    )
                                    1 -> Spacer(modifier = Modifier.width(6.dp))
                                    2 -> Text(
                                        text = info[it] ?: ERROR_STRING,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clickable {
                                                onDismissRequest()
                                            },
                                        textAlign = TextAlign.Start,
                                        maxLines = 1,
                                        fontWeight = FontWeight.Light,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun InfoDialog(
    info: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
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
        Song.mockSong.toDebugMap()
    ) {

    }
}
