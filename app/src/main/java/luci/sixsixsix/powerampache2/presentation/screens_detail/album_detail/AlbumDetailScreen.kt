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
package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.common.SongInfoThirdRow
import luci.sixsixsix.powerampache2.presentation.common.SongItem
import luci.sixsixsix.powerampache2.presentation.common.SongItemEvent
import luci.sixsixsix.powerampache2.presentation.common.SubtitleString
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.AlbumDetailTopBar
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.AlbumInfoSection
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.AlbumInfoViewEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun AlbumDetailScreen(
    navigator: DestinationsNavigator,
    albumId: String,
    album: Album? = null,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val album by viewModel.albumStateFlow.collectAsState()
    val isGlobalShuffleOn by viewModel.globalShuffleStateFlow.collectAsState()
    val songs = viewModel.state.getSongList()
    val currentSongState by mainViewModel.currentSongStateFlow().collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var infoVisibility by remember { mutableStateOf(true) }
    var playlistsDialogOpen by remember { mutableStateOf(AddToPlaylistOrQueueDialogOpen(false)) }
    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }

    val isLandscape = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            //infoVisibility = false
            true
        }
        else -> {
            //infoVisibility = true
            false
        }
    }

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

    val placeholder = painterResource(id = R.drawable.img_album_detail_placeholder)
    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = album.artUrl,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            contentDescription = album.name
        )
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = album.artUrl,
            contentScale = ContentScale.FillWidth,
            placeholder = placeholder,
            contentDescription = album.name,
        )
        // full screen view to add a transparent black layer on top
        // of the images for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.4f)
                .background(brush = screenBackgroundGradient)
        )

        if (state.isLoading) {
            LoadingScreen()
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                AlbumDetailTopBar(
                    navigator = navigator,
                    album = album,
                    isLoading = mainViewModel.isLoading,
                    isEditingPlaylist = addToPlaylistOrQueueDialogViewModel.state.isPlaylistEditLoading,
                    scrollBehavior = scrollBehavior,
                    onRate = {
                        viewModel.onEvent(AlbumDetailEvent.OnNewRating(it))
                    }
                ) { infoVisibility = !infoVisibility }
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(it)
                    .padding(top = dimensionResource(id = R.dimen.albumDetailScreen_top_padding))
                    .background(brush = albumBackgroundGradient),
                color = Color.Transparent
            ) {
                val isPlayingAlbum = mainViewModel.isPlaying && songs.contains(currentSongState)
                Column {
                    AlbumInfoSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = if (infoVisibility) { 470.dp /*any big number*/ } else { 0.dp })
                            .padding(dimensionResource(R.dimen.albumDetailScreen_infoSection_padding)),
                        album = album,
                        isPlayLoading = mainViewModel.isPlayLoading(),
                        isBuffering = mainViewModel.isBuffering,
                        isPlayingAlbum = isPlayingAlbum,
                        isLikeLoading = state.isLikeLoading,
                        isAlbumDownloaded = state.isAlbumDownloaded,
                        isDownloading = mainViewModel.state.isDownloading,
                        isPlaylistEditLoading = addToPlaylistOrQueueDialogViewModel.state.isPlaylistEditLoading,
                        isGlobalShuffleOn = isGlobalShuffleOn ,
                        artistClickListener = { artistId ->
                            Ampache2NavGraphs.navigateToArtist(navigator, artistId = artistId)
                        },
                        eventListener = { event ->
                            when(event) {
                                AlbumInfoViewEvents.PLAY_ALBUM -> {

                                    if (state.isLoading || viewModel.state.songs.isNullOrEmpty()) return@AlbumInfoSection

                                    if (isPlayingAlbum) {
                                        // will pause if playing
                                        mainViewModel.onEvent(MainEvent.PlayPauseCurrent)
                                    } else {
                                        if (!isGlobalShuffleOn) {
                                            // add next to the list and skip to the top of the album (which is next)
                                            mainViewModel.onEvent(MainEvent.AddSongsToQueueAndPlay(songs[0], state.getSongList()))
                                        } else {
                                            mainViewModel.onEvent(MainEvent.AddSongsToQueueAndPlayShuffled(state.getSongList()))
                                        }
                                    }
                                }
                                AlbumInfoViewEvents.SHARE_ALBUM ->
                                    viewModel.onEvent(AlbumDetailEvent.OnShareAlbum)
                                AlbumInfoViewEvents.DOWNLOAD_ALBUM ->
                                    mainViewModel.onEvent(MainEvent.OnDownloadSongs(songs))
                                AlbumInfoViewEvents.SHUFFLE_PLAY_ALBUM -> {
                                    viewModel.onEvent(AlbumDetailEvent.OnShufflePlaylistToggle)
                                }
                                AlbumInfoViewEvents.ADD_ALBUM_TO_PLAYLIST ->
                                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(
                                        isOpen = true,
                                        songs = songs
                                    )
                                AlbumInfoViewEvents.FAVOURITE_ALBUM ->
                                    viewModel.onEvent(AlbumDetailEvent.OnFavouriteAlbum)
                                AlbumInfoViewEvents.STOP_DOWNLOAD_ALBUM ->
                                    mainViewModel.onEvent(MainEvent.OnStopDownloadSongs)
                            }
                        }
                    )

                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { viewModel.onEvent(AlbumDetailEvent.Fetch(album.id)) }
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(state.songs.size) { i ->
                                val song = state.songs[i].song
                                val isOffline = state.songs[i].isOffline
                                SongItem(
                                    song = song,
                                    isLandscape = isLandscape,
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
                                            SongItemEvent.GO_TO_ALBUM -> { } // No ACTION, we're already in this album //navigator.navigate(AlbumDetailScreenDestination(viewModel.state.album.id, viewModel.state.album))
                                            SongItemEvent.GO_TO_ARTIST ->
                                                Ampache2NavGraphs.navigateToArtist(navigator,
                                                    artistId = album.artist.id
                                                )
                                            SongItemEvent.ADD_SONG_TO_QUEUE ->
                                                mainViewModel.onEvent(MainEvent.OnAddSongToQueue(song))
                                            SongItemEvent.ADD_SONG_TO_PLAYLIST ->
                                                playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            mainViewModel.onEvent(
                                                MainEvent.PlaySongReplacePlaylist(
                                                    song,
                                                    state.getSongList()
                                                )
                                            )
//                                            viewModel.onEvent(AlbumDetailEvent.OnSongSelected(song))
//                                            mainViewModel.onEvent(MainEvent.Play(song))
                                        },
                                    subtitleString = SubtitleString.NOTHING,
                                    songInfoThirdRow = SongInfoThirdRow.Time,
                                    isSongDownloaded = isOffline
                                )
                            }
                        }
                        if (state.isLoading && state.songs.isEmpty()) {
                            LoadingScreen()
                        }
                    }
                }
            }
        }
    }
}

private val albumBackgroundGradient
    @Composable
    get() =
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.75f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.65f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.62f),
            )

)

private val screenBackgroundGradient
    @Composable
    get() =
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background
            )

        )

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun AlbumPreview() {
//    AlbumDetailScreen(
//        navigator = EmptyDestinationsNavigator,
//        albumId = "1050",
//        album = Album(
//            name = "Album title",
//            time = 129,
//            id = UUID.randomUUID().toString(),
//            songCount = 11,
//            genre = listOf(
//                MusicAttribute(id = UUID.randomUUID().toString(), name = "Thrash Metal"),
//                MusicAttribute(id = UUID.randomUUID().toString(), name = "Progressive Metal"),
//                MusicAttribute(id = UUID.randomUUID().toString(), name = "Jazz"),
//            ),
//            artists = listOf(
//                MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
//                MusicAttribute(id = UUID.randomUUID().toString(), name = "Marty Friedman"),
//                MusicAttribute(id = UUID.randomUUID().toString(), name = "Other people"),
//            ),
//            year = 1986)
//    )
}
