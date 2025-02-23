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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.GITHUB_URL
import luci.sixsixsix.powerampache2.common.Constants.MASTODON_URL
import luci.sixsixsix.powerampache2.common.Constants.TELEGRAM_URL
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.common.openLinkInBrowser

@Composable fun AboutSocialSection(
    modifier: Modifier = Modifier
) {
    val applicationContext = LocalContext.current.applicationContext
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.about_links_title),
            fontSize = fontDimensionResource(id = R.dimen.about_sectionTitle_fontSize),
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        AboutSocialRow(
            image = R.drawable.ic_telegram,
            contentDescription = R.string.telegram_contentDescription,
            text = R.string.about_telegram,
            link = TELEGRAM_URL
        )

        Spacer(Modifier.height(12.dp))

        AboutSocialRow(
            image = R.drawable.ic_mastodon,
            contentDescription = R.string.mastodon_contentDescription,
            text = R.string.about_mastodon,
            link = MASTODON_URL
        )

        AboutSocialRow(
            image = R.drawable.ic_github,
            contentDescription = R.string.about_sourceCode_title,
            text = R.string.about_github,
            link = GITHUB_URL,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable fun AboutSocialRow(
    @DrawableRes image: Int,
    @StringRes text: Int,
    @StringRes contentDescription: Int,
    link: String,
    tint: Color? = null
) {
    val applicationContext = LocalContext.current.applicationContext

    Row(
        modifier = Modifier.fillMaxWidth().clickable { applicationContext.openLinkInBrowser(link) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AboutImageWithLink(
            link = link,
            modifier = Modifier.size(66.dp),
            imgId = image,
            contentDescription = stringResource(contentDescription),
            tint = tint
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = stringResource(id = text),
            fontSize = fontDimensionResource(id = R.dimen.about_sectionTitle_fontSize),
            maxLines = 1,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



@Composable @Preview fun PreviewAboutSocialSection() {
    AboutSocialSection()
}
