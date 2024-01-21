package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.player.RepeatMode
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel

@Composable
fun MiniPlayer(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    mainViewModel.state.song?.let {song ->
        MiniPlayerContent(
            song = song,
            modifier = modifier,
            shuffleOn = mainViewModel.shuffleOn,
            repeatMode = mainViewModel.repeatMode,
            isPlaying = mainViewModel.isPlaying,
            isBuffering = mainViewModel.isBuffering
        ) { event ->
            mainViewModel.onEvent(event)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MiniPlayerContent(
    song: Song,
    isPlaying: Boolean,
    isBuffering: Boolean,
    shuffleOn: Boolean,
    repeatMode: RepeatMode,
    modifier: Modifier = Modifier,
    onEvent: (MainEvent) -> Unit
) {
    Card(
        border = BorderStroke(
            width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .background(Color.Transparent),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.songItem_card_cornerRadius))
    ) {
        Row(
            modifier = modifier
                .padding(vertical = 5.dp, horizontal = 5.dp)
        ) {
            Card(
                border = BorderStroke(
                    width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .background(Color.Transparent),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(1.dp),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.songItem_card_cornerRadius))
            ) {
                AsyncImage(
                    model = song.imageUrl,
                    contentScale = ContentScale.FillHeight,
                    placeholder = painterResource(id = R.drawable.placeholder_album),
                    error = painterResource(id = R.drawable.ic_playlist),
                    contentDescription = song.title,
                )
            }

            Spacer(
                modifier = Modifier
                    .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer))
            )

            Column(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = song.title,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer))
                )
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = song.artist.name,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier.widthIn(min = 20.dp, max = 40.dp),
                    onClick = {
                        onEvent(MainEvent.SkipPrevious)
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.SkipPrevious,
                        contentDescription = "SkipPrevious"
                    )
                }
                IconButton(modifier = Modifier.widthIn(min = 60.dp, max = 100.dp),
                    onClick = {
                        onEvent(MainEvent.PlayPauseCurrent)
                    }) {
                    if (!isBuffering) {
                        Icon(
                            modifier = Modifier.aspectRatio(1f / 1f),
                            imageVector = if (!isPlaying) {
                                Icons.Default.PlayCircle
                            } else {
                                Icons.Default.PauseCircle
                            },
                            contentDescription = "Play"
                        )
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
                IconButton(modifier = Modifier.widthIn(min = 20.dp, max = 40.dp),
                    onClick = {
                        onEvent(MainEvent.SkipNext)
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.SkipNext,
                        contentDescription = "SkipNext"
                    )
                }
                IconButton(modifier = Modifier.widthIn(min = 20.dp, max = 40.dp),
                    onClick = {
                        onEvent(MainEvent.Shuffle(!shuffleOn))
                    }) {
                    Icon(
                        imageVector = if(!shuffleOn)
                            Icons.Outlined.Shuffle
                        else
                            Icons.Outlined.ShuffleOn,
                        contentDescription = "shuffle toggle"
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun previewMiniPlayer() {
    MiniPlayerContent(
        song = Song.mockSong,
        modifier = Modifier.width(400.dp).height(50.dp),
        isPlaying = true,
        isBuffering = true,
        shuffleOn = true,
        repeatMode = RepeatMode.OFF
    ) {}
}
