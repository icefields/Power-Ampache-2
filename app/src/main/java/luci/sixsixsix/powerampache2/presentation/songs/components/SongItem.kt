package luci.sixsixsix.powerampache2.presentation.songs.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Segment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 7.dp, vertical = 6.dp)
    ) {
        Card(
            border = BorderStroke((0.5).dp, MaterialTheme.colorScheme.background),
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(1.dp),
            shape = RoundedCornerShape(1.dp)
        ) {
            AsyncImage(
                model = song.imageUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.ic_playlist),
                contentDescription = song.title,
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(
            modifier = Modifier
                .weight(5f)
                .padding(horizontal = 6.dp, vertical = 0.dp)
        ) {
            Text(
                modifier = Modifier.basicMarquee(),
                text = song.title,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.basicMarquee(),
                text = song.artist.name,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                maxLines = 1,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.basicMarquee(),
                text = song.album.name,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                maxLines = 1,
                textAlign = TextAlign.Start
            )


//                    Text(
//                        text = ("album: ${song.album.name} \nalbum-Artist: ${song.albumArtist.name}"),
//                        fontStyle = FontStyle.Italic,
//                        color = MaterialTheme.colorScheme.onBackground,
//                        maxLines = 2,
//                    )
        }
        Button(onClick = {},
            modifier = Modifier.weight(0.5f)
        ) {
            Image(
                painterResource(id = android.R.drawable.ic_menu_preferences),
                "menu",
                modifier = Modifier.background(Color.Transparent),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
