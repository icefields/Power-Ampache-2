package luci.sixsixsix.powerampache2.presentation.screens.albums.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.AlbumInfoSection
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumItem(album: Album, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(dimensionResource(R.dimen.albumItem_card_cornerRadius))
    ) {
        Box(modifier = Modifier.background(brush = albumBackgroundGradient)) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f / 1f)
                    .border(
                        dimensionResource(R.dimen.albumItem_card_border),
                        MaterialTheme.colorScheme.background
                    ),
                model = album.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.ic_playlist),
                contentDescription = album.name,
            )
//            Box(modifier = Modifier
//                .fillMaxSize()
//                .background(brush = albumBackgroundGradient))

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(brush = albumBackgroundGradient)
                    //.background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                    .padding(
                        start = dimensionResource(R.dimen.albumItem_infoText_paddingHorizontal),
                        end = dimensionResource(R.dimen.albumItem_infoText_paddingHorizontal),
                        top = 10.dp,
                        bottom = dimensionResource(R.dimen.albumItem_infoText_paddingVertical)
                    )
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = album.name,
                        fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_title),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        lineHeight = fontDimensionResource(R.dimen.albumItem_infoTextSection_lineHeight_title),
                        style = TextStyle(
                            fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_title),
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.background,
                                offset = Offset(0.0f, 0.0f),
                                blurRadius = 18f
                            )
                        )
                    )


                    Text(
                        modifier = Modifier.basicMarquee(),
                        text = album.artist.name,
                        fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_artist),
                        fontWeight = FontWeight.Light,
                        style = TextStyle(
                            fontSize = fontDimensionResource(R.dimen.albumItem_infoTextSection_textSize_title),
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.background,
                                offset = Offset(0.0f, 0.0f),
                                blurRadius = 18f
                            )
                        )
                    )
            }
        }
    }
}

private val albumBackgroundGradient
    @Composable
    get() =
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
            )
        )

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun AlbumItemPreview() {
    AlbumItem(
        Album(
            name = "Album title",
            time = 129,
            id = UUID.randomUUID().toString(),
            songCount = 11,
            genre = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Thrash Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Progressive Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Jazz"),
            ),
            artists = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Marty Friedman"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Other people"),
            ),
            year = 1986)
    )
}
