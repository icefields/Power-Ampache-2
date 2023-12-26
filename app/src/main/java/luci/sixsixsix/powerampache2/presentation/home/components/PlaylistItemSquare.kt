package luci.sixsixsix.powerampache2.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistItemSquare(modifier: Modifier = Modifier, playlist: Playlist) {
    val width = 150.dp
    val imageSize = 150.dp
    Column(
        modifier = modifier
            .width(width)
            .padding(horizontal = 4.dp)
    ) {
        Card(
            border = BorderStroke((0.5).dp, Color.Black),
            modifier = Modifier.background(Color.Transparent),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(1.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .height(imageSize)
                    .width(imageSize),
                model = playlist.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.ic_playlist),
                contentDescription = playlist.name,
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = playlist.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                lineHeight = (16).sp
            )
        }
    }
}
