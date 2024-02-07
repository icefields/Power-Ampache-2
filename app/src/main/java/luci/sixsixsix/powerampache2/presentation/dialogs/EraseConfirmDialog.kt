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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.presentation.common.RoundedCornerButton

@Composable
fun EraseConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String = "",
    icon: ImageVector = Icons.Default.Warning,
    iconContentDescription: String = "Warning"
) {
    AlertDialog(
        icon = null,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = dialogTitle,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(
                            horizontal = 6.dp,
                            vertical = 0.dp
                        ),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
            }

        },
        text = {
            Text(
                text = dialogText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 6.dp,
                        vertical = 0.dp
                    ),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            RoundedCornerButton(
                text = android.R.string.ok,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                borderEnabled = false
            ) {
                onConfirmation()
            }
        },
        dismissButton = {
            RoundedCornerButton(
                text = android.R.string.cancel,
                contentColor = MaterialTheme.colorScheme.primary,
                borderEnabled = false
            ) {
                onDismissRequest()
            }
        }
    )
}

@Composable @Preview
fun EraseConfirmDialogPreview() {
    EraseConfirmDialog(
        onDismissRequest = {},
        onConfirmation = {},
        dialogTitle = "title dialog erase",
        dialogText = "dialogText dialog erase"
    )
}
