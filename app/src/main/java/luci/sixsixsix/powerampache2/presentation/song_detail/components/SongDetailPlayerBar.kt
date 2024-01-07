package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.presentation.main.MainEvent

@Composable
fun SongDetailPlayerBar(
    isPlaying:Boolean,
    isBuffering:Boolean,
    progress: Float,
    durationStr: String,
    progressStr: String,
    modifier: Modifier = Modifier,
    onEvent: (MainEvent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 9.dp, horizontal = 9.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        PlayerControls(
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            modifier = Modifier.fillMaxWidth(),
            onEvent = onEvent
        )
        PlayerTimeSlider(
            progress = progress,
            durationStr = durationStr,
            progressStr = progressStr,
            onEvent = onEvent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isBuffering: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (MainEvent) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(modifier = Modifier.weight(1f),
            onClick = {
                onEvent(MainEvent.Repeat) // TODO repeat one or all
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                imageVector = Icons.Outlined.Repeat,
                contentDescription = "Repeat"
            )
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = {
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onEvent(MainEvent.Backwards) },
                        onDoubleTap = { onEvent(MainEvent.Backwards) },
                        onPress = { onEvent(MainEvent.SkipPrevious) }
                    )
                },
                imageVector = Icons.Outlined.SkipPrevious,
                contentDescription = "SkipPrevious"
            )
        }

        IconButton(
            modifier = Modifier
            .height(80.dp)
            .widthIn(min = 100.dp, max = 100.dp),
            onClick = {
                onEvent(MainEvent.PlayPauseCurrent)
            }) {
            if (!isBuffering) {
                Icon(
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.aspectRatio(1f / 1f),
                    imageVector = if (!isPlaying) {
                        Icons.Default.PlayCircle
                    } else {
                        Icons.Default.PauseCircle
                    }, // Pause
                    contentDescription = "Play"
                )
            } else {
                Card(modifier = Modifier
                    .height(80.dp)
                    .widthIn(min = 100.dp, max = 100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                }
            }
        }

        IconButton(modifier = Modifier.weight(1f),
            onClick = {
                onEvent(MainEvent.SkipNext)
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
                onEvent(MainEvent.Shuffle)
            }) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                imageVector = Icons.Outlined.Shuffle, //ShuffleOn
                contentDescription = "Shuffle"
            )
        }
    }
}

@Composable
fun PlayerTimeSlider(
    progress: Float,
    durationStr: String,
    progressStr: String,
    onEvent: (MainEvent) -> Unit
) {
    var newProgressiveValue by remember { mutableFloatStateOf(0.0f) }
    var useNewProgressiveValue by remember { mutableStateOf(false) }

    Column (modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = if (useNewProgressiveValue) newProgressiveValue else progress,
            onValueChange = { newValue ->
                useNewProgressiveValue = true
                newProgressiveValue = newValue
                onEvent(MainEvent.UpdateProgress(newProgress = newValue))
            },
            onValueChangeFinished = {
                useNewProgressiveValue = false
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = progressStr, fontSize = 11.sp)
            Text(text = durationStr, fontSize = 11.sp)
        }
    }
}

@Composable
@Preview
fun PreviewSongDetailPlayerBar() {
    SongDetailPlayerBar(
        isPlaying = false,
        isBuffering = true,
        progress = 12f,
        durationStr = "3:40",
        progressStr = "5:32"
    ) {}
}