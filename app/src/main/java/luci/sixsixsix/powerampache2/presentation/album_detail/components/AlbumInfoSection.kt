package luci.sixsixsix.powerampache2.presentation.album_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.totalTime
import java.util.UUID

enum class AlbumInfoViewEvents {
    PLAY_ALBUM,
    SHARE_ALBUM,
    DOWNLOAD_ALBUM,
    SHUFFLE_PLAY_ALBUM,
    ADD_ALBUM_TO_PLAYLIST,
}

@Composable
fun AlbumInfoSection(
    modifier: Modifier,
    album: Album,
    eventListener: (albumInfoViewEvents: AlbumInfoViewEvents) -> Unit
) {
    Column(modifier = modifier) {
        MusicAttributeChips(
            attributes = album.genre,
            containerColor = MaterialTheme.colorScheme.background
        )

        Spacer(modifier = Modifier.height(4.dp))
        MusicAttributeChips(
            attributes = album.artists,
            containerColor = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(6.dp))
        if (album.year > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_year),
                name = "${album.year}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (album.songCount > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_songs),
                name = "${album.songCount}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (album.time > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_time),
                name = album.totalTime()
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        AlbumInfoButtonsRow(modifier = Modifier.fillMaxWidth(), album = album, eventListener)
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun AlbumInfoSectionPreview() {
    AlbumInfoSection(
        Modifier,
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
            year = 1986
        ),
        eventListener = {}
    )
}