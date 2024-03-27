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
package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.presentation.common.DefaultFullWidthButton
import luci.sixsixsix.powerampache2.presentation.main.AuthEvent

@Composable
fun LoginButton(
    onEvent: (AuthEvent) -> Unit
) {
    DefaultFullWidthButton(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.bottomDrawer_login_padding_horizontal),
                vertical = 10.dp
            )
            .fillMaxWidth(),
        colours = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.primaryDark),
            contentColor = colorResource(id = R.color.onPrimaryDark)
        ),
        onClick = { onEvent(AuthEvent.Login) }
    ) {
        Icon(
            imageVector = Icons.Default.Login,
            contentDescription = "Login"
        )
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp, horizontal = 9.dp),
            text = stringResource(id = R.string.loginScreen_login),
            textAlign = TextAlign.Center,
            //fontWeight = FontWeight.SemiBold,
            fontSize = fontDimensionResource(id = R.dimen.button_login_text_size)
        )
    }
}
