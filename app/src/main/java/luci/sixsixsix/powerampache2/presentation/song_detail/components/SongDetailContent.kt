package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumInfoViewEvents
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItemEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailContent(
    // navigator: DestinationsNavigator,
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val scope = rememberCoroutineScope()
    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }

    if (playlistsDialogOpen.isOpen) {
        playlistsDialogOpen.song?.let {
            AddToPlaylistOrQueueDialog(it,
                onDismissRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                }
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
                .aspectRatio(1f)
                .fillMaxWidth(),
            model = state.song?.imageUrl,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.placeholder_album),
            contentDescription = state.song?.title,
        )
        Spacer(modifier = Modifier.height(32.dp))

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
        Spacer(modifier = Modifier.height(10.dp))

        Divider(Modifier.padding(vertical = 2.dp))
        viewModel.state.song?.let { song ->
            SongDetailButtonRow(modifier = Modifier.fillMaxWidth(), song = song) { event ->
                when(event) {
                    SongDetailButtonEvents.SHARE_SONG -> viewModel.onEvent(MainEvent.OnShareSong(song))
                    SongDetailButtonEvents.DOWNLOAD_SONG -> viewModel.onEvent(MainEvent.OnDownloadSong(song))
                    SongDetailButtonEvents.ADD_SONG_TO_PLAYLIST_OR_QUEUE -> {
                        playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, song)
                    }
                    SongDetailButtonEvents.GO_TO_ALBUM -> {
                        viewModel.state.song?.album?.id?.let { albumId ->
                            Ampache2NavGraphs.navigator?.navigate(AlbumDetailScreenDestination(albumId = albumId))
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    }
                    SongDetailButtonEvents.GO_TO_ARTIST -> {
                        viewModel.state.song?.artist?.id?.let { artistId ->
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
        Divider(Modifier.padding(vertical = 2.dp))

        Spacer(modifier = Modifier.height(12.dp))

        SongDetailPlayerBar(modifier = Modifier.fillMaxWidth())

        LazyColumn(modifier = Modifier.weight(1.0f)) {
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
}
