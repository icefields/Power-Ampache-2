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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.presentation.common.RoundedCornerButton

@Composable
fun NewPlaylistDialog(
    onConfirm: (playlistName: String, playlistType: PlaylistType) -> Unit,
    onCancel: () -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    //var playlistType by remember { mutableStateOf(PlaylistType.private) }
    val radioOptions = listOf(PlaylistType.private, PlaylistType.public)
    var playlistType by remember { mutableStateOf(radioOptions[0]) }

    Dialog(onDismissRequest = { onCancel() }) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 30.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                OutlinedTextField(
                    value = playlistName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    onValueChange = {
                        playlistName = it
                    },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Playlist Name",
                            modifier = Modifier
                            //    .wrapContentSize(Alignment.Center)
                                .padding(vertical = 0.dp),
                            //textAlign = TextAlign.Center,
                            //fontWeight = FontWeight.Bold,
                            //fontSize = 15.sp
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                PlaylistTypeRadioButtons(
                    radioOptions = radioOptions,
                    selectedOption = playlistType,
                    onOptionSelected = {
                        playlistType = it
                    }
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    RoundedCornerButton(
                        text = android.R.string.cancel,
                        contentColor = MaterialTheme.colorScheme.primary,
                        borderEnabled = false
                    ) {
                        onCancel()
                    }

                    RoundedCornerButton(
                        text = R.string.dialog_create,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        borderEnabled = false
                    ) {
                        onConfirm(playlistName,playlistType)
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistTypeRadioButtons(
    radioOptions: List<PlaylistType>,
    selectedOption: PlaylistType,
    onOptionSelected: (PlaylistType) -> Unit
) {

    Row(
        Modifier
            .fillMaxWidth()) {
        radioOptions.forEach { type ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1.0f)
                    .selectable(
                        selected = (type == selectedOption),
                        onClick = {
                            onOptionSelected(type)
                        }
                    )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center),
                    selected = (type == selectedOption),
                    onClick = { onOptionSelected(type) }
                )
                Text(
                    text = type.name.capitalize(),
                   // modifier = Modifier
                        //.wrapContentSize(Alignment.Center)
                        //.padding(vertical = 5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable @Preview
fun PreviewNewPlaylistDialog() {
    NewPlaylistDialog(
        onCancel = {},
        onConfirm = { name, type ->

        }
    )
}
