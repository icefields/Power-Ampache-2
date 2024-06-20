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
package luci.sixsixsix.powerampache2.presentation.screens.artists.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Artist

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistItem(
    artist: Artist,
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.aspectRatio(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .sizeIn(maxHeight = 200.dp)
                   // .border(1.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
                ,
                model = artist.artUrl,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.placeholder_album),
                contentDescription = artist.name,
                contentScale = ContentScale.Crop
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = artist.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = (14).sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(1.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (artist.songCount > 0) {
                    Text(
                        modifier = Modifier.basicMarquee(),
                        text = artistSubtitle(artist.songCount, artist.albumCount),
                        fontStyle = FontStyle.Italic,
                        fontSize = 9.sp,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

private fun artistSubtitle(songCount: Int, albumCount: Int) = StringBuilder().run {
    if (songCount > 0) {
        append("Songs ")
        append(songCount)
    }
    if (songCount > 0 && albumCount > 0) {
        append(" | ")
    }
    if (albumCount > 0) {
        append("Albums ")
        append(albumCount)
    }
    toString()
}

@Composable
@Preview(widthDp = 150, heightDp = 150)
fun PreviewArtistItem() {
    ArtistItem(artist = Artist.mockArtist())
}
