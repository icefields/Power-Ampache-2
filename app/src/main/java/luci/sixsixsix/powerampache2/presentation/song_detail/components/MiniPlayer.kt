package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumInfoViewEvents
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MiniPlayer(modifier: Modifier = Modifier,
               mainViewModel: MainViewModel = hiltViewModel()
) {
    Row(modifier = modifier.padding(vertical = 5.dp, horizontal = 5.dp)) {
        Card(
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                color = MaterialTheme.colorScheme.background
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
                model = mainViewModel.state.song?.imageUrl,
                contentScale = ContentScale.FillHeight,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.ic_playlist),
                contentDescription = mainViewModel.state.song?.title,
            )
        }

        Spacer(modifier = Modifier
            .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

        Column(modifier = Modifier.weight(1.0f).fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.basicMarquee(),
                text = mainViewModel.state.song?.title ?: Constants.ERROR_TITLE,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier
                .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
            Text(
                modifier = Modifier.basicMarquee(),
                text = mainViewModel.state.song?.artist?.name ?: Constants.ERROR_TITLE,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }

        Row(modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(modifier = Modifier.widthIn(min = 20.dp, max = 40.dp),
                onClick = {
                }) {
                Icon(
                    imageVector = Icons.Outlined.SkipPrevious,
                    contentDescription = "SkipPrevious"
                )
            }
            IconButton(modifier = Modifier.widthIn(min = 60.dp, max = 100.dp),
                onClick = {
                    mainViewModel.onEvent(MainEvent.PlayCurrent)
                }) {
                Icon(
                    modifier = Modifier.aspectRatio(1f / 1f),
                    imageVector = Icons.Outlined.PlayCircle, // Pause
                    contentDescription = "Play"
                )
            }
            IconButton(modifier = Modifier.widthIn(min = 20.dp, max = 40.dp),
                onClick = {
                }) {
                Icon(
                    imageVector = Icons.Outlined.SkipNext,
                    contentDescription = "SkipNext"
                )
            }
            IconButton(modifier = Modifier.widthIn(min = 20.dp, max = 40.dp),
                onClick = {
                }) {
                Icon(
                    imageVector = Icons.Outlined.Shuffle, //ShuffleOn
                    contentDescription = "Share"
                )
            }
        }
    }
}
