package luci.sixsixsix.powerampache2.presentation.song_detail


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel

import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.navigation.AlbumsNavGraph
import luci.sixsixsix.powerampache2.presentation.navigation.ArtistsNavGraph

@Composable
fun SongDetailScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        AsyncImage(
            modifier = Modifier
                .weight(7f)
                .fillMaxWidth(),
            model = state.song?.imageUrl,
            placeholder = painterResource(id = R.drawable.ic_home),
            error = painterResource(id = R.drawable.ic_playlist),
            contentDescription = state.song?.title,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = state.song?.title ?: "",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.width(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(2.0f))
            IconButton(
                onClick = {
                    state.song?.let {
                        viewModel.onEvent(MainEvent.Play(it))
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayCircle,
                    contentDescription = state.song?.title ?: "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxSize()
                )
            }
            Box(modifier = Modifier.weight(2.0f))
        }
        
        LazyColumn(modifier = Modifier.weight(3.0f)) {
            items(1) {
                Text(
                    text = "${state.song?.copy(imageUrl = "", songUrl = "", filename = "")}"
                        .replace(", ","\n"),
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 30,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

        }
    }
}
