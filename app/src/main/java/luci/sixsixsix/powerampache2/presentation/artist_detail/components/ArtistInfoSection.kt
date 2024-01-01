package luci.sixsixsix.powerampache2.presentation.artist_detail.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.totalTime
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AlbumInfoButtonsRow
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AttributeText
import luci.sixsixsix.powerampache2.presentation.album_detail.components.MusicAttributeChips
import java.util.UUID

enum class ArtistInfoViewEvents {
    SHARE_ARTIST
}

@Composable
fun ArtistInfoSection(
    modifier: Modifier,
    artist: Artist,
    eventListener: (albumInfoViewEvents: ArtistInfoViewEvents) -> Unit
) {
    var summaryOpen by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        MusicAttributeChips(
            attributes = artist.genre,
            containerColor = MaterialTheme.colorScheme.background
        )

        Spacer(modifier = Modifier.height(6.dp))
        if (artist.yearFormed > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_year),
                name = "${artist.yearFormed}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (artist.songCount > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_songs),
                name = "${artist.songCount}"
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (!artist.summary.isNullOrBlank()) {
            Text( // name
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                          summaryOpen = !summaryOpen
                },
                text = artist.summary,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                maxLines = if (summaryOpen) { 50 } else { 5 },
                lineHeight = 17.sp
            )
        }
//        Spacer(modifier = Modifier.height(4.dp))
//        ArtistInfoButtonsRow(modifier = Modifier.fillMaxWidth(), artist = artist, eventListener)
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun ArtistInfoSectionPreview() {
    ArtistInfoSection(
        Modifier,
        Artist.mockArtist(),
        eventListener = {}
    )
}
