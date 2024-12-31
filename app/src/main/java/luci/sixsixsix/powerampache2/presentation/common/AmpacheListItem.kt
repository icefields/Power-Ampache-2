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
package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.genresString
import luci.sixsixsix.powerampache2.domain.models.isOwnerAdmin
import luci.sixsixsix.powerampache2.domain.models.isOwnerSystem
import luci.sixsixsix.powerampache2.domain.models.isSmartPlaylist
import luci.sixsixsix.powerampache2.presentation.common.songitem.SongItemEvent

data class InfoViewItemText(
    val firstRowText: String,
    val secondRowText: String?,
    val thirdRowText: String,
)

@Composable
fun <T: AmpacheModel> AmpacheListItem(
    item: T,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    isSongDownloaded: Boolean = false,
    showDownloadedSongMarker: Boolean = false,
    enableSwipeToRemove: Boolean = false,
    onRemove: (AmpacheModel) -> Unit = {},
    onRightToLeftSwipe: (AmpacheModel) -> Unit = {}
) {
    var imageUrl: String? = null
    lateinit var infoViewItemText: InfoViewItemText
    var isFavourite = false
    var rating: Int = ERROR_INT
    var showAmpacheBadge = false
    var showPublicBadge = false

    when(item) {
        is Song -> {
            imageUrl = item.imageUrl
            infoViewItemText = InfoViewItemText(
                item.title,
                item.artist.name,
                item.album.name
            )
            isFavourite = item.flag == 1
            rating = item.rating.toInt()
        }
        is Album -> {
            imageUrl = item.artUrl
            infoViewItemText = InfoViewItemText(item.name, item.artist.name, stringResource(R.string.item_title_album))
            isFavourite = item.flag == 1
            rating = item.rating
        }
        is Artist -> {
            imageUrl = item.artUrl
            infoViewItemText = InfoViewItemText(item.name, item.genresString, stringResource(R.string.item_title_artist))
            isFavourite = item.flag == 1
        }
        is Playlist -> {
            imageUrl = item.artUrl

            val ownerText = item.owner?.let { owner ->
                if (!item.isOwnerSystem() && !item.isOwnerAdmin()) {
                    owner
                } else stringResource(R.string.item_title_playlist)
            } ?: stringResource(R.string.item_title_playlist)

            val items = item.items?.let {
                if (it > 0) stringResource(id = R.string.playlistItem_songCount, it) else " "
            } ?: run { " " }
            infoViewItemText = InfoViewItemText(item.name, items, ownerText)
            isFavourite = item.flag == 1
            rating = item.rating
            showAmpacheBadge = item.isSmartPlaylist()
            showPublicBadge = item.type == PlaylistType.public
        }
        else ->
            InfoViewItemText(ERROR_STRING, null, ERROR_STRING)
    }
    SwipeToDismissItem(
        item = item,
        foregroundView = {
            AmpacheListItemMain(
                modifier = modifier,
                item = item,
                imageUrl = imageUrl,
                textInfo = infoViewItemText,
                songItemEventListener = songItemEventListener,
                isSongDownloaded = isSongDownloaded,
                showDownloadedSongMarker = showDownloadedSongMarker,
                hideSongMenu = item !is Song,
                isFavourite = isFavourite,
                rating = rating,
                showAmpacheBadge = showAmpacheBadge,
                showPublicBadge = showPublicBadge
            )
        },
        enableSwipeToRemove = enableSwipeToRemove,
        onRemove = onRemove,
        onRightToLeftSwipe = onRightToLeftSwipe
    )
}

@Composable
fun <T: AmpacheModel> AmpacheListItemMain(
    item: T,
    imageUrl: String?,
    textInfo: InfoViewItemText,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    isSongDownloaded: Boolean,
    hideSongMenu: Boolean,
    showDownloadedSongMarker: Boolean,
    isFavourite: Boolean = false,
    rating: Int = ERROR_INT,
    showAmpacheBadge: Boolean = false,
    showPublicBadge: Boolean = false
) {
    var isContextMenuVisible by rememberSaveable { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
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
                .weight(if (textInfo.secondRowText == null) 0.7f else 1f)
                .background(Color.Transparent)
                .align(Alignment.CenterVertically),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(1.dp),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.songItem_card_cornerRadius))
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = imageUrl,
                    contentScale = ContentScale.FillWidth,
                    placeholder = painterResource(id = R.drawable.placeholder_album),
                    error = painterResource(id = R.drawable.placeholder_album),
                    contentDescription = textInfo.firstRowText,
                )
                if(isSongDownloaded && showDownloadedSongMarker) {
                    Card(modifier = Modifier.size(20.dp)) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(2.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DownloadDone,
                                contentDescription = "download done"
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

        AmpacheInfoTextSection(
            isFavourite = isFavourite,
            showAmpacheBadge = showAmpacheBadge,
            showPublicBadge = showPublicBadge,
            rating = rating,
            modifier = Modifier
                .weight(5f)
                .padding(
                    horizontal = dimensionResource(R.dimen.songItem_infoTextSection_paddingHorizontal),
                    vertical = dimensionResource(R.dimen.songItem_infoTextSection_paddingVertical))
                .align(Alignment.CenterVertically),
            textInfo = textInfo,
        )

        if (!hideSongMenu) {
            Image(
                alignment = Alignment.Center,
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = stringResource(id = R.string.menu_content_description),
                modifier = Modifier
                    .background(Color.Transparent)
                    .weight(0.5f)
                    .pointerInput(true) {
                        detectTapGestures(
                            onPress = { offset ->
                                pressOffset = DpOffset(offset.x.toDp(), offset.y.toDp())
                                isContextMenuVisible = true
                            })
                    },
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )
        }

        Spacer(modifier = Modifier
            .width(15.dp))
    }

    Spacer(modifier = Modifier
        .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer) * 2))

    SongDropDownMenu(
        isContextMenuVisible = isContextMenuVisible,
        pressOffset = pressOffset,
        isSongDownloaded = isSongDownloaded,
        songItemEventListener = {
            isContextMenuVisible = false
            songItemEventListener(it)
        },
        onDismissRequest = {
            isContextMenuVisible = false
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AmpacheInfoTextSection(
    modifier: Modifier,
    textInfo: InfoViewItemText,
    isFavourite: Boolean = false,
    rating: Int = ERROR_INT,
    showAmpacheBadge: Boolean = false,
    showPublicBadge: Boolean = false
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.basicMarquee(),
            text = textInfo.firstRowText,
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
                textInfo.secondRowText?.let { secondRowText ->
                    Text(
                        modifier = Modifier.basicMarquee(),
                        text = secondRowText,
                        fontWeight = FontWeight.Light,
                        fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_artist),
                        maxLines = 1,
                        textAlign = TextAlign.Start
                    )
                }
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = textInfo.thirdRowText,
                    fontWeight = FontWeight.Light,
                    fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_album),
                    maxLines = 1,
                    textAlign = TextAlign.Start
                )
            }
            ListItemBadges(isFavourite, rating, showAmpacheBadge, showPublicBadge)
        }
    }
}

@Composable
private fun ListItemBadges(
    isFavourite: Boolean,
    rating: Int,
    showAmpacheBadge: Boolean,
    showPublicBadge: Boolean
) {
    if (rating > 0) {
        StarRatingIcon(rating)
        Spacer(modifier = Modifier.width(6.dp))
    }
    if (showAmpacheBadge) {
        Icon(
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_power_ampache_mono),
            contentDescription = "smart playlist")
    }
    if (showPublicBadge) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.playlistItem_icon_size)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            imageVector = Icons.Rounded.Public,
            contentDescription = "public playlist")
        Spacer(modifier = Modifier.width(4.dp))
    }
    if (isFavourite) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.playlistItem_icon_size)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            imageVector = Icons.Rounded.Favorite,
            contentDescription = "favorite playlist")
    }
}

@Preview
@Composable
fun AmpacheModelItemPreview() {
    AmpacheListItem(
        item = Song.mockSong,
        songItemEventListener = {},
    ) {

    }
}
