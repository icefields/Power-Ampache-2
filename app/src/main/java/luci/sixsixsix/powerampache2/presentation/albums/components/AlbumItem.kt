package luci.sixsixsix.powerampache2.presentation.albums.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Album

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumItem(album: Album, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(dimensionResource(R.dimen.albumItem_card_cornerRadius))
    ) {
        Box {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .border(dimensionResource(R.dimen.albumItem_card_border), Color.Black),
                model = album.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.ic_playlist),
                contentDescription = album.name,
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(red = 0, blue = 0, green = 0, alpha = 150))
                    .padding(
                        horizontal = dimensionResource(R.dimen.albumItem_infoText_paddingHorizontal),
                        vertical = dimensionResource(R.dimen.albumItem_infoText_paddingVertical)
                    )
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = album.name,
                    fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_title),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    lineHeight = fontDimensionResource(R.dimen.albumItem_infoTextSection_lineHeight_title)
                )
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = album.artist.name,
                    fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_artist),
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}
