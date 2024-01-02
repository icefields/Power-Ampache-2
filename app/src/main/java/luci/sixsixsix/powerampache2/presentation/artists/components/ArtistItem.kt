package luci.sixsixsix.powerampache2.presentation.artists.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Artist

@Composable
fun ArtistItem(
    artist: Artist,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.aspectRatio(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .sizeIn(maxHeight = 200.dp)
                    .border(1.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
                model = artist.artUrl,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.ic_playlist),
                contentDescription = artist.name,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = artist.name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = (14).sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(1.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            if (artist.songCount > 0) {
                Text(
                    text = ("Songs ${artist.songCount} | Albums ${artist.albumCount}"),
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
@Preview(widthDp = 150, heightDp = 150)
fun PreviewArtistItem() {
    ArtistItem(artist = Artist.mockArtist())
}
