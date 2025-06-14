package luci.sixsixsix.powerampache2.presentation.dialogs

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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.TextListItemColoured

@Composable
fun ShareDialog(
    onDismissRequest: () -> Unit = { },
    onShareWeb: () -> Unit,
    onSharePowerAmpache: () -> Unit,
    @StringRes buttonShareWebText: Int = R.string.share_link_web,
    @StringRes buttonSharePowerAmpacheText: Int = R.string.share_link_pa2,
    @StringRes buttonCancelText: Int = android.R.string.cancel
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        },
        content = {
            Column {
                TextListItemColoured(buttonShareWebText, onShareWeb)
                TextListItemColoured(buttonSharePowerAmpacheText, onSharePowerAmpache)
                TextListItemColoured(buttonCancelText,
                    colour = MaterialTheme.colorScheme.onPrimary,
                    onClick = onDismissRequest)
            }
        }
    )
}

@Composable @Preview
fun ShareDialogDialogPreview() {
    ShareDialog(
        onDismissRequest = { },
        onShareWeb = { },
        onSharePowerAmpache = { }
    )
}
