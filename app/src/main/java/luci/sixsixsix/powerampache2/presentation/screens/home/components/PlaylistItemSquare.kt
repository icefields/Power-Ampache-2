package luci.sixsixsix.powerampache2.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import luci.sixsixsix.powerampache2.domain.models.isSmartPlaylist
import kotlin.random.Random

private val width = 150.dp
private val height = 75.dp

@Composable
fun PlaylistItemSquare(
    modifier: Modifier = Modifier,
    playlistColumn: PlaylistColumn,
    onPlaylistClick: (playlist: Playlist) -> Unit
) {
    Column {
        for(playlist in playlistColumn) {
            ColourPlaylistHomeItem(
                playlist = playlist,
                modifier = modifier,
                colour = RandomThemeBackgroundColour("${playlist.id}${playlist.name}"), // random bg colour based on hashcode
                onPlaylistClick = onPlaylistClick
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ColourPlaylistHomeItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    colour: Color,
    onPlaylistClick: (playlist: Playlist) -> Unit
) {
    Card(
        border = BorderStroke((0.5).dp, MaterialTheme.colorScheme.background),
        modifier = modifier
            .width(width)
            .height(height)
            .padding(horizontal = 4.dp)
            .clickable {
                onPlaylistClick(playlist)
            },
        colors = CardDefaults.cardColors(
            containerColor = colour
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            getIconForPlaylist(playlist)?.let { icon ->
                Icon(
                    modifier = Modifier.size(30.dp)
                        .padding(4.dp),
                    imageVector = icon,
                    contentDescription = null,
                    //tint = MaterialTheme.colorScheme.secondary
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    text = playlist.name,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    lineHeight = (16).sp
                )
            }
        }
    }
}

@Composable
private fun getIconForPlaylist(playlist: Playlist) =
    when(playlist) {
        is FlaggedPlaylist -> Icons.Outlined.FavoriteBorder
        is HighestPlaylist -> Icons.Outlined.StarOutline
        is FrequentPlaylist -> Icons.Outlined.Timeline
        is RecentPlaylist -> Icons.Outlined.NewReleases
        else -> if (playlist.isSmartPlaylist()) {
            ImageVector.vectorResource(id = R.drawable.ic_power_ampache_mono)
        } else null
    }


@Composable @Preview
fun PreviewPlaylistItemHome() {
    PlaylistItemSquare(playlistColumn = PlaylistColumn(
        listOf(Playlist.mock(), Playlist.mock())
    ), onPlaylistClick = { })
}
