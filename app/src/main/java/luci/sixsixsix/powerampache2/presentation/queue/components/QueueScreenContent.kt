package luci.sixsixsix.powerampache2.presentation.queue.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.queue.QueueEvent
import luci.sixsixsix.powerampache2.presentation.queue.QueueViewModel
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItemEvent

@Composable
fun QueueScreenContent(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: QueueViewModel = hiltViewModel()
) {
    val state = mainViewModel.state
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(state.queue) { song ->
            SongItem(
                song = song,
                songItemEventListener = { event ->
                    when(event) {
                        SongItemEvent.PLAY_NEXT -> {} // viewModel.onEvent(AlbumDetailEvent.OnAddSongToQueueNext(song))
                        SongItemEvent.SHARE_SONG -> {} // viewModel.onEvent(AlbumDetailEvent.OnShareSong(song))
                        SongItemEvent.DOWNLOAD_SONG -> {} // viewModel.onEvent(AlbumDetailEvent.OnDownloadSong(song))
                        SongItemEvent.GO_TO_ALBUM -> {} //  navigator.navigate(AlbumDetailScreenDestination(albumId = song.album.id))
                        SongItemEvent.GO_TO_ARTIST -> navigator.navigate(
                            ArtistDetailScreenDestination(artistId = song.artist.id, artist = null)
                        )
                        SongItemEvent.ADD_SONG_TO_QUEUE -> {} // viewModel.onEvent(AlbumDetailEvent.OnAddSongToQueue(song))
                        SongItemEvent.ADD_SONG_TO_PLAYLIST -> {}
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // TODO BUG when tapping on a song, in the context of a playlist, do not
                        //  move the new song on top, just start playing from the selected song
                        mainViewModel.onEvent(MainEvent.Play(song))
                        viewModel.onEvent(QueueEvent.OnSongSelected(song))
                    }
            )
        }
    }
}
