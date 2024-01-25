package luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material.icons.outlined.OfflinePin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.ShuffleOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Playlist

@Composable
fun PlaylistInfoButtonsRow(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    isPlayingPlaylist: Boolean,
    eventListener: (playlistInfoViewEvents: PlaylistInfoViewEvents) -> Unit) {
    Row(modifier = modifier
        .padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_chipsRow_padding)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                eventListener(PlaylistInfoViewEvents.DOWNLOAD_PLAYLIST)
            }) {
            Icon(
                imageVector = Icons.Outlined.DownloadForOffline,
                contentDescription = "Download"
            )
        }

        IconButton(modifier = Modifier
            .height(80.dp)
            .widthIn(min = 80.dp, max = 100.dp),
            onClick = {
                eventListener(PlaylistInfoViewEvents.PLAY_PLAYLIST)
            }) {
            Icon(
                modifier = Modifier.aspectRatio(1f/1f),
                imageVector = if (!isPlayingPlaylist) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                contentDescription = "Play Pause"
            )
        }

        IconButton(
            onClick = {
                eventListener(PlaylistInfoViewEvents.SHUFFLE_PLAY_PLAYLIST)
            }) {

            Icon(
                painterResource(id = R.drawable.ic_shuffleplay),
                contentDescription = "Shuffle Play"
            )
        }

        IconButton(
            onClick = {
                eventListener(PlaylistInfoViewEvents.SHARE_PLAYLIST)
            }) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share"
            )
        }
    }
}
