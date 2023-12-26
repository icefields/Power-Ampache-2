package luci.sixsixsix.powerampache2.presentation.album_detail


import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem
import java.lang.StringBuilder

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
                Surface(modifier = Modifier.padding(it).padding(top = 5.dp).background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(red = 0, blue = 0, green = 0, alpha = 220),
                            Color(red = 0, blue = 0, green = 0, alpha = 240),
                            Color.Black,
                            Color.Black,
                        )
                    )
                ),
                    color = Color.Transparent//Color(red = 0, blue = 0, green = 0, alpha = 190)
                ) {
                    Column {
                        Box (modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = if (infoVisibility) { 470.dp } else { 0.dp })
                            .padding(10.dp)
                        ) {
                            Text(text = albumToString(album), fontWeight = FontWeight.Normal, fontSize = 19.sp)
                        }

                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = { viewModel.onEvent(AlbumDetailEvent.Refresh) }
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()

                                    //.background(Color(red = 0, blue = 0, green = 0, alpha = 120))
                            ) {
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
                                    )
                                }
                            }
                        }
                    }
                }
            }

    }
}

fun albumToString(album: Album): String {
    val sb = StringBuilder()
        for (field in album.javaClass.declaredFields) {
            field.isAccessible = true

            field.get(album)?.let {
                if(
                    !field.name.lowercase().contains("url") &&
                    !field.name.lowercase().contains("artist") &&
                    !field.name.lowercase().contains("CREATOR") &&
                    !field.name.lowercase().contains("\$stable") &&
                    "$it".isNotBlank() &&
                    "$it" != "0" &&
                    !"$it".contains("CREATOR") &&
                    !"$it".contains("\$stable") &&
                    "$it" != "[]"
                ) {
                    if (it is List<*>) {
                        if (field.name != "genre") {
                            sb.append(field.name)
                                .append(": ")
                        } else {
                            sb.append(" | ")
                        }

                        it.forEach { listElem ->
                            listElem?.let {
                                if (listElem is MusicAttribute) {
                                    sb.append(listElem.name)
                                    sb.append(" | ")
                                }
                            }
                        }
                        sb.append("\n")


                    }
                    else if (it is MusicAttribute) {
                        sb.append(field.name)
                            .append(": ")
                            .append("${it.name}")
                            .append("\n")
                    } else {
                        sb.append(field.name)
                            .append(": ")
                            .append("${field.get(album)}")
                            .append("\n")
                    }
                }
            }
        }
    return sb.toString().split("CREATOR")[0]
}
