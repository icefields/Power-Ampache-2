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
package luci.sixsixsix.powerampache2.presentation.screens.offline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton
import luci.sixsixsix.powerampache2.presentation.common.EmptyListView
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongItem
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongItemEvent
import luci.sixsixsix.powerampache2.presentation.common.songitem.SubtitleString
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.dialogs.EraseConfirmDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.ShareDialog
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = false)
fun OfflineSongsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    viewModel: OfflineSongsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }

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
                        text = "Offline Songs",
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
                    IconButton(
                        onClick = {
                            playlistsDialogOpen =
                                AddToPlaylistOrQueueDialogOpen(true, viewModel.state.songs)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "add all songs in queue to playlist"
                        )
                    }
                    IconButton(
                        onClick = {
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistRemove,
                            contentDescription = "clear all downloaded songs"
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
                            contentDescription = "play all"
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
            OfflineSongsMainContent(
                navigator = navigator,
                mainViewModel = mainViewModel,
                viewModel = viewModel,
                modifier = modifier,
                playlistOrQueueDialogOpen = playlistsDialogOpen
            )
        }
    }

}

@Composable
@Destination(start = false)
fun OfflineSongsMainContent(
    navigator: DestinationsNavigator? = Ampache2NavGraphs.navigator,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    playlistOrQueueDialogOpen: AddToPlaylistOrQueueDialogOpen = AddToPlaylistOrQueueDialogOpen(false),
    viewModel: OfflineSongsViewModel = hiltViewModel(),
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val state = viewModel.state
    var playlistsDialogOpen by remember { mutableStateOf(playlistOrQueueDialogOpen) }

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

    var showDeleteSongDialog by remember { mutableStateOf<Song?>(null) }
    showDeleteSongDialog?.let { songToRemove ->
        EraseConfirmDialog(
            onDismissRequest = {
                showDeleteSongDialog = null
            },
            onConfirmation = {
                showDeleteSongDialog = null
                // delete downloaded song, delete database entry
                mainViewModel.onEvent(MainEvent.OnDownloadedSongDelete(songToRemove))
            },
            dialogTitle = stringResource(id = R.string.warning_song_remove_title),
            dialogText = stringResource(id = R.string.warning_song_remove_subtitle, songToRemove.name) //"Delete ${songToRemove.name} from downloaded songs?"
        )
    }

    var songToShare: Song? by remember { mutableStateOf(null) }

    AnimatedVisibility(songToShare != null) {
        songToShare?.let { songS ->
            ShareDialog(
                onShareWeb = {
                    mainViewModel.onEvent(MainEvent.OnShareSongWebUrl(songS))
                    songToShare = null
                },
                onSharePowerAmpache = {
                    mainViewModel.onEvent(MainEvent.OnShareSong(songS))
                    songToShare = null
                },
                onDismissRequest = {
                    songToShare = null
                }
            )
        }

    }

    Box(modifier = modifier) {
        if (state.isLoading && state.songs.isEmpty()) {
            LoadingScreen()
        } else if(state.songs.isEmpty()) {
            EmptyListView(title = stringResource(id = R.string.offline_noData_warning))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.songs) { song ->
                SongItem(
                    song = song,
                    isSongDownloaded = true,
                    showDownloadedSongMarker = false,
                    songItemEventListener = { event ->
                        when(event) {
                            SongItemEvent.PLAY_NEXT ->
                                mainViewModel.onEvent(MainEvent.OnAddSongToQueueNext(song))
                            SongItemEvent.SHARE_SONG -> {
                                songToShare = song
                            }
                            SongItemEvent.DOWNLOAD_SONG -> { } // DO NOTHING
                            SongItemEvent.EXPORT_DOWNLOADED_SONG ->
                                mainViewModel.onEvent(MainEvent.OnExportDownloadedSong(song))
                            SongItemEvent.GO_TO_ALBUM -> navigator?.navigate(
                                AlbumDetailScreenDestination(albumId = song.album.id, album = null)
                            )
                            SongItemEvent.GO_TO_ARTIST ->
                                Ampache2NavGraphs.navigateToArtist(navigator, artistId = song.artist.id, artist = null)

                            SongItemEvent.ADD_SONG_TO_QUEUE ->
                                mainViewModel.onEvent(MainEvent.OnAddSongToQueue(song))
                            SongItemEvent.ADD_SONG_TO_PLAYLIST ->
                                playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                        }
                    },
                    subtitleString = SubtitleString.ARTIST,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            mainViewModel.onEvent(
                                MainEvent.PlaySongAddToQueueTop(
                                    song,
                                    state.songs
                                )
                            )
//                            viewModel.onEvent(OfflineSongsEvent.OnSongSelected(song))
//                            mainViewModel.onEvent(MainEvent.Play(song))
                        },
                    enableSwipeToRemove = true,
                    onRemove = { songToRemove ->
                        showDeleteSongDialog = songToRemove
                    },
                    onRightToLeftSwipe = {
                        playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                    }
                )
            }
        }
    }
}
