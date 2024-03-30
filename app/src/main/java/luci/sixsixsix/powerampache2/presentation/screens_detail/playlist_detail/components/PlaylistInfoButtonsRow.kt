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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.common.ButtonDownload
import luci.sixsixsix.powerampache2.presentation.common.ButtonWithLoadingIndicator
import luci.sixsixsix.powerampache2.presentation.common.LikeButton
import luci.sixsixsix.powerampache2.presentation.common.ShuffleToggleButton

@Composable
fun PlaylistInfoButtonsRow(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    isPlayingPlaylist: Boolean,
    isDownloading: Boolean,
    isPlaylistEditLoading: Boolean,
    isGlobalShuffleOn: Boolean,
    isLikeAvailable: Boolean,
    isLiked: Boolean,
    isLikeLoading: Boolean,
    iconTint: Color,
    eventListener: (playlistInfoViewEvents: PlaylistInfoViewEvents) -> Unit
) {
    Row(modifier = modifier
        .padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_chipsRow_padding)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        ButtonWithLoadingIndicator(
            iconTint = iconTint,
            imageVector = Icons.Outlined.AddBox,
            imageContentDescription = "Add to playlist",
            background = Color.Transparent,
            isLoading = isPlaylistEditLoading,
            showBoth = true
        ) {
            eventListener(PlaylistInfoViewEvents.ADD_PLAYLIST_TO_PLAYLIST)
        }

        ButtonDownload(
            isDownloading = isDownloading,
            iconTint = iconTint,
            onStartDownloadClick = { eventListener(PlaylistInfoViewEvents.DOWNLOAD_PLAYLIST) },
            onStopDownloadClick = { eventListener(PlaylistInfoViewEvents.STOP_DOWNLOAD_PLAYLIST) }
        )

        IconButton(modifier = Modifier
            .height(80.dp)
            .widthIn(min = 80.dp, max = 100.dp),
            onClick = {
                eventListener(PlaylistInfoViewEvents.PLAY_PLAYLIST)
            }) {
            Icon(
                modifier = Modifier.aspectRatio(1f/1f),
                imageVector = if (!isPlayingPlaylist) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                contentDescription = "Play Pause",
                tint = iconTint
            )
        }

        ShuffleToggleButton(isGlobalShuffleOn = isGlobalShuffleOn) {
            eventListener(PlaylistInfoViewEvents.SHUFFLE_PLAY_PLAYLIST)
        }

        LikeButton(
            modifier = Modifier.alpha(if (isLikeAvailable) 1f else 0.1f),
            isLikeLoading = isLikeLoading, isFavourite = isLiked) {
            if (isLikeAvailable) {
                eventListener(PlaylistInfoViewEvents.LIKE_PLAYLIST)
            }
        }


        // TODO not possible to share playlists, is this a bug?
//        IconButton(
//            onClick = {
//                eventListener(PlaylistInfoViewEvents.SHARE_PLAYLIST)
//            }) {
//            Icon(
//                imageVector = Icons.Outlined.Share,
//                contentDescription = "Share"
//            )
//        }
    }
}
