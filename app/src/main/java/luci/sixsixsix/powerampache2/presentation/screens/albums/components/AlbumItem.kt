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
package luci.sixsixsix.powerampache2.presentation.screens.albums.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import java.util.UUID

@Composable
fun AlbumItem(album: Album, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(dimensionResource(R.dimen.albumItem_card_cornerRadius))
    ) {
        Box(modifier = Modifier.background(brush = albumBackgroundGradient)) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f / 1f)
                    .border(
                        dimensionResource(R.dimen.albumItem_card_border),
                        MaterialTheme.colorScheme.background
                    ),
                model = album.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.placeholder_album),
                contentDescription = album.name,
            )

            // Text Section
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxSize()
                    .background(brush = albumBackgroundGradient)
                    //.background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                    .padding(
                        start = dimensionResource(R.dimen.albumItem_infoText_paddingHorizontal),
                        end = dimensionResource(R.dimen.albumItem_infoText_paddingHorizontal),
                        top = 0.dp,
                        bottom = dimensionResource(R.dimen.albumItem_infoText_paddingVertical)
                    )
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = album.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_title),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        lineHeight = fontDimensionResource(R.dimen.albumItem_infoTextSection_lineHeight_title),
                        style = TextStyle(
                            fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_title),
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.background,
                                offset = Offset(0.0f, 0.0f),
                                blurRadius = 9f
                            )
                        )
                    )

                    Text(
                        modifier = Modifier.basicMarquee(),
                        color = MaterialTheme.colorScheme.onBackground,
                        text = album.artist.name,
                        fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_artist),
                        fontWeight = FontWeight.Normal,
                        style = TextStyle(
                            fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_artist),
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.background,
                                offset = Offset(0.0f, 0.0f),
                                blurRadius = 9f
                            )
                        )
                    )
            }
        }
    }
}

private val albumBackgroundGradient
    @Composable
    get() =
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
//                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
//                MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                MaterialTheme.colorScheme.background
            )
        )

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun AlbumItemPreview() {
    AlbumItem(
        Album(
            name = "Album title",
            time = 129,
            id = UUID.randomUUID().toString(),
            songCount = 11,
            genre = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Thrash Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Progressive Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Jazz"),
            ),
            artists = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Marty Friedman"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Other people"),
            ),
            year = 1986)
    )
}
