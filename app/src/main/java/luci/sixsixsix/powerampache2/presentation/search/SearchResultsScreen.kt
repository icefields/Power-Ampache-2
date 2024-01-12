package luci.sixsixsix.powerampache2.presentation.search

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.albums.AlbumsScreen
import luci.sixsixsix.powerampache2.presentation.albums.AlbumsViewModel
import luci.sixsixsix.powerampache2.presentation.artists.ArtistsScreen
import luci.sixsixsix.powerampache2.presentation.artists.ArtistsViewModel
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistsScreen
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistsViewModel
import luci.sixsixsix.powerampache2.presentation.songs.SongsListScreen
import luci.sixsixsix.powerampache2.presentation.songs.SongsViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    songsViewModel: SongsViewModel = hiltViewModel(),
    albumsViewModel: AlbumsViewModel = hiltViewModel(),
    artistsViewModel: ArtistsViewModel = hiltViewModel(),
    playlistsViewModel: PlaylistsViewModel = hiltViewModel()
) {
    val controller = LocalSoftwareKeyboardController.current
    val localFocusManager = LocalFocusManager.current

    val songsState = songsViewModel.state
    val albumsState = albumsViewModel.state
    val artistsState = artistsViewModel.state
    val playlistsState = playlistsViewModel.state

    if (songsState.isLoading &&
        albumsState.isLoading &&
        artistsState.isLoading &&
        playlistsState.isLoading
    ) {
        LoadingScreen()
    }

    Column(modifier = modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            localFocusManager.clearFocus()
        })
    }) {
        if (!songsState.isLoading &&
            !albumsState.isLoading &&
            !artistsState.isLoading &&
            !playlistsState.isLoading &&
            songsState.songs.isEmpty() &&
            albumsState.albums.isEmpty() &&
            artistsState.artists.isEmpty() &&
            playlistsState.playlists.isEmpty()
        ) {
            SearchSectionTitleText(text = "Nothing seems to match your search query")
        }

        if (songsState.songs.isNotEmpty()) {
            SearchSectionTitleText(text = "Songs")
            SongsListScreen(
                modifier = Modifier.weight(1f),
                navigator = navigator,
                mainViewModel = mainViewModel,
                viewModel = songsViewModel
            )
        }

        if (albumsState.albums.isNotEmpty()) {
            SearchSectionTitleText(text = "Albums")
            AlbumsScreen(
                modifier = Modifier.weight(1f),
                navigator = navigator,
                viewModel = albumsViewModel,
                gridItemsRow = 4,
                minGridItemsRow = 3
            )
        }

        if (playlistsState.playlists.isNotEmpty()) {
            SearchSectionTitleText(text = "Playlists")
            PlaylistsScreen(
                modifier = Modifier.weight(1f),
                navigator = navigator,
                viewModel = playlistsViewModel,
            )
        }

        if (artistsState.artists.isNotEmpty()) {
            SearchSectionTitleText(text = "Artists")
            ArtistsScreen(
                modifier = Modifier.weight(1f),
                navigator = navigator,
                viewModel = artistsViewModel,
                gridItemsRow = 4
            )
        }

        // add spacing below if empty results
        val atLeastOneEmpty = songsState.songs.isEmpty()||albumsState.albums.isEmpty()||
                artistsState.artists.isEmpty() ||playlistsState.playlists.isEmpty()
        val totalItems = songsState.songs.size + albumsState.albums.size +
                artistsState.artists.size + playlistsState.playlists.size
        if (atLeastOneEmpty && totalItems < 5) {
            Box(modifier = Modifier.weight(2.0f))
        } else if (totalItems < 11) {
            Box(modifier = Modifier.weight(1.0f))
        }
    }
}


@Composable
fun SearchSectionTitleText(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 11.dp).padding(top = 5.dp, bottom = 4.dp),
                text = text,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }


}