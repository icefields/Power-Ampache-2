package luci.sixsixsix.powerampache2.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination

@Composable
fun HomeScreenSection(navigator: DestinationsNavigator, albumsRow: List<Any>?, text: String) {
    if (!albumsRow.isNullOrEmpty()) {
        Column(modifier = Modifier.height(280.dp)) {
            SectionTitle(text = text)
            SectionRow(navigator = navigator, albumsRow = albumsRow)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
    )
}

@Composable
fun SectionRow(navigator: DestinationsNavigator, albumsRow: List<Any>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(albumsRow) { album: Any ->
            when(album) {
                is Album -> AlbumItemSquare(
                    modifier = Modifier.clickable {
                        navigator.navigate(AlbumDetailScreenDestination(album.id, album))},
                    album = album
                )
                is Playlist -> PlaylistItemSquare(
                    modifier = Modifier.clickable {
                        navigator.navigate(PlaylistDetailScreenDestination(album.id,))},
                    playlist = album
                )
            }
        }
    }
}
