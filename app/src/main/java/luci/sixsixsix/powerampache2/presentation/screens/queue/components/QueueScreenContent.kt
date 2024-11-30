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
package luci.sixsixsix.powerampache2.presentation.screens.queue.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.common.SongItem
import luci.sixsixsix.powerampache2.presentation.common.SongItemEvent
import luci.sixsixsix.powerampache2.presentation.common.SubtitleString
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.dialogs.EraseConfirmDialog
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens.queue.QueueEvent
import luci.sixsixsix.powerampache2.presentation.screens.queue.QueueViewModel

@Composable
fun QueueScreenContent(
    navigator: DestinationsNavigator,
    mainViewModel: MainViewModel,
    queueViewModel: QueueViewModel,
    modifier: Modifier = Modifier,
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel
) {
    val queue by queueViewModel.queueFlow.collectAsState()
    val songState by mainViewModel.currentSongStateFlow().collectAsState()

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

    var showDeleteSongDialog by remember { mutableStateOf<Song?>(null) }
    showDeleteSongDialog?.let { songToRemove ->
        EraseConfirmDialog(
            onDismissRequest = {
                showDeleteSongDialog = null
            },
            onConfirmation = {
                showDeleteSongDialog = null
                queueViewModel.onEvent(QueueEvent.OnSongRemove(songToRemove))
            },
            dialogTitle = stringResource(id = R.string.warning_song_remove_title),
            dialogText = "Delete ${songToRemove.name} from your queue?"
        )
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(
            items = queue,
            //key = { _, item -> item }
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
                        SongItemEvent.EXPORT_DOWNLOADED_SONG ->
                            mainViewModel.onEvent(MainEvent.OnExportDownloadedSong(song))
                        SongItemEvent.GO_TO_ALBUM ->
                            navigator.navigate(AlbumDetailScreenDestination(
                                albumId = song.album.id,
                                album = null))
                        SongItemEvent.GO_TO_ARTIST ->
                            Ampache2NavGraphs.navigateToArtist(navigator, artistId = song.artist.id)
                        SongItemEvent.ADD_SONG_TO_QUEUE -> { } // already in queue
                        SongItemEvent.ADD_SONG_TO_PLAYLIST ->
                            playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                    }
                },
                subtitleString = SubtitleString.ARTIST,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (song.mediaId == songState?.mediaId)
                            MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
                    )
                    .clickable {
                        mainViewModel.onEvent(MainEvent.PlaySong(song))
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
