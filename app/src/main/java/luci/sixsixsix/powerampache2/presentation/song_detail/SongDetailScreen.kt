package luci.sixsixsix.powerampache2.presentation.song_detail


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.main.screens.getPeakHeight
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailQueueDragHandle
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailQueueTopBar
import luci.sixsixsix.powerampache2.presentation.queue.QueueEvent
import luci.sixsixsix.powerampache2.presentation.queue.QueueViewModel
import luci.sixsixsix.powerampache2.presentation.queue.components.QueueScreenContent
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItemEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    //navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val scaffoldState = rememberBottomSheetScaffoldState()
    val barHeight = dimensionResource(id = R.dimen.miniPlayer_height)
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SongDetailQueueScreenContent()
        },
        sheetDragHandle = {
            SongDetailQueueDragHandle(scaffoldState = scaffoldState)
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = barHeight
    ) {
        SongDetailContent(
            //navigator = navigator,
            Modifier.padding(paddingValues = it))
    }
}

@Composable
fun SongDetailContent(
    // navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        AsyncImage(
            modifier = Modifier
                .weight(7f)
                .fillMaxWidth(),
            model = state.song?.imageUrl,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.ic_playlist),
            contentDescription = state.song?.title,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = state.song?.title ?: "",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.width(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(2.0f))
            IconButton(
                onClick = {
                    state.song?.let {
                        viewModel.onEvent(MainEvent.Play(it))
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayCircle,
                    contentDescription = state.song?.title ?: "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxSize()
                )
            }
            Box(modifier = Modifier.weight(2.0f))
        }

        LazyColumn(modifier = Modifier.weight(1.0f)) {
            items(1) {
                Text(
                    text = "${state.song?.toDebugString()}",
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 30,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(3.0f)) {
            items(viewModel.state.queue.toList()) { song ->
                Text(
                    text = "${song.title} - ${song.artist.name}",
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun SongDetailQueueScreenContent(
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
                        SongItemEvent.GO_TO_ARTIST -> {}
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
