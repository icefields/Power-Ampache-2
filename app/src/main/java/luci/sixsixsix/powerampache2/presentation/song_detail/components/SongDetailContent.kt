package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel

@Composable
fun SongDetailContent(
    // navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(),
            model = state.song?.imageUrl,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.ic_playlist),
            contentDescription = state.song?.title,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = state.song?.title ?: "",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            //color = MaterialTheme.colorScheme.primary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = state.song?.artist?.name ?: "",
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Divider()
        SongDetailButtonRow(modifier = Modifier.fillMaxWidth(), song = viewModel.state.song ?: Song.mockSong) {

        }
        Divider()

        Spacer(modifier = Modifier.height(12.dp))

        SongDetailPlayerBar(modifier = Modifier.fillMaxWidth())

        LazyColumn(modifier = Modifier.weight(1.0f)) {
//            items(1) {
//                Text(
//                    text = "${state.song?.toDebugString()}",
//                    fontWeight = FontWeight.Light,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    maxLines = 30,
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.End
//                )
//            }
        }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//
//        LazyColumn(modifier = Modifier.weight(1.0f)) {
//            items(1) {
//                Text(
//                    text = "${state.song?.toDebugString()}",
//                    fontWeight = FontWeight.Light,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    maxLines = 30,
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.End
//                )
//            }
//        }
//
//        LazyColumn(modifier = Modifier.weight(3.0f)) {
//            items(viewModel.state.queue.toList()) { song ->
//                Text(
//                    text = "${song.title} - ${song.artist.name}",
//                    fontWeight = FontWeight.Light,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    maxLines = 1,
//                    modifier = Modifier.fillMaxWidth())
//            }
//        }
    }
}
