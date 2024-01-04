package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel

val textPaddingVertical = 10.dp

data class AddToPlaylistOrQueueDialogOpen(
    val isOpen: Boolean,
    val song: Song? = null
)

@Composable
fun AddToPlaylistOrQueueDialog(
    song: Song,
    onDismissRequest: () -> Unit,
    viewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // use same colour for the header
                val headerBgColour = RandomThemeBackgroundColour()
                Text(
                    text = "PLAYLISTS",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(vertical = textPaddingVertical),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                // Divider(modifier = Modifier.padding(vertical = 4.dp))
                PlaylistDialogItem(
                    title = "Add to Current Queue",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            mainViewModel.onEvent(MainEvent.OnAddSongToQueue(song))
                            onDismissRequest()
                        },
                    backgroundColour = headerBgColour
                )
                PlaylistDialogItem(
                    title = "Create New",
                    icon = Icons.Default.Add,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            mainViewModel.onEvent(MainEvent.OnAddSongToQueue(song))
                            onDismissRequest()
                        },
                    backgroundColour = headerBgColour
                )
                //Divider(modifier = Modifier.padding(vertical = 4.dp))
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    items(viewModel.state.playlists) { playlist ->
                        PlaylistDialogItem(
                            title = playlist.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onEvent(
                                        AddToPlaylistOrQueueDialogEvent.AddToPlaylist(
                                            song = song,
                                            playlistId = playlist.id
                                        )
                                    )
                                    onDismissRequest()
                                }
                        )
                        // Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
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
    Card(
        // border = BorderStroke((0.0).dp, MaterialTheme.colorScheme.background),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColour
        ),
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