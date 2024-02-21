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
package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.presentation.common.TopBarCircularProgress
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongDetailTopBar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .padding(
                vertical = 5.dp,
                horizontal = 8.dp
            )
    ) {
        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .padding(dimensionResource(id = R.dimen.close_handle_icon_padding)),
            //contentScale = ContentScale.FillHeight,
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "close song detail screen",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.basicMarquee(),
                text = mainViewModel.state.song?.title ?: Constants.ERROR_TITLE,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier
                .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
            Text(
                modifier = Modifier.basicMarquee(),
                text = mainViewModel.state.song?.artist?.name ?: Constants.ERROR_TITLE,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }

        TopBarCircularProgress(
            isLoading = mainViewModel.isLoading || mainViewModel.state.isDownloading,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}
