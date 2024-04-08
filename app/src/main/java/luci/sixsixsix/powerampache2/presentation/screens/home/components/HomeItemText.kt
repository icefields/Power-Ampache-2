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
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource

@OptIn(ExperimentalFoundationApi::class)
@Composable fun HomeItemText(
    title: String, subtitle: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier
            .heightIn(min = 55.dp)
            .padding(horizontal = 4.dp, vertical = 1.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = title,
            fontSize = fontDimensionResource(id = R.dimen.home_album_title_fontSize),
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            lineHeight = fontDimensionResource(id = R.dimen.home_album_title_lineHeight)
        )

        Text(
            modifier = Modifier.basicMarquee(),
            text = subtitle,
            fontSize = fontDimensionResource(id = R.dimen.home_album_artist_fontSize),
            fontWeight = FontWeight.Normal
        )

    }
}
