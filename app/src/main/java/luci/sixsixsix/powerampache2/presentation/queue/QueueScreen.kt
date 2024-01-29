package luci.sixsixsix.powerampache2.presentation.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.ButtonWithLoadingIndicator
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.queue.components.QueueScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = false)
fun QueueScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    queueViewModel: QueueViewModel = hiltViewModel(),
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }

    if (playlistsDialogOpen.isOpen) {
        if(playlistsDialogOpen.songs.isNotEmpty()) {
            AddToPlaylistOrQueueDialog(
                songs = playlistsDialogOpen.songs,
                onDismissRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                },
                mainViewModel = mainViewModel,
                viewModel = addToPlaylistOrQueueDialogViewModel
            )
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.Transparent),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                title = {
                    Text(
                        text = "Queue",
                        maxLines = 1,
                        fontWeight = FontWeight.Normal,
                    )
                },
                navigationIcon = {
                    CircleBackButton {
                        navigator.navigateUp()
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    ButtonWithLoadingIndicator(
                        imageVector = Icons.Default.PlaylistAdd,
                        imageContentDescription = "add all songs in queue to playlist",
                        isLoading = addToPlaylistOrQueueDialogViewModel.state.isPlaylistEditLoading
                    ) {
                        playlistsDialogOpen =
                            AddToPlaylistOrQueueDialogOpen(true, queueViewModel.queueState)
                    }
                    IconButton(
                        onClick = {
                            queueViewModel.onEvent(QueueEvent.OnClearQueue)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistRemove,
                            contentDescription = "clear playlist"
                        )
                    }
                    IconButton(
                        onClick = {
                            mainViewModel.onEvent(MainEvent.PlayPauseCurrent)
                        }
                    ) {
                        Icon(
                            imageVector = if (!mainViewModel.isPlaying)
                                Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = stringResource(id = R.string.search_content_description)
                        )
                    }
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .padding(it)
                .padding(top = dimensionResource(id = R.dimen.albumDetailScreen_top_padding)),
        ) {
            QueueScreenContent(
                navigator = navigator,
                mainViewModel = mainViewModel,
                queueViewModel = queueViewModel,
                addToPlaylistOrQueueDialogViewModel = addToPlaylistOrQueueDialogViewModel
            )
        }
    }
}
