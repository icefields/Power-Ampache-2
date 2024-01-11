package luci.sixsixsix.powerampache2.presentation.playlist_detail

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
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.album_detail.AlbumDetailEvent
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumInfoViewEvents
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.playlist_detail.components.PlaylistDetailTopBar
import luci.sixsixsix.powerampache2.presentation.playlist_detail.components.PlaylistInfoSection
import luci.sixsixsix.powerampache2.presentation.playlist_detail.components.PlaylistInfoViewEvents
import luci.sixsixsix.powerampache2.presentation.songs.components.SongInfoThirdRow
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItemEvent
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun PlaylistDetailScreen(
    navigator: DestinationsNavigator,
    playlist: Playlist,
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel
) {
    val state = viewModel.state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var infoVisibility by remember { mutableStateOf(true) }

    val backgrounds = viewModel.generateBackgrounds()
    val randomBackgroundTop = backgrounds.first
    val randomBackgroundBottom = backgrounds.second

    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = randomBackgroundTop,
            contentScale = ContentScale.Crop,
            contentDescription = playlist.name
        )
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = randomBackgroundBottom,
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
                    playlist = playlist,
                    isLoading = state.isLoading,
                    scrollBehavior = scrollBehavior
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
                Column {
                    PlaylistInfoSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = if (infoVisibility) {
                                    470.dp // any big number
                                } else {
                                    0.dp
                                }
                            )
                            .padding(
                                dimensionResource(R.dimen.albumDetailScreen_infoSection_padding)
                            ),
                        playlist = playlist,
                        songs = viewModel.state.songs,
                        eventListener = { event ->
                            when(event) {
                                PlaylistInfoViewEvents.PLAY_PLAYLIST -> {
                                    viewModel.onEvent(PlaylistDetailEvent.OnPlayPlaylist)
                                    mainViewModel.onEvent(MainEvent.Play(viewModel.state.songs[0]))
                                }
                                PlaylistInfoViewEvents.SHARE_PLAYLIST -> viewModel.onEvent(PlaylistDetailEvent.OnSharePlaylist)
                                PlaylistInfoViewEvents.DOWNLOAD_PLAYLIST -> viewModel.onEvent(PlaylistDetailEvent.OnDownloadPlaylist)
                                PlaylistInfoViewEvents.SHUFFLE_PLAY_PLAYLIST -> viewModel.onEvent(PlaylistDetailEvent.OnShufflePlaylist)
                            }
                        }
                    )

                    showHideEmptyPlaylistView(playlist = playlist, state = state)

                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { viewModel.onEvent(PlaylistDetailEvent.Fetch(playlist)) }
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(state.songs.size) { i ->
                                val song = state.songs[i]
                                SongItem(
                                    song = song,
                                    songItemEventListener = { event ->
                                        when(event) {
                                            SongItemEvent.PLAY_NEXT -> mainViewModel.onEvent(MainEvent.OnAddSongToQueueNext(song))
                                            SongItemEvent.SHARE_SONG -> mainViewModel.onEvent(MainEvent.OnShareSong(song))
                                            SongItemEvent.DOWNLOAD_SONG -> mainViewModel.onEvent(MainEvent.OnDownloadSong(song))
                                            SongItemEvent.GO_TO_ALBUM -> navigator.navigate(
                                                AlbumDetailScreenDestination(albumId = song.album.id, album = null)
                                            )
                                            SongItemEvent.GO_TO_ARTIST -> navigator.navigate(
                                                ArtistDetailScreenDestination(artistId = song.artist.id, artist = null)
                                            )
                                            SongItemEvent.ADD_SONG_TO_QUEUE -> mainViewModel.onEvent(MainEvent.OnAddSongToQueue(song))
                                            SongItemEvent.ADD_SONG_TO_PLAYLIST -> {}
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onEvent(
                                                PlaylistDetailEvent.OnSongSelected(
                                                    song
                                                )
                                            )
                                            mainViewModel.onEvent(MainEvent.Play(song))
                                        },
                                    songInfoThirdRow = SongInfoThirdRow.Time,

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

@Composable
private fun showHideEmptyPlaylistView(playlist: Playlist, state: PlaylistDetailState) {
    if (
        (playlist is RecentPlaylist ||
                playlist is FrequentPlaylist ||
                playlist is HighestPlaylist ||
                playlist is FlaggedPlaylist) &&
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
