package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongDetailPlayerBar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    Row(
        modifier = modifier
            .padding(vertical = 9.dp, horizontal = 9.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(modifier = Modifier.weight(1f),
            onClick = {
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                imageVector = Icons.Outlined.Repeat,
                contentDescription = "Repeat"
            )
        }
        IconButton(modifier = Modifier.weight(1f),
            onClick = {
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Outlined.SkipPrevious,
                contentDescription = "SkipPrevious"
            )
        }
        IconButton(modifier = Modifier
            .height(80.dp)
            .widthIn(min = 100.dp, max = 100.dp),
            onClick = {
                mainViewModel.onEvent(MainEvent.PlayCurrent)
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.aspectRatio(1f / 1f),
                imageVector = Icons.Default.PlayCircle, // Pause
                contentDescription = "Play"
            )
        }
        IconButton(modifier = Modifier.weight(1f),
            onClick = {
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Outlined.SkipNext,
                contentDescription = "SkipNext"
            )
        }

        IconButton(modifier = Modifier.weight(1f),
            onClick = {
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                imageVector = Icons.Outlined.Shuffle, //ShuffleOn
                contentDescription = "Shuffle"
            )
        }
    }
}
