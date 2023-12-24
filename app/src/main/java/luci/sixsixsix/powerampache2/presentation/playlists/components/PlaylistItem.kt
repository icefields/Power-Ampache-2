package luci.sixsixsix.powerampache2.presentation.playlists.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Playlist

@Composable
fun PlaylistItem(
    playlist: Playlist,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    modifier = Modifier.weight(1f),
                    model = playlist.artUrl,
                    placeholder = painterResource(id = R.drawable.ic_home),
                    error = painterResource(id = R.drawable.ic_playlist),
                    contentDescription = playlist.name,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = playlist.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(3f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${playlist.type}",
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ("Songs: ${playlist.items}"),
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
            )
        }
    }
}
