/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.RepeatOn
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.RepeatOneOn
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.ShuffleOn
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.player.RepeatMode
import luci.sixsixsix.powerampache2.presentation.main.MainEvent

@Composable
fun SongDetailPlayerBar(
    isPlaying:Boolean,
    isBuffering:Boolean,
    shuffleOn: Boolean,
    repeatMode: RepeatMode,
    progress: Float,
    durationStr: String,
    progressStr: String,
    modifier: Modifier = Modifier,
    onEvent: (MainEvent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 9.dp, horizontal = 9.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        PlayerControls(
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            shuffleOn = shuffleOn,
            repeatMode = repeatMode,
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

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isBuffering: Boolean,
    shuffleOn: Boolean,
    repeatMode: RepeatMode,
    modifier: Modifier = Modifier,
    onEvent: (MainEvent) -> Unit
) {
    val tint = MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,

    ) {
        IconButton(modifier = Modifier.weight(1f),
            onClick = {
                onEvent(MainEvent.Repeat) // TODO repeat one or all
            }) {
            Icon(
                tint = tint,
                imageVector = when(repeatMode) {
                    RepeatMode.OFF -> Icons.Outlined.Repeat
                    RepeatMode.ONE -> Icons.Outlined.RepeatOneOn
                    RepeatMode.ALL -> Icons.Outlined.RepeatOn
                },
                contentDescription = "Repeat"
            )
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = {
            }) {
            Icon(
                tint = tint,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
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

        PlayButton(
            modifier = Modifier
                .height(80.dp)
                .width(100.dp),
            iconTint = tint,
            background = Color.Transparent,
            isBuffering = isBuffering,
            isPlaying = isPlaying
        ) { onEvent(MainEvent.PlayPauseCurrent) }

        IconButton(modifier = Modifier.weight(1f),
            onClick = {
                onEvent(MainEvent.SkipNext)
            }) {
            Icon(
                tint = tint,
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Outlined.SkipNext,
                contentDescription = "SkipNext"
            )
        }

        IconButton(modifier = Modifier.weight(1f),
            onClick = {
                onEvent(MainEvent.Shuffle(!shuffleOn))
            }) {
            Icon(
                tint = tint,
                imageVector = if(!shuffleOn)
                    Icons.Outlined.Shuffle
                else
                    Icons.Outlined.ShuffleOn,
                contentDescription = "Shuffle"
            )
        }
    }
}

@Composable
fun PlayButton(
    modifier: Modifier = Modifier,
    isBuffering: Boolean,
    isPlaying: Boolean,
    background: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    onEvent: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = { onEvent() }
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = background)) {
            Box(modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.Center) {
                if (!isBuffering) {
                    Icon(
                        tint = iconTint,
                        modifier = Modifier.aspectRatio(1f / 1f),
                        imageVector = if (!isPlaying) {
                            Icons.Default.PlayCircle
                        } else {
                            Icons.Default.PauseCircle
                        },
                        contentDescription = "Play"
                    )
                } else {
                    CircularProgressIndicator()
                }
            }
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
    var useNewProgressiveValue by rememberSaveable { mutableStateOf(false) }

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
        progressStr = "5:32",
        repeatMode = RepeatMode.ONE,
        shuffleOn = true
    ) {}
}