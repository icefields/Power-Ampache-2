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
package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.Song

import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel

val textPaddingVertical = 10.dp

data class AddToPlaylistOrQueueDialogOpen(
    val isOpen: Boolean,
    val songs: List<Song> = listOf()
)

/**
 * this Dialog handles adding a song or a list of songs to a playlist
 */
@Composable
fun AddToPlaylistOrQueueDialog(
    songs: List<Song>,
    onDismissRequest: () -> Unit,
    onCreatePlaylistRequest: (success: Boolean) -> Unit = {},
    mainViewModel: MainViewModel,
    viewModel: AddToPlaylistOrQueueDialogViewModel
) {
    val playlistsState by viewModel.playlistsStateFlow.collectAsState()

    var headerBgColour by remember { mutableStateOf(Color.Transparent) }
    // workaround, not allowed to call RandomThemeBackgroundColour() inside remember block
    if (headerBgColour == Color.Transparent)
        headerBgColour = RandomThemeBackgroundColour()

    var listBgColour by remember { mutableStateOf(Color.Transparent) }
    // workaround, not allowed to call RandomThemeBackgroundColour() inside remember block
    if (listBgColour == Color.Transparent)
        listBgColour = RandomThemeBackgroundColour()

    var createPlaylistDialogOpen by remember { mutableStateOf(false) }

    val textColor = MaterialTheme.colorScheme.onSurface

    if (createPlaylistDialogOpen) {
        CreateAddToPlaylist(
            viewModel = viewModel,
            songs = songs,
            onConfirm = { _, _ ->
                createPlaylistDialogOpen = false
                onDismissRequest()
            },
            onCancel = {
                createPlaylistDialogOpen = false
            }
        )
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .alpha(
                    if (createPlaylistDialogOpen) 0.1f else 1.0f
                ),
            shape = RoundedCornerShape(4.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.addToPlaylistOrQueueDialog_playlists),
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(vertical = textPaddingVertical),
                    textAlign = TextAlign.Center,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Divider()
                PlaylistDialogItem(
                    title = stringResource(id = R.string.addToPlaylistOrQueueDialog_createNew),
                    icon = Icons.Default.Add,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            createPlaylistDialogOpen = true
                        },
                    backgroundColour = headerBgColour
                )
                Divider()
                PlaylistDialogItem(
                    title = stringResource(id = R.string.addToPlaylistOrQueueDialog_addQueue),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            addToQueue(mainViewModel, viewModel, songs = songs)
                            onDismissRequest()
                        },
                    backgroundColour = headerBgColour
                )
                Divider()
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    items(playlistsState) { playlist ->
                        PlaylistDialogItem(
                            title = playlist.name,
                            backgroundColour = listBgColour,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    addToPlaylist(viewModel, songs, playlist)
                                    onDismissRequest()
                                }
                        )
                    }
                }
            }
        }
    }
}

private fun addToPlaylist(
    viewModel: AddToPlaylistOrQueueDialogViewModel,
    songs: List<Song>,
    playlist: Playlist
) {
    when (songs.size) {
        1 -> viewModel.onEvent(
            AddToPlaylistOrQueueDialogEvent.AddSongToPlaylist(
                song = songs[0],
                playlistId = playlist.id
            )
        )
        else -> {
            viewModel.onEvent(
                AddToPlaylistOrQueueDialogEvent.AddSongsToPlaylist(
                    songs = songs,
                    playlist = playlist
                )
            )
        }
    }
}

private fun addToQueue(mainViewModel: MainViewModel, viewModel: AddToPlaylistOrQueueDialogViewModel, songs: List<Song>) {
    when (songs.size) {
        1 -> mainViewModel.onEvent(MainEvent.OnAddSongToQueue(songs[0]))
        else -> {
            viewModel.onEvent(AddToPlaylistOrQueueDialogEvent.OnAddAlbumToQueue(songs = songs))
        }
    }
}

@Composable
private fun CreateAddToPlaylist(
    viewModel: AddToPlaylistOrQueueDialogViewModel,
    songs: List<Song>,
    onConfirm: (playlistName: String, playlistType: PlaylistType) -> Unit,
    onCancel: () -> Unit
) {
    NewPlaylistDialog(
        onConfirm = { playlistName, playlistType ->
            viewModel.onEvent(
                when (songs.size) {
                    1 -> AddToPlaylistOrQueueDialogEvent.CreatePlaylistAndAddSong(
                        song = songs[0],
                        playlistName = playlistName,
                        playlistType = playlistType
                    )
                    else -> {
                        AddToPlaylistOrQueueDialogEvent.CreatePlaylistAndAddSongs(
                            songs = songs,
                            playlistName = playlistName,
                            playlistType = playlistType
                        )
                    }
                }
            )
            onConfirm(playlistName, playlistType)
        }
    ) {
        onCancel()
    }
}

@Composable
fun PlaylistDialogItem(
    modifier: Modifier,
    title: String,
    icon: ImageVector? = null,
    iconContentDescription: String = title,
    backgroundColour: Color = RandomThemeBackgroundColour(),
    fontWeight: FontWeight = FontWeight.Normal
) {
    val backgroundColourState by remember { mutableStateOf(backgroundColour) }

    Card(
        // border = BorderStroke((0.0).dp, MaterialTheme.colorScheme.background),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColourState),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
            icon?.let {
                Icon(imageVector = it, contentDescription = iconContentDescription)
                PlaylistDialogItemQueueText(
                    modifier = Modifier.wrapContentSize(),
                    title = title,
                    fontWeight = fontWeight
                )
            } ?: PlaylistDialogItemQueueText(
                title = title,
                fontWeight = fontWeight
            )
        }

    }
}

@Composable
fun PlaylistDialogItemQueueText(
    modifier:Modifier = Modifier.fillMaxWidth(),
    title: String,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = textPaddingVertical),
        text = title,
        fontWeight = fontWeight,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
}
