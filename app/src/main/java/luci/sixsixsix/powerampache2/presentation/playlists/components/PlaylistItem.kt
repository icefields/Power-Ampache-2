package luci.sixsixsix.powerampache2.presentation.playlists.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Playlist

@Composable
fun PlaylistItem(
    playlistInfo: Playlist,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.songItem_row_paddingHorizontal),
                vertical = dimensionResource(id = R.dimen.songItem_row_paddingVertical)
            )
    ) {
        Card(
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)
                .align(Alignment.CenterVertically),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(1.dp),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.songItem_card_cornerRadius))
        ) {
            AsyncImage(
                model = playlistInfo.artUrl,
                contentScale = ContentScale.FillWidth,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.ic_playlist),
                contentDescription = playlistInfo.name,
            )
        }

        Spacer(modifier = Modifier
            .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

        InfoTextSection(
            modifier = Modifier
                .weight(5f)
                .padding(
                    horizontal = dimensionResource(R.dimen.songItem_infoTextSection_paddingHorizontal),
                    vertical = dimensionResource(R.dimen.songItem_infoTextSection_paddingVertical)
                )
                .align(Alignment.CenterVertically),
            playlistInfo = playlistInfo
        )

        Button(
            onClick = {},
            modifier = Modifier.weight(0.5f)
        ) {
            Image(
                painterResource(id = android.R.drawable.ic_menu_preferences),
                stringResource(id = R.string.menu_content_description),
                modifier = Modifier.background(Color.Transparent),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }
    Spacer(modifier = Modifier
        .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer) * 2))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InfoTextSection(modifier: Modifier, playlistInfo: Playlist) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.basicMarquee(),
            text = playlistInfo.name,
            fontWeight = FontWeight.Normal,
            fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_title),
            maxLines = 1,
        )
        Spacer(modifier = Modifier
            .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
        Text(
            modifier = Modifier.basicMarquee(),
            text = playlistInfo.items?.let {
                stringResource(id = R.string.playlistItem_songCount, it)
            } ?: run { " " } ,
            fontWeight = FontWeight.Light,
            fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_artist),
            maxLines = 1,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier
            .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
        Text(
            modifier = Modifier.basicMarquee(),
            text = playlistInfo.type ?: "",
            fontWeight = FontWeight.Light,
            fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_album),
            maxLines = 1,
            textAlign = TextAlign.Start
        )
    }
}
