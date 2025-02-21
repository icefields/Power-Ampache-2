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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.common.openLinkInBrowser

@Composable
fun AboutImageWithLink(
    imgUrl: String,
    link: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val applicationContext = LocalContext.current.applicationContext
    AsyncImage(
        modifier = modifier
            .clickable { applicationContext.openLinkInBrowser(link) },
        contentScale = ContentScale.FillWidth,
        model = imgUrl,
        contentDescription = contentDescription
    )
}

@Composable
fun AboutImageWithLink(
    @DrawableRes imgId: Int,
    link: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    val applicationContext = LocalContext.current.applicationContext
    if (tint == null) {
        Image(
            painter = painterResource(id = imgId),
            modifier = modifier
                .padding(horizontal = 11.dp)
                .clickable { applicationContext.openLinkInBrowser(link) },
            contentScale = ContentScale.Fit,
            contentDescription = contentDescription
        )
    } else {
        Icon(
            tint = tint,
            painter = painterResource(id = imgId),
            modifier = modifier
                .padding(horizontal = 11.dp)
                .clickable { applicationContext.openLinkInBrowser(link) },
            contentDescription = contentDescription,
        )
    }
}
