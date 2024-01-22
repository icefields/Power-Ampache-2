package luci.sixsixsix.powerampache2.presentation.queue.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.common.SongItem
import luci.sixsixsix.powerampache2.presentation.common.SongItemEvent
import luci.sixsixsix.powerampache2.presentation.common.SubtitleString
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.queue.QueueEvent
import luci.sixsixsix.powerampache2.presentation.queue.QueueViewModel

@Composable
fun QueueScreenContent(
    navigator: DestinationsNavigator,
    mainViewModel: MainViewModel,
    viewModel: QueueViewModel,
    modifier: Modifier = Modifier
) {
    val queue = viewModel.queueState
    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }
    if (playlistsDialogOpen.isOpen) {
        if (playlistsDialogOpen.songs.isNotEmpty()) {
            AddToPlaylistOrQueueDialog(
                songs = playlistsDialogOpen.songs,
                onDismissRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                },
                mainViewModel = mainViewModel,
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
                        SongItemEvent.PLAY_NEXT ->
                            mainViewModel.onEvent(MainEvent.OnAddSongToQueueNext(song))
                        SongItemEvent.SHARE_SONG ->
                            mainViewModel.onEvent(MainEvent.OnShareSong(song))
                        SongItemEvent.DOWNLOAD_SONG ->
                            mainViewModel.onEvent(MainEvent.OnDownloadSong(song))
                        SongItemEvent.GO_TO_ALBUM ->
                            navigator.navigate(AlbumDetailScreenDestination(
                                albumId = song.album.id,
                                album = null))
                        SongItemEvent.GO_TO_ARTIST ->
                            navigator.navigate(ArtistDetailScreenDestination(
                                artistId = song.artist.id,
                                artist = null))
                        SongItemEvent.ADD_SONG_TO_QUEUE -> { } // already in queue
                        SongItemEvent.ADD_SONG_TO_PLAYLIST ->
                            playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                    }
                },
                subtitleString = SubtitleString.ARTIST,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (song == mainViewModel.state.song) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                    .clickable {
                        // TODO BUG when tapping on a song, in the context of a playlist, do not
                        //  move the new song on top, just start playing from the selected song
                        viewModel.onEvent(QueueEvent.OnSongSelected(song))
                        mainViewModel.onEvent(MainEvent.Play(song))
                    },
                enableSwipeToRemove = true,
                onRemove = { viewModel.onEvent(QueueEvent.OnSongRemove(it)) }
            )
        }
    }
}
