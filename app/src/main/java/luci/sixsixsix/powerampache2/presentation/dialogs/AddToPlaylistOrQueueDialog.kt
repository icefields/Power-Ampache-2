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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel

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
    var headerBgColour by remember { mutableStateOf(Color.Transparent) }
    // workaround, not allowed to call RandomThemeBackgroundColour() inside remember block
    if (headerBgColour == Color.Transparent)
        headerBgColour = RandomThemeBackgroundColour()

    var listBgColour by remember { mutableStateOf(Color.Transparent) }
    // workaround, not allowed to call RandomThemeBackgroundColour() inside remember block
    if (listBgColour == Color.Transparent)
        listBgColour = RandomThemeBackgroundColour()

    var createPlaylistDialogOpen by remember { mutableStateOf(false) }

    if (createPlaylistDialogOpen) {
        L("createPlaylistDialogOpen")
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
                    text = "PLAYLISTS",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(vertical = textPaddingVertical),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Divider()
                PlaylistDialogItem(
                    title = "Create New",
                    icon = Icons.Default.Add,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            //onCreatePlaylistRequest()
                            L("PlaylistDialogItem createPlaylistDialogOpen")
                            createPlaylistDialogOpen = true
                        },
                    backgroundColour = headerBgColour
                )
                Divider()
                PlaylistDialogItem(
                    title = "Add to Current Queue",
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
                    items(viewModel.state.playlists) { playlist ->
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
                        //Divider()
                    }
                }
            }
        }
    }
}

private fun addToPlaylist(viewModel: AddToPlaylistOrQueueDialogViewModel, songs: List<Song>, playlist: Playlist) {
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
    onConfirm: (playlistName: String, playlistType: MainNetwork.PlaylistType) -> Unit,
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
    backgroundColour: Color = RandomThemeBackgroundColour()
) {
    var backgroundColourState by remember { mutableStateOf(backgroundColour) }

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
                    modifier = Modifier.wrapContentSize(),title = title)
            } ?: PlaylistDialogItemQueueText(title = title)
        }

    }
}

@Composable
fun PlaylistDialogItemQueueText( modifier:Modifier = Modifier.fillMaxWidth(), title: String) {
    Text(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = textPaddingVertical),
        text = title,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )
}
