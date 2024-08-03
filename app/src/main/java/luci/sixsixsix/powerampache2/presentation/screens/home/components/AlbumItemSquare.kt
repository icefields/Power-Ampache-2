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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album

@Composable
fun AlbumItemSquare(
    modifier: Modifier = Modifier,
    imageSize: Dp = dimensionResource(id = R.dimen.home_album_item_image_size_default),
    album: Album
) {
    val paddingHorizontal = dimensionResource(id = R.dimen.home_album_item_image_text_padding)
    val width = imageSize + paddingHorizontal + paddingHorizontal
    Column(
        modifier = modifier
            .width(width)
            .padding(horizontal = paddingHorizontal)
    ) {
        Card(
            //border = BorderStroke((0.5).dp, MaterialTheme.colorScheme.background),
            modifier = Modifier.background(Color.Transparent),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f/1f)
                    .size(imageSize),
                model = album.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.placeholder_album),
                contentDescription = album.name,
            )
        }
        HomeItemText(title = album.name, subtitle = album.artist.name)
    }
}
