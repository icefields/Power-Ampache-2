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

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.common.toDebugMap
import luci.sixsixsix.powerampache2.domain.models.totalTime
import luci.sixsixsix.powerampache2.presentation.common.LikeButton
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.dialogs.InfoDialog
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailContent(
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel
) {
    val currentSongState by mainViewModel.currentSongStateFlow().collectAsState()
    val scope = rememberCoroutineScope()
    val buttonsTint = MaterialTheme.colorScheme.onSurfaceVariant

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
                info = currentSongState?.toDebugMap() ?: mapOf(),
                onDismissRequest = {
                    infoDialogOpen = false
                }
            )
    }

    var isOffline by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = dimensionResource(id = R.dimen.player_screen_padding))
    ) {
        SongAlbumCoverArt(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            artUrl = currentSongState?.imageUrl,
            contentDescription = currentSongState?.title,
            onSwipeLeft = { mainViewModel.onEvent(MainEvent.SkipNext) },
            onSwipeRight = { mainViewModel.onEvent(MainEvent.SkipPrevious) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            Column {
                Text(
                    text = currentSongState?.artist?.name ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = fontDimensionResource(id = R.dimen.player_artistName_size),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee()
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = currentSongState?.title ?: "",
                    fontWeight = FontWeight.Normal,
                    fontSize = fontDimensionResource(id = R.dimen.player_songTitle_size),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LikeButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp),
                isLikeLoading = mainViewModel.state.isLikeLoading,
                isFavourite = currentSongState?.flag == 1
            ) {
                mainViewModel.onEvent(MainEvent.FavouriteSong)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        currentSongState?.let { song ->
            mainViewModel.isOfflineSong(song) {
                isOffline = it
            }
            SongDetailButtonRow(
                modifier = Modifier.fillMaxWidth(),
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
                        currentSongState?.album?.id?.let { albumId ->
                            Ampache2NavGraphs.navigateToAlbum(albumId = albumId)
                            scope.launch {
                                mainScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    SongDetailButtonEvents.GO_TO_ARTIST ->
                        currentSongState?.artist?.id?.let { artistId ->
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

        Spacer(modifier = Modifier.height(12.dp))

        SongDetailPlayerBar(
            progress = mainViewModel.progress,
            durationStr = currentSongState?.totalTime() ?: "",
            progressStr = mainViewModel.progressStr,
            isPlaying = mainViewModel.isPlaying,
            isPlayLoading = mainViewModel.isPlayLoading(),
            shuffleOn = mainViewModel.shuffleOn,
            repeatMode = mainViewModel.repeatMode,
            isBuffering = mainViewModel.isBuffering,
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
fun SongAlbumCoverArt(
    modifier: Modifier,
    artUrl: String?,
    contentDescription: String?,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
) {
    var currentPage by remember { mutableIntStateOf(1500) }
    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { 3000 }
    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page > currentPage) {
                onSwipeLeft()
            } else if (page < currentPage) {
                onSwipeRight()
            }
            currentPage = page
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) {
        AsyncImage(
            modifier = Modifier
                //.weight(1f)
                .fillMaxWidth(),
            model = artUrl,
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.placeholder_album),
            contentDescription = contentDescription,
        )
    }
}
