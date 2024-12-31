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
package luci.sixsixsix.powerampache2.presentation.common.songitem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.common.SongDropDownMenu
import luci.sixsixsix.powerampache2.presentation.common.SwipeToDismissItem

enum class SongInfoThirdRow { AlbumTitle, Time }

enum class SubtitleString { NOTHING, ARTIST, ALBUM }

@Composable
fun SongItem(
    song: Song,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isLandscape: Boolean = false,
    isSongDownloaded: Boolean = false,
    showDownloadedSongMarker: Boolean = true,
    subtitleString: SubtitleString = SubtitleString.ARTIST,
    songInfoThirdRow: SongInfoThirdRow = SongInfoThirdRow.AlbumTitle,
    enableSwipeToRemove: Boolean = false,
    isEditSongSelected: Boolean = false,
    onRemove: (Song) -> Unit = {},
    onRightToLeftSwipe: (Song) -> Unit = {},
    onEditMoveUp: (Song) -> Unit = { _ -> },
    onEditMoveDown: (Song) -> Unit = { _ -> },
    onEditSelected: (Boolean, Song) -> Unit = { _, _ -> }
) {
    SwipeToDismissItem(
        item = song,
        foregroundView = {
            if (isEditMode.not()) {
                SongItemMain(
                    song = song,
                    songItemEventListener = songItemEventListener,
                    modifier = modifier,
                    isLandscape  = isLandscape,
                    isSongDownloaded  = isSongDownloaded,
                    showDownloadedSongMarker = showDownloadedSongMarker,
                    subtitleString  = subtitleString,
                    songInfoThirdRow = songInfoThirdRow
                )
            } else {
                SongItemForegroundEdit(
                    song = song,
                    modifier = modifier,
                    isSongDownloaded  = isSongDownloaded,
                    showDownloadedSongMarker = showDownloadedSongMarker,
                    subtitleString  = subtitleString,
                    songInfoThirdRow = songInfoThirdRow,
                    checked = isEditSongSelected,
                    onCheckedChange = onEditSelected,
                    onMoveUp = { onEditMoveUp(song) },
                    onMoveDown = { onEditMoveDown(song) }
                )
            }
        },
        enableSwipeToRemove = enableSwipeToRemove,
        onRemove = onRemove,
        onRightToLeftSwipe = onRightToLeftSwipe
    )
}


@Composable
fun SongItemMain(
    song: Song,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    isSongDownloaded: Boolean,
    showDownloadedSongMarker: Boolean,
    subtitleString: SubtitleString = SubtitleString.ARTIST,
    songInfoThirdRow: SongInfoThirdRow = SongInfoThirdRow.AlbumTitle
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
        // show Image only in portrait mode
        if(isLandscape.not()) {
            SongAlbumCover(
                modifier = Modifier
                    .weight(if (subtitleString == SubtitleString.NOTHING) 0.7f else 1f)
                    .background(Color.Transparent)
                    .align(Alignment.CenterVertically),
                song = song,
                isSongDownloaded = isSongDownloaded,
                showDownloadedSongMarker = showDownloadedSongMarker
            )
        }

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

        InfoTextSectionSongItem(
            modifier = Modifier
                .weight(5f)
                .padding(
                    horizontal = dimensionResource(R.dimen.songItem_infoTextSection_paddingHorizontal),
                    vertical = dimensionResource(R.dimen.songItem_infoTextSection_paddingVertical))
                .align(Alignment.CenterVertically),
            song = song,
            subtitleString = subtitleString,
            songInfoThirdRow = songInfoThirdRow
        )

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
        }
    )
}

@Composable
private fun SongAlbumCover(
    modifier: Modifier,
    song: Song,
    isSongDownloaded: Boolean,
    showDownloadedSongMarker: Boolean
) {
    Card(
        border = BorderStroke(
            width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
            color = MaterialTheme.colorScheme.background
        ),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.songItem_card_cornerRadius))
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = song.imageUrl,
                contentScale = ContentScale.FillWidth,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.placeholder_album),
                contentDescription = song.title,
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
                        Icon(imageVector = Icons.Outlined.DownloadDone,
                            contentDescription = "download done")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SongItemPreview() {
    SongItem(
        song = Song.mockSong,
        songItemEventListener = {},
        subtitleString = SubtitleString.NOTHING,
        isSongDownloaded = true
    )
}
