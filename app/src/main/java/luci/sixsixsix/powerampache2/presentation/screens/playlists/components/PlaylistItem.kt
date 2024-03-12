/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.screens.playlists.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.StarRate
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.common.dpTextUnit
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.isFavourite
import luci.sixsixsix.powerampache2.domain.models.isOwnerAdmin
import luci.sixsixsix.powerampache2.domain.models.isOwnerSystem
import luci.sixsixsix.powerampache2.domain.models.isSmartPlaylist
import luci.sixsixsix.powerampache2.presentation.common.StarRatingIcon
import luci.sixsixsix.powerampache2.presentation.common.SwipeToDismissItem

@Composable
fun PlaylistItemMain(
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
                containerColor = RandomThemeBackgroundColour(playlistInfo.id)
            ),
            elevation = CardDefaults.cardElevation(1.dp),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.songItem_card_cornerRadius))
        ) {
            val isSmartPlaylist = playlistInfo.isSmartPlaylist()
            Box {
                if(!isSmartPlaylist) {
                AsyncImage(
                    model = if(!isSmartPlaylist) playlistInfo.artUrl else "",
                    contentScale = ContentScale.FillWidth,
                    placeholder = painterResource(id = R.drawable.placeholder_album),
                    error = painterResource(id = R.drawable.placeholder_album),
                    contentDescription = playlistInfo.name,
                    colorFilter = if(isSmartPlaylist) {
                        ColorFilter.lighting(
                            add = Color.Black.copy(alpha = 0.4f),
                            multiply = RandomThemeBackgroundColour(playlistInfo.id)
                        )
                    } else null
                )} else {
                    Icon(
                        painter = painterResource(id = R.drawable.placeholder_album),
                        contentDescription = "smart list image",
                        //tint = RandomThemeBackgroundColour(playlistInfo.id)
                    )
                }
            }

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
    }
    Spacer(modifier = Modifier
        .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer) * 2))
}

@Composable
fun PlaylistItem(
    playlistInfo: Playlist,
    modifier: Modifier = Modifier,
    enableSwipeToRemove: Boolean,
    onRemove: (Playlist) -> Unit
) {
    SwipeToDismissItem(
        item = playlistInfo,
        foregroundView = {
            PlaylistItemMain(playlistInfo, modifier)
        },
        enableSwipeToRemove = enableSwipeToRemove,
        onRemove = onRemove,
        onRightToLeftSwipe = onRemove,
        iconRight = Icons.Default.Delete,
    )
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
            .fillMaxWidth()
        ) {
            Column(modifier = Modifier
                .weight(1f)
            ) {
                val ownerText =
                    playlistInfo.owner?.let { owner ->
                        if (!playlistInfo.isOwnerSystem() && !playlistInfo.isOwnerAdmin()) {
                            owner
                        } else ""
                    } ?: ""

                Text(
                    modifier = Modifier.basicMarquee(),
                    text = playlistInfo.items?.let {
                        if (it > 0) stringResource(id = R.string.playlistItem_songCount, it) else " "
                    } ?: run { " " } ,
                    fontWeight = FontWeight.Light,
                    fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_artist),
                    maxLines = 1,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = ownerText,
                    fontWeight = FontWeight.Light,
                    fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_album),
                    maxLines = 1,
                    textAlign = TextAlign.Start
                )
            }
            if (playlistInfo.rating > 0) {
                StarRatingIcon(playlistInfo.rating)
                Spacer(modifier = Modifier.width(6.dp))
            }
            if (playlistInfo.isSmartPlaylist()) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_power_ampache_mono),
                    contentDescription = "smart playlist")
            }
            if (playlistInfo.type == PlaylistType.public) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.playlistItem_icon_size)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = Icons.Rounded.Public,
                    contentDescription = "public playlist")
                Spacer(modifier = Modifier.width(4.dp))
            }
            if (playlistInfo.isFavourite()) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.playlistItem_icon_size)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "favorite playlist")
            }
        }

    }
}

@Composable @Preview
fun PreviewPlaylistItem() {
    PlaylistItem(playlistInfo = Playlist.mock()
    , enableSwipeToRemove = true) { _ ->

    }
}