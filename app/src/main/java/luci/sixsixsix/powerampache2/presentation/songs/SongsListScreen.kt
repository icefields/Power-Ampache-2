package luci.sixsixsix.powerampache2.presentation.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItemEvent

@Composable
@Destination(start = false)
fun SongsListScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Box(modifier = modifier) {
        if (state.isLoading && state.songs.isEmpty()) {
            LoadingScreen()
        }
        Column {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.onEvent(SongsEvent.Refresh) }
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize(),) {
                    items(
                        state.songs.size,
                        // TODO conflicts, why? key = { i -> state.songs[i].key() }
                    ) { i ->
                        val song = state.songs[i]
                        SongItem(
                            song = song,
                            songItemEventListener = { event ->
                                when(event) {
                                    SongItemEvent.PLAY_NEXT -> {} // viewModel.onEvent(AlbumDetailEvent.OnAddSongToQueueNext(song))
                                    SongItemEvent.SHARE_SONG -> {} // viewModel.onEvent(AlbumDetailEvent.OnShareSong(song))
                                    SongItemEvent.DOWNLOAD_SONG -> {} // viewModel.onEvent(AlbumDetailEvent.OnDownloadSong(song))
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
                                    viewModel.onEvent(SongsEvent.OnSongSelected(song))
                                    mainViewModel.onEvent(MainEvent.Play(song))
                                }
                        )
                        // TODO decide to include or not this
                        // footer(i = i, state = state)
                    }
                }
            }
        }
    }
}

@Composable
fun footer(i: Int, state: SongsState) {
    if (i < state.songs.size - 1) {
        // if not last item add a divider
        // TODO: do I want a divider? Divider(modifier = Modifier.padding(horizontal = 16.dp))
    } else if (i == state.songs.size - 1) {
        // TODO should this screen be allowed to load more ?
        Column(modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .alpha(
                        if (state.isFetchingMore) {
                            1.0f
                        } else {
                            0.0f
                        }
                    )
            )
        }
    }
}
