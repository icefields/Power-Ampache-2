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
package luci.sixsixsix.powerampache2.presentation.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongItemEvent

@Composable
fun SongDropDownMenu(
    modifier: Modifier = Modifier,
    isContextMenuVisible: Boolean,
    pressOffset: DpOffset,
    isSongDownloaded: Boolean,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    onDismissRequest:() -> Unit
) {
    DropdownMenu(
        modifier = modifier.padding(dimensionResource(id = R.dimen.songDropdown_padding)),
        expanded = isContextMenuVisible,
        offset = pressOffset.copy(
            y = pressOffset.y,
            x = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.toDp() }
        ),
        onDismissRequest = onDismissRequest
    ) {
        SongDropDownMenuItem(
            text = R.string.dropdownMenu_item_playNext,
            iconImageVector = Icons.Default.PlaylistPlay
        ) {
            songItemEventListener(SongItemEvent.PLAY_NEXT)
        }
        SongDropDownMenuItem(
            text = R.string.dropdownMenu_item_addToQueue,
            iconImageVector = Icons.Default.QueueMusic
        ) {
            songItemEventListener(SongItemEvent.ADD_SONG_TO_QUEUE)
        }
        SongDropDownMenuItem(
            text = R.string.dropdownMenu_item_addToPlaylist,
            iconImageVector = Icons.Default.PlaylistAdd
        ) {
            songItemEventListener(SongItemEvent.ADD_SONG_TO_PLAYLIST)
        }
        SongDropDownMenuItem(
            text = R.string.dropdownMenu_item_goToAlbum,
            iconImageVector = Icons.Default.Album
        ) {
            songItemEventListener(SongItemEvent.GO_TO_ALBUM)
        }
        SongDropDownMenuItem(
            text = R.string.dropdownMenu_item_goToArtist,
            iconImageVector = Icons.Default.Audiotrack
        ) {
            songItemEventListener(SongItemEvent.GO_TO_ARTIST)
        }
        SongDropDownMenuItem(
            text = R.string.dropdownMenu_item_share,
            iconImageVector = Icons.Default.Share
        ) {
            songItemEventListener(SongItemEvent.SHARE_SONG)
        }
        if (isSongDownloaded) {
            SongDropDownMenuItem(
                text = R.string.dropdownMenu_item_export,
                iconImageVector = Icons.Default.SaveAlt
            ) {
                songItemEventListener(SongItemEvent.EXPORT_DOWNLOADED_SONG)
            }
        } else {
            SongDropDownMenuItem(
                text = R.string.dropdownMenu_item_download,
                iconImageVector = Icons.Default.Download
            ) {
                songItemEventListener(SongItemEvent.DOWNLOAD_SONG)
            }
        }
    }
}

@Composable
fun SongDropDownMenuItem(
    text: String,
    iconImageVector: ImageVector,
    contentDescription: String = text,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconImageVector,
                    contentDescription = contentDescription
                )
                Text(
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.songDropdown_item_padding_horizontal)),
                    text = text,
                    fontSize = fontDimensionResource(id = R.dimen.songDropdown_textSize)
                )
            }
        },
        onClick = onClick
    )
}

@Composable
fun SongDropDownMenuItem(
    @StringRes text: Int,
    iconImageVector: ImageVector,
    onClick: () -> Unit
) {
    val title = stringResource(id = text)
    SongDropDownMenuItem(
        text = title,
        iconImageVector = iconImageVector,
        contentDescription = title,
        onClick = onClick
    )
}

@Composable
@Preview
fun SongDropDownMenuItemPreview() {
    SongDropDownMenuItem(
        text = R.string.dropdownMenu_item_export,
        iconImageVector = Icons.Default.SaveAlt
    ) {
    }
}
