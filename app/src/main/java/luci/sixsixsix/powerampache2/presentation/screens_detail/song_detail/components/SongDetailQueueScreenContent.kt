package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.queue.QueueEvent
import luci.sixsixsix.powerampache2.presentation.queue.QueueViewModel
import luci.sixsixsix.powerampache2.presentation.common.SongItem
import luci.sixsixsix.powerampache2.presentation.common.SongItemEvent
import luci.sixsixsix.powerampache2.presentation.common.SubtitleString
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailQueueScreenContent(
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    viewModel: QueueViewModel = hiltViewModel(),
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val queue = viewModel.queueState
    val scope = rememberCoroutineScope()

    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }
    if (playlistsDialogOpen.isOpen) {
        if (playlistsDialogOpen.songs.isNotEmpty()) {
            AddToPlaylistOrQueueDialog(
                songs = playlistsDialogOpen.songs,
                onDismissRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                },
                mainViewModel = mainViewModel,
                viewModel = addToPlaylistOrQueueDialogViewModel,
                onCreatePlaylistRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                }
            )
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(
            items = queue,
            key = { _, item -> item.mediaId }
        ) { _, song ->
            SongItem(
                song = song,
                songItemEventListener = { event ->
                    when(event) {
                        SongItemEvent.PLAY_NEXT -> mainViewModel.onEvent(MainEvent.OnAddSongToQueueNext(song))
                        SongItemEvent.SHARE_SONG -> mainViewModel.onEvent(MainEvent.OnShareSong(song))
                        SongItemEvent.DOWNLOAD_SONG -> mainViewModel.onEvent(MainEvent.OnDownloadSong(song))
                        SongItemEvent.GO_TO_ALBUM -> {
                            Ampache2NavGraphs.navigator?.navigate(AlbumDetailScreenDestination(albumId = song.album.id))
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                        SongItemEvent.GO_TO_ARTIST -> {
                            Ampache2NavGraphs.navigator?.navigate(ArtistDetailScreenDestination(artistId = song.artist.id))
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                        SongItemEvent.ADD_SONG_TO_QUEUE -> mainViewModel.onEvent(MainEvent.OnAddSongToQueue(song))
                        SongItemEvent.ADD_SONG_TO_PLAYLIST ->
                            playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                    }
                },
                subtitleString = SubtitleString.ARTIST,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (song == mainViewModel.state.song) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                    .clickable {
                        mainViewModel.onEvent(MainEvent.Play(song))
                        viewModel.onEvent(QueueEvent.OnSongSelected(song))
                    },
                enableSwipeToRemove = true,
                onRemove = { viewModel.onEvent(QueueEvent.OnSongRemove(it)) },
                onRightToLeftSwipe = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                }
            )
        }
    }
}
