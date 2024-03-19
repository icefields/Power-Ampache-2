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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.OfflinePin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Song

enum class SongDetailButtonEvents {
    SHARE_SONG,
    DOWNLOAD_SONG,
    DELETE_DOWNLOADED_SONG,
    ADD_SONG_TO_PLAYLIST_OR_QUEUE,
    GO_TO_ALBUM,
    GO_TO_ARTIST,
    SHOW_INFO
}

@Composable
fun SongDetailButtonRow(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    isOffline: Boolean,
    eventListener: (albumInfoViewEvents: SongDetailButtonEvents) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_chipsRow_padding))
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        PlayerButton(
            text = R.string.player_buttonText_add,
            icon = Icons.Outlined.AddBox,
            tint = tint
        ) {
            eventListener(SongDetailButtonEvents.ADD_SONG_TO_PLAYLIST_OR_QUEUE)
        }
        PlayerButton(
            text = R.string.player_buttonText_download,
            icon = if (!isOffline)
                Icons.Outlined.DownloadForOffline
            else
                Icons.Outlined.OfflinePin,
            tint = tint
        ) {
            if (!isOffline) {
                eventListener(SongDetailButtonEvents.DOWNLOAD_SONG)
            } else {
                eventListener(SongDetailButtonEvents.DELETE_DOWNLOADED_SONG)
            }
        }
        PlayerButton(
            text = R.string.player_buttonText_share,
            icon = Icons.Outlined.Share,
            tint = tint
        ) {
            eventListener(SongDetailButtonEvents.SHARE_SONG)
        }
        PlayerButton(
            text = R.string.player_buttonText_info,
            icon = Icons.Outlined.Info,
            tint = tint
        ) {
            eventListener(SongDetailButtonEvents.SHOW_INFO)
        }
        PlayerButton(
            text = R.string.player_buttonText_album,
            icon = Icons.Outlined.Album,
            tint = tint
        ) {
            eventListener(SongDetailButtonEvents.GO_TO_ALBUM)
        }
        PlayerButton(
            text = R.string.player_buttonText_artist,
            icon = Icons.Outlined.Audiotrack,
            tint = tint
        ) {
            eventListener(SongDetailButtonEvents.GO_TO_ARTIST)
        }
    }
}

@Composable
fun PlayerButton(
    @StringRes text: Int,
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    @StringRes contentDescription: Int = text,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerButtonIcon(
            icon = icon,
            contentDescription = contentDescription,
            tint = tint,
            onClick = onClick
        )
        PlayerButtonText(text = text, tint = tint)
    }
}

@Composable
fun PlayerButtonText(
    @StringRes text: Int,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = stringResource(id = text),
        fontSize = fontDimensionResource(id = R.dimen.player_buttonTitle_fontSize),
        fontWeight = FontWeight.Medium,
        color = tint
    )
}

@Composable
fun PlayerButtonIcon(
    icon: ImageVector,
    @StringRes contentDescription: Int,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            tint = tint,
            imageVector = icon,
            contentDescription = stringResource(id = contentDescription)
        )
    }
}

@Composable
@Preview
fun PreviewSongDetailButtonRow() {
    SongDetailButtonRow(
        modifier = Modifier.fillMaxWidth(),
        isOffline = false
    ) { }
}