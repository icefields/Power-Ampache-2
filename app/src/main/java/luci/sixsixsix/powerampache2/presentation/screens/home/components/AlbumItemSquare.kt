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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Album

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumItemSquare(modifier: Modifier = Modifier, album: Album) {
    val width = 150.dp
    val imageSize = 150.dp
    Column(
        modifier = modifier
            .width(width)
            .padding(horizontal = 4.dp)
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
                    .height(imageSize)
                    .width(imageSize),
                model = album.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.placeholder_album),
                contentDescription = album.name,
            )
        }

        Column(
            modifier = Modifier
                .heightIn(min = 55.dp)
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = album.name,
                fontSize = fontDimensionResource(id = R.dimen.home_album_title_fontSize),
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                lineHeight = fontDimensionResource(id = R.dimen.home_album_title_lineHeight)
            )

            Text(
                modifier = Modifier.basicMarquee(),
                text = album.artist.name,
                fontSize = fontDimensionResource(id = R.dimen.home_album_artist_fontSize),
                fontWeight = FontWeight.Normal
            )

        }
    }
}
