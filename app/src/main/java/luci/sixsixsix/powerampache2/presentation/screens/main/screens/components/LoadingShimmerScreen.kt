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
package luci.sixsixsix.powerampache2.presentation.screens.main.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.common.shimmer


@Composable
fun LoadingShimmerScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 0.dp).padding(top = 66.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(5) {
                LoadingShimmerScreenSection()
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
        ) { }
    }
}

@Composable
fun LoadingShimmerScreenSection() {
    Column() {
        Text(
            modifier = Modifier
                .width(200.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .shimmer(),
            text = "",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            items(4) {
                AlbumItemSquareLoading()
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.home_row_spacing)))
    }
}

@Composable
fun AlbumItemSquareLoading(
    modifier: Modifier = Modifier,
    imageSize: Dp = dimensionResource(id = R.dimen.home_album_item_image_size_default),
) {
    val paddingHorizontal = dimensionResource(id = R.dimen.home_album_item_image_text_padding)
    val width = imageSize + paddingHorizontal + paddingHorizontal
    Column(
        modifier = modifier
            .width(width)
            .padding(horizontal = paddingHorizontal)
    ) {
        Card(
            modifier = Modifier.background(Color.Transparent),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f / 1f)
                    .size(imageSize)
                    .shimmer(),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            modifier = modifier
                .heightIn(min = 55.dp)
                .padding(horizontal = 4.dp, vertical = 1.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.width(130.dp).shimmer(),
                text = "",
                fontSize = fontDimensionResource(id = R.dimen.home_album_title_fontSize),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                textAlign = TextAlign.Start,
                lineHeight = fontDimensionResource(id = R.dimen.home_album_title_lineHeight)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.width(170.dp).shimmer(),
                text = "",
                textAlign = TextAlign.Start,
                maxLines = 1,
                fontSize = fontDimensionResource(id = R.dimen.home_album_artist_fontSize),
                fontWeight = FontWeight.Normal
            )

        }
    }
}
