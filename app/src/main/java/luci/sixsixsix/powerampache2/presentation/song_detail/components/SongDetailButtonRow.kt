package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumInfoViewEvents

@Composable
fun SongDetailButtonRow(
    modifier: Modifier = Modifier,
    song : Song,
    eventListener: (albumInfoViewEvents: AlbumInfoViewEvents) -> Unit
) {
    val fontSize = 11.sp
    val tint = MaterialTheme.colorScheme.tertiary
    Row(
        modifier = modifier
        .padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_chipsRow_padding)).padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    eventListener(AlbumInfoViewEvents.ADD_ALBUM_TO_PLAYLIST)
                }) {
                Icon(
                    tint = tint,
                    imageVector = Icons.Outlined.AddBox,
                    contentDescription = "Add to playlist"
                )
            }

            Text(text = "Add", fontSize = fontSize, fontWeight = FontWeight.Medium, color = tint)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    eventListener(AlbumInfoViewEvents.DOWNLOAD_ALBUM)
                }) {
                Icon(
                    tint = tint,
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Download"
                )
            }
            Text(text = "Download", fontSize = fontSize, fontWeight = FontWeight.Medium, color = tint)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconButton(
                onClick = {
                    eventListener(AlbumInfoViewEvents.SHARE_ALBUM)
                }) {
                Icon(
                    tint = tint,
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share"
                )
            }
            Text(text = "Share", fontSize = fontSize, fontWeight = FontWeight.Medium, color = tint)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconButton(
                onClick = {
                    //eventListener(AlbumInfoViewEvents.SHARE_ALBUM)
                }) {
                Icon(
                    tint = tint,
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info"
                )
            }
            Text(text = "Info", fontSize = fontSize, fontWeight = FontWeight.Medium, color = tint)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconButton(
                onClick = {
                    //eventListener(AlbumInfoViewEvents.SHARE_ALBUM)
                }) {
                Icon(
                    tint = tint,
                    imageVector = Icons.Outlined.Album,
                    contentDescription = "Go to Album"
                )
            }
            Text(text = "Album", fontSize = fontSize, fontWeight = FontWeight.Medium, color = tint)
        }
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    //eventListener(AlbumInfoViewEvents.SHARE_ALBUM)
                }) {
                Icon(
                    tint = tint,
                    imageVector = Icons.Outlined.Audiotrack,
                    contentDescription = "Go to Artist"
                )
            }
            Text(text = "Artist", fontSize = fontSize, fontWeight = FontWeight.Medium, color = tint)
        }
    }
}
