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
package luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle

@Composable
fun PowerAmpEdit(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: ((String) -> Unit),
) {
    var text by remember { mutableStateOf(value) }

    Column(modifier = modifier.alpha(if (!enabled) { 0.5f } else { 1f })) {
        TextWithSubtitle(
            modifier = Modifier.fillMaxWidth(),
            title = title,
            subtitle = null
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                text,
                singleLine = true,
                modifier = Modifier.weight(3f),
                onValueChange = { text = it },
                enabled = true
            )
            Button(
                modifier = Modifier.wrapContentWidth().padding(start = 10.dp),
                onClick = { onValueChange(text) },
                enabled = true,
            ) {
                Text(stringResource(android.R.string.ok))
            }
        }

    }

}
