package luci.sixsixsix.powerampache2.presentation.album_detail

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumDetailTopBar
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumInfoSection
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumInfoViewEvents
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.songs.components.SongInfoThirdRow
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItemEvent
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun AlbumDetailScreen(
    navigator: DestinationsNavigator,
    albumId: String,
    album: Album,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var infoVisibility by remember { mutableStateOf(true) }

    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = album.artUrl,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.ic_image),
            contentDescription = album.name
        )
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = album.artUrl,
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.ic_playlist),
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
                    AlbumInfoSection(
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
                        album = album,
                        eventListener = { event ->
                            when(event) {
                                AlbumInfoViewEvents.PLAY_ALBUM -> viewModel.onEvent(AlbumDetailEvent.OnPlayAlbum)
                                AlbumInfoViewEvents.SHARE_ALBUM -> viewModel.onEvent(AlbumDetailEvent.OnShareAlbum)
                                AlbumInfoViewEvents.DOWNLOAD_ALBUM -> viewModel.onEvent(AlbumDetailEvent.OnDownloadAlbum)
                                AlbumInfoViewEvents.SHUFFLE_PLAY_ALBUM -> viewModel.onEvent(AlbumDetailEvent.OnShuffleAlbum)
                                AlbumInfoViewEvents.ADD_ALBUM_TO_PLAYLIST -> viewModel.onEvent(AlbumDetailEvent.OnAddAlbumToQueue)
                            }
                        }
                    )

                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { viewModel.onEvent(AlbumDetailEvent.Refresh) }
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
                                            SongItemEvent.PLAY_NEXT -> viewModel.onEvent(AlbumDetailEvent.OnAddSongToQueueNext(song))
                                            SongItemEvent.SHARE_SONG -> viewModel.onEvent(AlbumDetailEvent.OnShareSong(song))
                                            SongItemEvent.DOWNLOAD_SONG -> viewModel.onEvent(AlbumDetailEvent.OnDownloadSong(song))
                                            SongItemEvent.GO_TO_ALBUM -> navigator.navigate(AlbumDetailScreenDestination(album.id, album))
                                            SongItemEvent.GO_TO_ARTIST -> navigator.navigate(ArtistDetailScreenDestination(album.artist.id))
                                            SongItemEvent.ADD_SONG_TO_QUEUE -> viewModel.onEvent(AlbumDetailEvent.OnAddSongToQueue(song))
                                            SongItemEvent.ADD_SONG_TO_PLAYLIST -> {}
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onEvent(AlbumDetailEvent.OnSongSelected(song))
                                            mainViewModel.onEvent(MainEvent.Play(song))
                                        },
                                    songInfoThirdRow = SongInfoThirdRow.Time,

                                )
                            }
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
    AlbumDetailScreen(
        navigator = EmptyDestinationsNavigator,
        albumId = "1050",
        album = Album(
            name = "Album title",
            time = 129,
            id = UUID.randomUUID().toString(),
            songCount = 11,
            genre = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Thrash Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Progressive Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Jazz"),
            ),
            artists = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Marty Friedman"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Other people"),
            ),
            year = 1986)
    )
}