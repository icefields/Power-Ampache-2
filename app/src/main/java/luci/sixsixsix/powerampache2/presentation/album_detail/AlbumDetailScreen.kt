package luci.sixsixsix.powerampache2.presentation.album_detail


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.common.io.Files.append
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.totalTime
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var infoVisibility by remember { mutableStateOf(true) }

    Box {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            model = album.artUrl,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.ic_image),
            contentDescription = album.name,
            //alpha = 0.5f
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
        Box(modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
            .background(Color.Black))

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
                                Text(
                                    modifier = Modifier.basicMarquee(),
                                    text = "${album.name} - ${album.artist.name}",
                                    maxLines = 1)
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
                                IconButton(onClick = {
                                    infoVisibility = !infoVisibility
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search"
                                    )
                                }
                            }
                        )
                }
            ) {
                Surface(
                    modifier = Modifier
                        .padding(it)
                        .padding(top = 5.dp)
                        .background(brush = albumBackgroundGradientDark),
                    color = Color.Transparent
                ) {
                    Column {
                        AlbumInfoSection(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(
                                    max = if (infoVisibility) {
                                        470.dp
                                    } else {
                                        0.dp
                                    }
                                )
                                .padding(10.dp), 
                            album = album
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.onEvent(AlbumDetailEvent.OnSongSelected(song))
                                                mainViewModel.onEvent(MainEvent.Play(song))
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun AlbumInfoSection(modifier: Modifier, album: Album) {
    Column (modifier = modifier) {
        GenreChips(album.genre)
        // TODO remove? is this necessary?
        GenreChips(album.artists)

        Spacer(modifier = Modifier.height(6.dp))
        if (album.year > 0) {
            InfoText(
                modifier = Modifier.padding(horizontal = 6.dp),
                title = "Year",
                name = "${album.year}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (album.songCount > 0) {
            InfoText(
                modifier = Modifier.padding(horizontal = 6.dp),
                title = "Songs",
                name = "${album.songCount}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (album.time > 0) {
            InfoText(
                modifier = Modifier.padding(horizontal = 6.dp),
                title = "Total time",
                name = album.totalTime()
            )
        }
    }
}

@Composable
fun InfoText(modifier: Modifier = Modifier, title: String, name: String) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = title,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }
}

@Composable
fun GenreChips(attributes: List<MusicAttribute>) {
    LazyRow {
        items(attributes) {
            Row {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(red = 0, blue = 0, green = 0, alpha = 180)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        text = it.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

val albumBackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(red = 0, blue = 0, green = 0, alpha = 150),
        Color.Black,
        Color(red = 0, blue = 0, green = 0, alpha = 240),
        Color(red = 0, blue = 0, green = 0, alpha = 220),
        Color(red = 0, blue = 0, green = 0, alpha = 200),
        Color(red = 0, blue = 0, green = 0, alpha = 170),
        Color(red = 0, blue = 0, green = 0, alpha = 150),
        Color(red = 0, blue = 0, green = 0, alpha = 130),
    )
)
