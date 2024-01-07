package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.totalTime
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailContent(
    // navigator: DestinationsNavigator,
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    val state = mainViewModel.state
    val scope = rememberCoroutineScope()
    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }

    if (playlistsDialogOpen.isOpen) {
        playlistsDialogOpen.song?.let {
            AddToPlaylistOrQueueDialog(it,
                onDismissRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                },
                mainViewModel = mainViewModel
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            model = state.song?.imageUrl,
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.placeholder_album),
            contentDescription = state.song?.title,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.song?.title ?: "",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            //color = MaterialTheme.colorScheme.primary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = state.song?.artist?.name ?: "",
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Divider(Modifier.padding(vertical = 0.dp))
        mainViewModel.state.song?.let { song ->
            SongDetailButtonRow(modifier = Modifier.fillMaxWidth(), song = song) { event ->
                when(event) {
                    SongDetailButtonEvents.SHARE_SONG -> mainViewModel.onEvent(MainEvent.OnShareSong(song))
                    SongDetailButtonEvents.DOWNLOAD_SONG -> mainViewModel.onEvent(MainEvent.OnDownloadSong(song))
                    SongDetailButtonEvents.ADD_SONG_TO_PLAYLIST_OR_QUEUE -> {
                        playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, song)
                    }
                    SongDetailButtonEvents.GO_TO_ALBUM -> {
                        mainViewModel.state.song?.album?.id?.let { albumId ->
                            Ampache2NavGraphs.navigator?.navigate(AlbumDetailScreenDestination(albumId = albumId))
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    }
                    SongDetailButtonEvents.GO_TO_ARTIST -> {
                        mainViewModel.state.song?.artist?.id?.let { artistId ->
                            Ampache2NavGraphs.navigator?.navigate(ArtistDetailScreenDestination(artistId = artistId))
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    }
                    SongDetailButtonEvents.SHOW_INFO -> TODO()
                }
            }
        }
        Divider(Modifier.padding(vertical = 0.dp))

        Spacer(modifier = Modifier.height(12.dp))

        SongDetailPlayerBar(
            progress = mainViewModel.progress,
            durationStr = mainViewModel.state.song?.totalTime() ?: "",
            progressStr = mainViewModel.progressStr,
            isPlaying = mainViewModel.isPlaying,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) { event ->
            mainViewModel.onEvent(event)
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun otherStuff() {
    LazyColumn() {
//            items(1) {
//                Text(
//                    text = "${state.song?.toDebugString()}",
//                    fontWeight = FontWeight.Light,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    maxLines = 30,
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.End
//                )
//            }
    }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//
//        LazyColumn(modifier = Modifier.weight(1.0f)) {
//            items(1) {
//                Text(
//                    text = "${state.song?.toDebugString()}",
//                    fontWeight = FontWeight.Light,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    maxLines = 30,
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.End
//                )
//            }
//        }
//
//        LazyColumn(modifier = Modifier.weight(3.0f)) {
//            items(viewModel.state.queue.toList()) { song ->
//                Text(
//                    text = "${song.title} - ${song.artist.name}",
//                    fontWeight = FontWeight.Light,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    maxLines = 1,
//                    modifier = Modifier.fillMaxWidth())
//            }
//        }
}
