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
package luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton
import luci.sixsixsix.powerampache2.presentation.common.StarRatingButton
import luci.sixsixsix.powerampache2.presentation.common.TopBarCircularProgress

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistDetailTopBar(
    navigator: DestinationsNavigator,
    playlist: Playlist,
    isLoading: Boolean,
    isRatingVisible: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior,
    onRating: (Playlist, Int) -> Unit,
    onToggleSort: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        title = {
            Text(
                modifier = Modifier.basicMarquee().padding(15.dp),
                text = playlist.name,
                maxLines = 1,
                fontWeight = FontWeight.Normal,
                style = TextStyle(
                    fontSize = fontDimensionResource(id = R.dimen.albumDetail_title_fontSize),
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.background,
                        offset = Offset(0.0f, 0.0f),
                        blurRadius = 32f
                    )
                ),
                fontSize = fontDimensionResource(id = R.dimen.albumDetail_title_fontSize)

            )
        },
        navigationIcon = {
            CircleBackButton(background = Color.Transparent) {
                navigator.navigateUp()
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            TopBarCircularProgress(isLoading)
            if (isRatingVisible) {
                StarRatingButton(
                    currentRating = playlist.rating,
                    onRate = {
                        onRating(playlist, it)
                    }
                )
            }
            IconButton(onClick = onToggleSort) {
                Icon(imageVector = Icons.Outlined.Sort, contentDescription = "sorting")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun PlaylistDetailTopBarTopBarPreview() {
    PlaylistDetailTopBar(
        navigator = EmptyDestinationsNavigator,
        playlist = Playlist.mock(),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()),
        onRating = { _, _ -> },
        onToggleSort = {},
        isLoading = true
    )
}
