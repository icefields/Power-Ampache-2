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
package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.toDebugMap
import luci.sixsixsix.powerampache2.domain.models.totalTime
import luci.sixsixsix.powerampache2.presentation.common.LikeButton
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.dialogs.InfoDialog
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SongDetailContent(
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel
) {
    val state = mainViewModel.state
    val scope = rememberCoroutineScope()
    val buttonsTint = MaterialTheme.colorScheme.secondary

    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }
    if (playlistsDialogOpen.isOpen) {
        if (playlistsDialogOpen.songs.isNotEmpty()) {
            AddToPlaylistOrQueueDialog(playlistsDialogOpen.songs,
                onDismissRequest = {
                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(false)
                },
                mainViewModel = mainViewModel,
                viewModel = addToPlaylistOrQueueDialogViewModel
            )
        }
    }

    var infoDialogOpen by remember { mutableStateOf(false) }
    if (infoDialogOpen) {
            InfoDialog(
                info = mainViewModel.state.song?.toDebugMap() ?: mapOf(),
                onDismissRequest = {
                    infoDialogOpen = false
                }
            )
    }

    var isOffline by remember { mutableStateOf(false) }

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

        Box() {
            Column {
                Text(
                    text = state.song?.title ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    //color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee()
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
            }

            LikeButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                isLikeLoading = mainViewModel.state.isLikeLoading,
                isFavourite = mainViewModel.state.song?.flag == 1,
                iconTint = buttonsTint,
                background = Color.Transparent
            ) {
                mainViewModel.onEvent(MainEvent.FavouriteSong)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider(Modifier.padding(vertical = 0.dp))
        mainViewModel.state.song?.let { song ->

            mainViewModel.isOfflineSong(song) {
                isOffline = it
            }

            SongDetailButtonRow(
                modifier = Modifier.fillMaxWidth(),
                song = song,
                isOffline = isOffline,
                tint = buttonsTint
            ) { event ->
                when(event) {
                    SongDetailButtonEvents.SHARE_SONG ->
                        mainViewModel.onEvent(MainEvent.OnShareSong(song))
                    SongDetailButtonEvents.DOWNLOAD_SONG ->
                        mainViewModel.onEvent(MainEvent.OnDownloadSong(song))
                    SongDetailButtonEvents.ADD_SONG_TO_PLAYLIST_OR_QUEUE ->
                        playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                    SongDetailButtonEvents.GO_TO_ALBUM ->
                        mainViewModel.state.song?.album?.id?.let { albumId ->
                            Ampache2NavGraphs.navigateToAlbum(albumId = albumId)
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    SongDetailButtonEvents.GO_TO_ARTIST ->
                        mainViewModel.state.song?.artist?.id?.let { artistId ->
                            Ampache2NavGraphs.navigateToArtist(artistId = artistId)
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    SongDetailButtonEvents.SHOW_INFO ->
                        infoDialogOpen = true
                    SongDetailButtonEvents.DELETE_DOWNLOADED_SONG -> {
                        mainViewModel.onEvent(MainEvent.OnDownloadedSongDelete(song = song))
                        // TODO BREAKING_RULE anti-pattern. verify the song is actually deleted
                        isOffline = false
                    }
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
            isBuffering = mainViewModel.isBuffering,
            shuffleOn = mainViewModel.shuffleOn,
            repeatMode = mainViewModel.repeatMode,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
