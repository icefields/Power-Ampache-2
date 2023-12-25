package luci.sixsixsix.powerampache2.presentation.album_detail

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.navigation.AlbumsNavGraph
import luci.sixsixsix.powerampache2.presentation.songs.SongItem
import luci.sixsixsix.powerampache2.presentation.songs.SongsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
//@AlbumsNavGraph(start = false)
fun AlbumDetailScreen(
    navigator: DestinationsNavigator,
    albumId: String,
    album: Album,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state
//    val mainViewModel: MainViewModel = (LocalContext.current as MainActivity).mainViewModel

//    Toast.makeText(LocalContext.current, "AlbumDetailScreen ${mainViewModel.state.song?.title}", Toast.LENGTH_LONG).show()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Box() {

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = album.artUrl,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_image),
            //error = painterResource(id = R.drawable.ic_image),
            contentDescription = album.name,
            alpha = 0.5f

        )
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = album.artUrl,
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(id = R.drawable.ic_home),
            error = painterResource(id = R.drawable.ic_playlist),
            contentDescription = album.name,
        )
        Box(modifier = Modifier.fillMaxSize().alpha(0.5f).background(Color.Black))

        if (state.isLoading)
            LoadingScreen()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
                topBar = {

                        LargeTopAppBar(
                            modifier = Modifier.background(Color.Transparent),
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = MaterialTheme.colorScheme.surface,
                            ),
                            title = {
                                Text(text = "${album.name} - ${album.artist.name}", maxLines = 1)
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    navigator.navigateUp()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            scrollBehavior = scrollBehavior,
                            actions = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search"
                                    )
                                }
                            }
                        )
                }
            ) {
                Surface(modifier = Modifier.padding(it),
                    color = Color(red = 0, blue = 0, green = 0, alpha = 100)
                ) {
                    Column {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = { viewModel.onEvent(AlbumDetailEvent.Refresh) }
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(state.songs.size) { i ->
                                    val song = state.songs[i]
                                    SongItem(
                                        song = song,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                L("AlbumDetailScreen click $song")
                                                viewModel.onEvent(AlbumDetailEvent.OnSongSelected(song))
                                                mainViewModel.onEvent(MainEvent.Play(song))
                                            }
                                            .padding(16.dp)
                                    )

                                    if (i < state.songs.size - 1) {
                                        // if not last item add a divider
                                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

    }
}
