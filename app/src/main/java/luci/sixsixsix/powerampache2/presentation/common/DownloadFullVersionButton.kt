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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.common.goToPlayStore
import luci.sixsixsix.powerampache2.ui.theme.onPrimaryDark
import luci.sixsixsix.powerampache2.ui.theme.primaryDark
import java.lang.ref.WeakReference

@Composable
fun DownloadFullVersionButton() {
    val context = WeakReference(LocalContext.current)

    DefaultFullWidthButton(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal),
                vertical = 5.dp
            )
            .fillMaxWidth(),
        colours = ButtonDefaults.buttonColors(
            containerColor = primaryDark,
            contentColor = onPrimaryDark
        ),
        onClick = {
            context.get()?.goToPlayStore()
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = "Login"
                )
                Text(
                    modifier = Modifier
                        .padding(vertical = 1.dp, horizontal = 1.dp),
                    text = stringResource(id = R.string.download),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontDimensionResource(id = R.dimen.button_login_text_size)
                )
            }

            Text(
                modifier = Modifier
                    .padding(vertical = 1.dp, horizontal = 1.dp),
                text = stringResource(id = R.string.loginScreen_warning),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                fontSize = fontDimensionResource(id = R.dimen.button_login_text_size)
            )
        }
    }
}

@Composable @Preview
fun DownloadFullVersionButtonPreview() {
    DownloadFullVersionButton()
}
