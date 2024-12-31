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
package luci.sixsixsix.powerampache2.presentation.screens.search.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.common.AmpacheListItem
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongItemEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.search.SearchViewEvent

@Composable
@Destination
fun ResultsListView(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    playlists: List<Playlist>,
    swipeToRefreshEnabled: Boolean,
    isLoading: Boolean,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (SearchViewEvent) -> Unit,
    onSongSelected: (Song) -> Unit,
    onAlbumSelected: (albumId: String, album: Album?) -> Unit,
    onArtistSelected: (artistId: String, artist: Artist?) -> Unit,
    onPlaylistSelected: (Playlist) -> Unit,
    onSongEvent: (MainEvent) -> Unit,
    onOpenPlaylistDialog: (List<Song>) -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    val megaList = ArrayList<AmpacheModel>(playlists).apply {
        addAll(artists)
        addAll(albums)
        addAll(songs)
    }

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        SwipeRefresh(
            swipeEnabled = swipeToRefreshEnabled,
            state = swipeRefreshState,
            onRefresh = { onEvent(SearchViewEvent.Refresh) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(megaList) { item ->
                    AmpacheListItem(
                        item = item,
                        isSongDownloaded = false,
                        songItemEventListener = {
                            onSongItemEvent(
                                song = (item as Song),
                                event = it,
                                onSongEvent,
                                onAlbumSelected,
                                onArtistSelected,
                                onOpenPlaylistDialog
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (item) {
                                    is Song -> onSongSelected(item)
                                    is Album -> onAlbumSelected(item.id, item)
                                    is Artist -> onArtistSelected(item.id, item)
                                    is Playlist -> onPlaylistSelected(item)
                                    else -> {}
                                }
                            },
                        enableSwipeToRemove = false
                    )
                }
            }
        }
    }
}

private fun onSongItemEvent(
    song: Song,
    event: SongItemEvent,
    onSongEvent: (MainEvent) -> Unit,
    onAlbumSelected: (albumId: String, album: Album?) -> Unit,
    onArtistSelected: (artistId: String, artist: Artist?) -> Unit,
    onOpenPlaylistDialog: (List<Song>) -> Unit
) {
    when(event) {
        SongItemEvent.PLAY_NEXT ->
            onSongEvent(MainEvent.OnAddSongToQueueNext(song))
        SongItemEvent.SHARE_SONG ->
            onSongEvent(MainEvent.OnShareSong(song))
        SongItemEvent.DOWNLOAD_SONG ->
            onSongEvent(MainEvent.OnDownloadSong(song))
        SongItemEvent.EXPORT_DOWNLOADED_SONG ->
            onSongEvent(MainEvent.OnExportDownloadedSong(song))
        SongItemEvent.GO_TO_ALBUM ->
            onAlbumSelected(song.album.id, null)
        SongItemEvent.GO_TO_ARTIST ->
            onArtistSelected(song.artist.id, null)
        SongItemEvent.ADD_SONG_TO_QUEUE ->
            onSongEvent(MainEvent.OnAddSongToQueue(song))
        SongItemEvent.ADD_SONG_TO_PLAYLIST ->
            onOpenPlaylistDialog(listOf(song))
    }
}
