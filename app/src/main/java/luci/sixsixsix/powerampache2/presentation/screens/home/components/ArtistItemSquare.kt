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
package luci.sixsixsix.powerampache2.presentation.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.presentation.screens.artists.components.ArtistItem

@Composable
fun ArtistItemSquare(
    modifier: Modifier = Modifier,
    imageSize: Dp = dimensionResource(id = R.dimen.home_album_item_image_size_default),
    item: Artist
) {
    val paddingHorizontal = dimensionResource(id = R.dimen.home_album_item_image_text_padding)
    val totWidth = imageSize + paddingHorizontal + paddingHorizontal

    Column(
        modifier = modifier.widthIn(max = totWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArtistItem(
            modifier = Modifier.size(imageSize).padding(paddingHorizontal),
            artist = item,
            showText = false
        )
        HomeItemText(
            horizontalAlignment = Alignment.CenterHorizontally,
            textAlign = TextAlign.Center,
            title = item.name,
            subtitle = stringResource(id = R.string.home_artist_subtitle_item)
        )
    }
}
