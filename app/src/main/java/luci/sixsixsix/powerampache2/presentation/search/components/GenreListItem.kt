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
package luci.sixsixsix.powerampache2.presentation.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Genre
import java.util.UUID

@Composable
fun GenreListItem(
    genre: Genre,
    modifier: Modifier = Modifier,
    onGenreSelected: (playlist: Genre) -> Unit
) {
    ColourGenreListItem(
        genre = genre,
        modifier = modifier,
        colour = RandomThemeBackgroundColour("${genre.id}${genre.name}"), // random bg colour based on hashcode
        onGenreSelected = onGenreSelected
    )
}

@Composable
fun ColourGenreListItem(
    genre: Genre,
    modifier: Modifier = Modifier,
    colour: Color,
    onGenreSelected: (playlist: Genre) -> Unit
) {
    Card(
        //border = BorderStroke((0.0).dp, MaterialTheme.colorScheme.background),
        modifier = modifier
            .wrapContentHeight()
            .clickable {
                onGenreSelected(genre)
            },
        colors = CardDefaults.cardColors(
            containerColor = colour
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Icon(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(horizontal = 20.dp)
                    .align(Alignment.BottomEnd),
                painter = RandomThemeBackgroundColour.getBgEqImage(obj = genre),
                tint = Color(0x77ffffff),//MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                contentDescription = "background eq image"
            )

            Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 8.dp, top = 20.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        text = genre.name,
                        fontSize = fontDimensionResource(id = R.dimen.genre_title_fontSize),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        maxLines = 4,
                        minLines = 4,
                        lineHeight = fontDimensionResource(id = R.dimen.genre_title_lineHeight)
                    )

//                Text(
//                    //text = if (genre.songs > 0) {"Songs: ${genre.songs} " } else " ",
//                    text =  " ",
//                            fontSize = 14.sp,
//                            textAlign = TextAlign.Start,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    fontWeight = FontWeight.Normal,
//                            maxLines = 2,
//                            lineHeight = (16).sp
//                )
//
//                Text(text = " ", //if (genre.artists > 0) { "Artists: ${genre.artists} " } else " ",
//                    fontSize = 14.sp,
//                    textAlign = TextAlign.Start,
//                    fontWeight = FontWeight.Normal,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    maxLines = 2,
//                    lineHeight = (16).sp
//                )
            }


        }
    }
}


@Composable @Preview
fun PreviewPlaylistItemHome() {
    GenreListItem(
        genre = Genre(UUID.randomUUID().toString(), "Technical Death Metal",66,11,44,6)
    ) { }
}
