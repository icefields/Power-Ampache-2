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
package luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.common.SongInfoThirdRow
import luci.sixsixsix.powerampache2.presentation.common.SongItem
import luci.sixsixsix.powerampache2.presentation.common.SongItemEvent
import luci.sixsixsix.powerampache2.presentation.common.SubtitleString
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialog
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogOpen
import luci.sixsixsix.powerampache2.presentation.dialogs.AddToPlaylistOrQueueDialogViewModel
import luci.sixsixsix.powerampache2.presentation.dialogs.EraseConfirmDialog
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs.navigateToArtist
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail.components.PlaylistDetailTopBar
import luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail.components.PlaylistInfoSection
import luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail.components.PlaylistInfoViewEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun PlaylistDetailScreen(
    navigator: DestinationsNavigator,
    playlist: Playlist,
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
    addToPlaylistOrQueueDialogViewModel: AddToPlaylistOrQueueDialogViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val currentSongState by mainViewModel.currentSongStateFlow().collectAsState()
    val currentPlaylistState by viewModel.playlistStateFlow.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var infoVisibility by remember { mutableStateOf(true) }
    var showDeleteSongDialog by remember { mutableStateOf<Song?>(null) }

    var randomBackgroundTop by remember { mutableStateOf("") }
    var randomBackgroundBottom by remember { mutableStateOf("") }
    if (randomBackgroundBottom.isEmpty() || randomBackgroundTop.isEmpty()) {
        val backgrounds = viewModel.generateBackgrounds()
        if (randomBackgroundBottom.isEmpty()) {
            randomBackgroundBottom = backgrounds.second
        }
        if (randomBackgroundTop.isEmpty()) {
            randomBackgroundTop = backgrounds.first
        }
    }

    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }

    val configuration = LocalConfiguration.current
    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }.collect { orientation = it }
    }

    val isLandscape = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // infoVisibility = false
            true
        }
        else -> {
            // infoVisibility = true
            false
        }
    }

    showDeleteSongDialog?.let { songToRemove ->
        EraseConfirmDialog(
            onDismissRequest = {
                showDeleteSongDialog = null
                viewModel.onEvent(PlaylistDetailEvent.OnRemoveSongDismiss)
            },
            onConfirmation = {
                showDeleteSongDialog = null
                viewModel.onEvent(PlaylistDetailEvent.OnRemoveSong(songToRemove))
            },
            dialogTitle = stringResource(id = R.string.warning_song_remove_title),
            dialogText = "Delete ${songToRemove.name} from playlist \n${currentPlaylistState.name}?"
        )
    }

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

    val placeholder = painterResource(id = R.drawable.img_album_detail_placeholder)
    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = randomBackgroundTop,
            placeholder = placeholder,
            contentScale = ContentScale.Crop,
            contentDescription = playlist.name
        )
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = randomBackgroundBottom,
            placeholder = placeholder,
            contentScale = ContentScale.FillWidth,
            contentDescription = playlist.name,
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
                PlaylistDetailTopBar(
                    navigator = navigator,
                    playlist = currentPlaylistState,
                    isLoading = state.isLoading || state.isPlaylistRemoveLoading,
                    scrollBehavior = scrollBehavior,
                    isRatingVisible = !state.isGeneratedOrSmartPlaylist,
                    onRating = { playlist, rating ->
                        viewModel.onEvent(PlaylistDetailEvent.OnRatePlaylist(playlist, rating))
                    }
                ) {
                    viewModel.onEvent(PlaylistDetailEvent.OnToggleSort)
                }
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(it)
                    .padding(top = dimensionResource(id = R.dimen.albumDetailScreen_top_padding))
                    .background(brush = albumBackgroundGradient),
                color = Color.Transparent
            ) {
                val isPlayingPlaylist = mainViewModel.isPlaying
                        && state.getSongList().contains(currentSongState)

                Column {
                    PlaylistInfoSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = if (infoVisibility) {
                                    470.dp /* any big number */ } else { 0.dp })
                            .padding(dimensionResource(R.dimen.albumDetailScreen_infoSection_padding)),
                        playlist = currentPlaylistState,
                        isLoading = state.isLoading,
                        isPlayingPlaylist = isPlayingPlaylist,
                        isBuffering = mainViewModel.isBuffering,
                        isLikeAvailable = !state.isGeneratedOrSmartPlaylist,
                        isLikeLoading = state.isLikeLoading ,
                        isDownloading = mainViewModel.state.isDownloading,
                        isGlobalShuffleOn = state.isGlobalShuffleOn,
                        isPlayLoading = mainViewModel.isPlayLoading(),
                        enabled = !state.songs.isNullOrEmpty(),
                        songs = viewModel.state.getSongList(),
                        artistClickListener = {
                            artistId -> Ampache2NavGraphs.navigateToArtist(navigator, artistId)
                        },
                        isPlaylistEditLoading = addToPlaylistOrQueueDialogViewModel.state.isPlaylistEditLoading,
                        eventListener = { event ->
                            when(event) {

                                PlaylistInfoViewEvents.PLAY_PLAYLIST -> {
                                    if (viewModel.state.songs.isNullOrEmpty()) return@PlaylistInfoSection

                                    if (isPlayingPlaylist){
                                        // will pause if playing
                                        mainViewModel.onEvent(MainEvent.PlayPauseCurrent)
                                    } else if (state.songs.isNotEmpty()) {
                                        if (!state.isGlobalShuffleOn) {
                                            mainViewModel.onEvent(
                                                MainEvent.AddSongsToQueueAndPlay(state.songs[0].song, state.getSongList())
                                            )
                                        } else {
                                            mainViewModel.onEvent(
                                                MainEvent.AddSongsToQueueAndPlayShuffled(state.getSongList())
                                            )
                                        }
                                    }
                                }

                                PlaylistInfoViewEvents.SHARE_PLAYLIST ->
                                    viewModel.onEvent(PlaylistDetailEvent.OnSharePlaylist)

                                PlaylistInfoViewEvents.DOWNLOAD_PLAYLIST -> if (!state.isLoading) {
                                    mainViewModel.onEvent(MainEvent.OnDownloadSongs(viewModel.state.getSongList()))
                                } else {
                                    viewModel.onEvent(PlaylistDetailEvent.OnPlaylistNotReadyDownload)
                                }

                                PlaylistInfoViewEvents.SHUFFLE_PLAY_PLAYLIST -> {
                                    viewModel.onEvent(PlaylistDetailEvent.OnShufflePlaylistToggle)
                                }

                                PlaylistInfoViewEvents.STOP_DOWNLOAD_PLAYLIST ->
                                    mainViewModel.onEvent(MainEvent.OnStopDownloadSongs)

                                PlaylistInfoViewEvents.ADD_PLAYLIST_TO_PLAYLIST ->
                                    playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(
                                        isOpen = true,
                                        songs = viewModel.state.getSongList()
                                    )

                                PlaylistInfoViewEvents.LIKE_PLAYLIST ->
                                    viewModel.onEvent(PlaylistDetailEvent.OnLikePlaylist)
                            }
                        }
                    )

                    showHideEmptyPlaylistView(playlist = currentPlaylistState, state = state)

                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { viewModel.onEvent(PlaylistDetailEvent.Fetch(currentPlaylistState)) }
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            itemsIndexed(
                                items = state.songs,
                                //key = { _, item -> item }
                            ) { _, songWrapped ->
                                val song = songWrapped.song
                                val isOffline = songWrapped.isOffline
                                SongItem(
                                    song = song,
                                    isLandscape = isLandscape,
                                    isSongDownloaded = isOffline,
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
                                                navigateToArtist(navigator, song.artist.id)
                                            SongItemEvent.ADD_SONG_TO_QUEUE ->
                                                mainViewModel.onEvent(MainEvent.OnAddSongToQueue(song))
                                            SongItemEvent.ADD_SONG_TO_PLAYLIST ->
                                                playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            mainViewModel.onEvent(MainEvent.PlaySongReplacePlaylist(song, state.getSongList()))
                                        },
                                    subtitleString = SubtitleString.ARTIST,
                                    songInfoThirdRow = SongInfoThirdRow.Time,
                                    enableSwipeToRemove = viewModel.state.isUserOwner,
                                    onRemove = { songToRemove ->
                                        showDeleteSongDialog = songToRemove
                                    },
                                    onRightToLeftSwipe = {
                                        playlistsDialogOpen = AddToPlaylistOrQueueDialogOpen(true, listOf(song))
                                    }
                                )
                            }
                        }

                        if (state.isLoading && state.songs.isEmpty()) { LoadingScreen() }
                    }
                }
            }
        }
    }
}

@Composable
private fun showHideEmptyPlaylistView(playlist: Playlist, state: PlaylistDetailState) {
    if (
        /*(playlist is RecentPlaylist ||
                playlist is FrequentPlaylist ||
                playlist is HighestPlaylist ||
                playlist is FlaggedPlaylist) &&*/
        !state.isLoading && !state.isRefreshing && state.songs.isNullOrEmpty()
    ){
        Card(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "This playlist is empty")
                Text(text = when(playlist) {
                    is FrequentPlaylist -> "Your most frequently played songs will appear here"
                    is HighestPlaylist -> "Your highest rated songs will appear here"
                    is RecentPlaylist -> "Your most recent songs will appear here"
                    is FlaggedPlaylist -> "Your liked songs will appear here"
                    else -> ""
                })
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
