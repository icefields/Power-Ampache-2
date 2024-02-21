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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.presentation.common.ButtonDownload
import luci.sixsixsix.powerampache2.presentation.common.ButtonWithLoadingIndicator

@Composable
fun AlbumInfoButtonsRow(
    album: Album,
    isPlayingAlbum: Boolean,
    isAlbumDownloaded: Boolean,
    isPlaylistEditLoading: Boolean,
    isDownloading: Boolean,
    isGlobalShuffleOn: Boolean,
    modifier: Modifier = Modifier,
    eventListener: (albumInfoViewEvents: AlbumInfoViewEvents) -> Unit
) {
    Row(modifier = modifier
        .padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_chipsRow_padding)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ButtonWithLoadingIndicator(
            imageVector = Icons.Outlined.AddBox,
            imageContentDescription = "Add to playlist",
            background = Color.Transparent,
            isLoading = isPlaylistEditLoading,
            showBoth = true
        ) {
            eventListener(AlbumInfoViewEvents.ADD_ALBUM_TO_PLAYLIST)
        }

        ButtonDownload(
            isDownloaded = isAlbumDownloaded,
            isDownloading = isDownloading,
            onStartDownloadClick = { eventListener(AlbumInfoViewEvents.DOWNLOAD_ALBUM) },
            onStopDownloadClick = { eventListener(AlbumInfoViewEvents.STOP_DOWNLOAD_ALBUM) }
        )

        IconButton(modifier = Modifier
            .height(80.dp)
            .widthIn(min = 80.dp, max = 100.dp),
            onClick = {
                eventListener(AlbumInfoViewEvents.PLAY_ALBUM)
            }) {
            Icon(
                modifier = Modifier.aspectRatio(1f/1f),
                imageVector = if (!isPlayingAlbum) Icons.Default.PlayCircle else Icons.Default.PauseCircle, // Pause
                contentDescription = "Play"
            )
        }

        IconButton(
            onClick = {
                eventListener(AlbumInfoViewEvents.SHUFFLE_PLAY_ALBUM)
            }) {
            Icon(
                //modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.ic_shuffleplay),
                contentDescription = "Share",
                tint = if (isGlobalShuffleOn) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = {
                eventListener(AlbumInfoViewEvents.SHARE_ALBUM)
            }) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share"
            )
        }
    }
}

@Composable @Preview
fun AlbumInfoButtonsRowPreview() {
    AlbumInfoButtonsRow(Album.mock(), isPlayingAlbum = true, false, isDownloading = false, isGlobalShuffleOn = true, isPlaylistEditLoading = true){}
}
