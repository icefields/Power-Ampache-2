package luci.sixsixsix.powerampache2.presentation.playlist_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.album_detail.components.AttributeText

enum class PlaylistInfoViewEvents {
    PLAY_PLAYLIST,
    SHARE_PLAYLIST,
    DOWNLOAD_PLAYLIST,
    SHUFFLE_PLAY_PLAYLIST
}

@Composable
fun PlaylistInfoSection(
    modifier: Modifier,
    playlist: Playlist,
    eventListener: (playlistInfoViewEvents: PlaylistInfoViewEvents) -> Unit
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(6.dp))
        playlist.items?.let { itemCount ->
            if (itemCount > 0) {
                AttributeText(
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                    title = stringResource(id = R.string.albumDetailScreen_infoSection_songs),
                    name = "$itemCount"
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (playlist.averageRating > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.playlistDetailScreen_infoSection_avgRating),
                name = "${playlist.averageRating}"
            )
        }
        if (!playlist.type.isNullOrBlank()) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = "",
                name = playlist.type
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        PlaylistInfoButtonsRow(modifier = Modifier.fillMaxWidth(), playlist = playlist, eventListener)
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun PlaylistInfoSectionPreview() {
    PlaylistInfoSection(
        Modifier,
        Playlist.mock(),
        eventListener = {}
    )
}
