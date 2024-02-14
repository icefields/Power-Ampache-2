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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

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
        modifier = Modifier.padding(4.dp),
        expanded = isContextMenuVisible,
        offset = pressOffset.copy(
            y = pressOffset.y,
            x = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.toDp() }
        ),
        onDismissRequest = onDismissRequest
    ) {

        DropdownMenuItem(
            text = {
                Row {
                    Icon(
                        imageVector = Icons.Default.PlaylistPlay,
                        contentDescription = stringResource(id = R.string.back_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = "Play next"
                    )
                } },
            onClick = {
                songItemEventListener(SongItemEvent.PLAY_NEXT)
            }
        )
        DropdownMenuItem(
            text = {
                Row {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = stringResource(id = R.string.back_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = "Add to queue"
                    )
                } },
            onClick = {
                songItemEventListener(SongItemEvent.ADD_SONG_TO_QUEUE)
            }
        )
        DropdownMenuItem(
            text = {
                Row {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = stringResource(id = R.string.back_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = "Add to playlist"
                    )
                } },
            onClick = {
                songItemEventListener(SongItemEvent.ADD_SONG_TO_PLAYLIST)
            }
        )
        DropdownMenuItem(
            text = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = stringResource(id = R.string.back_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = "Go to Album"
                    )
                } },
            onClick = {
                songItemEventListener(SongItemEvent.GO_TO_ALBUM)
            }
        )
        DropdownMenuItem(
            text = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Audiotrack,
                        contentDescription = stringResource(id = R.string.back_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = "Go to Artist"
                    )
                } },
            onClick = {
                songItemEventListener(SongItemEvent.GO_TO_ARTIST)
            }
        )
        DropdownMenuItem(
            text = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(id = R.string.back_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = "Share"
                    )
                } },
            onClick = {
                songItemEventListener(SongItemEvent.SHARE_SONG)
            }
        )
        if (isSongDownloaded) {
            DropdownMenuItem(
                text = {
                    Row {
                        Icon(
                            imageVector = Icons.Default.SaveAlt,
                            contentDescription = "Export Downloaded Song"
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = "Export Downloaded Song"
                        )
                    }
                },
                onClick = {
                    songItemEventListener(SongItemEvent.EXPORT_DOWNLOADED_SONG)
                }
            )
        } else {
            DropdownMenuItem(
                text = {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(id = R.string.back_content_description)
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = "Download"
                        )
                    }
                },
                onClick = {
                    songItemEventListener(SongItemEvent.DOWNLOAD_SONG)
                }
            )
        }
    }
}
