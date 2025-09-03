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
package luci.sixsixsix.powerampache2.presentation.screens.main.screens.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.ui.theme.errorDark
import luci.sixsixsix.powerampache2.ui.theme.onBackgroundDark
import luci.sixsixsix.powerampache2.ui.theme.onSurfaceVariantDark
import luci.sixsixsix.powerampache2.ui.theme.surfaceContainerDark

@Composable fun LoginTextField(
    @StringRes label: Int,
    value: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit)
{
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        label = {
            Text(text = stringResource(id = label))
        },
        maxLines = 1,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = onBackgroundDark,
            unfocusedTextColor = onSurfaceVariantDark,
            unfocusedLabelColor = onSurfaceVariantDark,
            errorTextColor = errorDark,
            focusedContainerColor = surfaceContainerDark,
            unfocusedContainerColor = surfaceContainerDark
        )
    )
}
