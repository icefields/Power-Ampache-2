package luci.sixsixsix.powerampache2.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.navigation.HomeNavGraph
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistEvent
import luci.sixsixsix.powerampache2.presentation.playlists.components.PlaylistItem
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistsViewModel

@Composable
@HomeNavGraph(start = true)
@Destination(start = false)
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(7) { i ->
            when(i) {
                0 -> {
                    Text(text = "Recent")
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(state.recentAlbums) { album: Album ->
                            AlbumItemSquare(navigator, album)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                1 -> {
                    if (state.flaggedAlbums.isNotEmpty()) {
                        Text(text = "Flagged")
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(state.flaggedAlbums) { album: Album ->
                                AlbumItemSquare(navigator, album)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                2 -> {
                    if (state.frequentAlbums.isNotEmpty()) {
                        Text(text = "Frequent")
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(state.frequentAlbums) { album: Album ->
                                AlbumItemSquare(navigator, album)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                3 -> {
                    if (state.highestAlbums.isNotEmpty()) {
                        Text(text = "Highest")
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(state.highestAlbums) { album: Album ->
                                AlbumItemSquare(navigator, album)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                4 -> {
                    if (state.newestAlbums.isNotEmpty()) {
                        Text(text = "Newest")
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(state.newestAlbums) { album: Album ->
                                AlbumItemSquare(navigator, album)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                5 -> {
                    if (state.randomAlbums.isNotEmpty()) {
                        Text(text = "More Albums")
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(state.randomAlbums) { album: Album ->
                                AlbumItemSquare(navigator, album)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                6 -> {
                    Text(text = "Playlists")
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(state.playlists) { album: Playlist ->
                            PlaylistItemSquare(navigator, album)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun AlbumItemSquare(navigator: DestinationsNavigator, album: Album) {
    Column {
        AsyncImage(
            modifier = Modifier
                .height(120.dp)
                .width(120.dp)
                .clickable {
                    L("AlbumItemSquare navigator.navigate(AlbumDetailScreenDestination(album.id))")
                    navigator.navigate(AlbumDetailScreenDestination(album.id))
                },
            model = album.artUrl,
            placeholder = painterResource(id = R.drawable.ic_home),
            error = painterResource(id = R.drawable.ic_playlist),
            contentDescription = album.name,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = album.name, fontSize = 10.sp)
        Text(text = album.artist.name, fontSize = 10.sp)
    }
}

@Composable
fun PlaylistItemSquare(navigator: DestinationsNavigator, playlist: Playlist) {
    Column {
        AsyncImage(
            modifier = Modifier
                .height(120.dp)
                .width(120.dp)
                .clickable {
                    L("navigator.navigate(PlaylistDetailScreenDestination(playlist.id))")
                    navigator.navigate(PlaylistDetailScreenDestination(playlist.id))
                },
            model = playlist.artUrl,
            placeholder = painterResource(id = R.drawable.ic_home),
            error = painterResource(id = R.drawable.ic_playlist),
            contentDescription = playlist.name,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = playlist.name, fontSize = 10.sp)
        Text(text = "${playlist.items}", fontSize = 10.sp)
    }
}


