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
package luci.sixsixsix.powerampache2.presentation.screens.settings.subscreens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.BUYMEACOFFEE_URL
import luci.sixsixsix.powerampache2.common.Constants.PATREON_IMG_URL
import luci.sixsixsix.powerampache2.common.Constants.PATREON_URL
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.common.openLinkInBrowser
import luci.sixsixsix.powerampache2.presentation.common.DonateConsider

@Composable
fun AboutSupportSection(
    onDonateConsiderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext

    Column (
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.about_support_title),
            fontSize = fontDimensionResource(id = R.dimen.about_sectionTitle_fontSize),
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        DonateConsider(onClick = onDonateConsiderClick)

        Spacer(modifier = Modifier.height(16.dp))

        AboutRoundCornerCard(
            modifier = Modifier.height(60.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bmc_brand_logo),
                modifier = Modifier.padding(8.dp)
                    .clickable { context.openLinkInBrowser(BUYMEACOFFEE_URL) },
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = stringResource(id = R.string.bmac_contentDescription),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AboutRoundCornerCard(
            modifier = Modifier.height(60.dp)
        ) {
            AboutImageWithLink(
                imgUrl = PATREON_IMG_URL,
                link = PATREON_URL,
                contentDescription = stringResource(id = R.string.patreon_contentDescription),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(90.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewAboutSupportSection() {
    AboutSupportSection(onDonateConsiderClick = {})
}
