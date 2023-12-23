package luci.sixsixsix.powerampache2.presentation.playlist_detail

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.presentation.navigation.PlaylistsNavGraph

import luci.sixsixsix.powerampache2.presentation.songs.SongItem

@Composable
@PlaylistsNavGraph
@Destination
fun PlaylistDetailScreen(
    navigator: DestinationsNavigator,
    playlistId: String,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(PlaylistDetailEvent.Refresh) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.songs.size) { i ->
                    val song = state.songs[i]
                    SongItem(
                        song = song,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(PlaylistDetailEvent.OnSongSelected(song))
                                //mainViewModel.state = mainViewModel.state.copy(currentSong = song)
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
