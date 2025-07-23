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
package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton
import luci.sixsixsix.powerampache2.presentation.common.StarRatingButton
import luci.sixsixsix.powerampache2.presentation.common.TopBarCircularProgress
import luci.sixsixsix.powerampache2.presentation.dialogs.EraseConfirmDialog
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlbumDetailTopBar(
    navigator: DestinationsNavigator,
    album: Album,
    isLoading: Boolean,
    isEditingPlaylist: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    showInfoIcon: Boolean = false,
    onRate: (Int) -> Unit,
    onRightIconClick: () -> Unit
) {

    var exitWarningVisible by remember { mutableStateOf(false) }
    AnimatedVisibility(visible = exitWarningVisible) {
        EraseConfirmDialog(
            onDismissRequest = { exitWarningVisible = false },
            onConfirmation = { navigator.navigateUp() },
            dialogTitle = stringResource(id = R.string.warning_playlist_adding_title),
            dialogText = stringResource(id = R.string.warning_playlist_adding),
            buttonOkText = R.string.warning_playlist_adding_leave,
            buttonCancelText = R.string.warning_playlist_adding_stay
        )
    }

    LargeTopAppBar(
        modifier = Modifier.background(Color.Transparent),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        title = {
            Text(
                modifier = Modifier
                    .basicMarquee()
                    .padding(15.dp),
                text = "${album.name} - ${album.artist.name}",
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                style = TextStyle(
                    fontSize = fontDimensionResource(id = R.dimen.albumDetail_title_fontSize),
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.surface,
                        offset = Offset(0.0f, 0.0f),
                        blurRadius = 18f
                    )
                ),
                fontSize = fontDimensionResource(id = R.dimen.albumDetail_title_fontSize)

            )
        },
        navigationIcon = {
            CircleBackButton {
                if (!isEditingPlaylist) {
                    navigator.navigateUp()
                } else {
                    exitWarningVisible = true
                }
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            TopBarCircularProgress(isLoading)

            if (showInfoIcon) {
                IconButton(onClick = onRightIconClick) { Icon(imageVector = Icons.Outlined.Info,
                    contentDescription = "show hide album info") }
            }
            StarRatingButton(
                currentRating = album.rating,
                onRate = onRate
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun AlbumDetailTopBarPreview() {
    AlbumDetailTopBar(
        navigator = EmptyDestinationsNavigator,
        album = Album(
            name = "Peace Sells, But Who's Buying",
            artist = MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
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
            year = 1986),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()),
        onRightIconClick = {},
        onRate = {},
        isLoading = true,
        isEditingPlaylist = true,
    )
}
