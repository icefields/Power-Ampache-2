package luci.sixsixsix.powerampache2.presentation.album_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.totalTime

@Composable
fun AlbumInfoSection(modifier: Modifier, album: Album) {
    Column(modifier = modifier) {
        MusicAttributeChips(album.genre)
        // TODO remove? is this necessary?
        MusicAttributeChips(album.artists)

        val horizontalPadding =
            dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)

        Spacer(modifier = Modifier.height(6.dp))
        if (album.year > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_year),
                name = "${album.year}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (album.songCount > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_songs),
                name = "${album.songCount}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (album.time > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_time),
                name = album.totalTime()
            )
        }
    }
}
