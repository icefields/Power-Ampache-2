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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R

@Composable
fun DonateConsider(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(18.dp))
        DefaultFullWidthButton(
            modifier = modifier,
            borderStrokeColour = MaterialTheme.colorScheme.onSurface,
            onClick = onClick
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(10.dp)
                    .clickable {
                        onClick()
                    }
            ) {
                Text(
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.about_considerDonating),
                )
                Text(
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    text = stringResource(id = R.string.website),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
//    Card(
//        border = BorderStroke(
//            width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
//            color = MaterialTheme.colorScheme.onSurface
//        ),
//        modifier = Modifier.clickable {
//                onClick()
//            },
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
//        elevation = CardDefaults.cardElevation(0.dp),
//        shape = RoundedCornerShape(10.dp)
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = modifier
//                .padding(10.dp)
//                .clickable {
//                    onClick()
//                }
//        ) {
//            Text(
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center,
//                text = stringResource(id = R.string.about_considerDonating),
//            )
//            Text(
//                text = stringResource(id = R.string.website),
//            )
//        }
//    }
}

@Preview
@Composable
fun DonateConsiderPreview() {
    DonateConsider(modifier = Modifier.width(500.dp)) {
    }
}
