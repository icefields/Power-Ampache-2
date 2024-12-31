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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song

@Composable
fun SongItemForegroundEdit(
    song: Song,
    modifier: Modifier = Modifier,
    isSongDownloaded: Boolean,
    showDownloadedSongMarker: Boolean,
    subtitleString: SubtitleString = SubtitleString.ARTIST,
    songInfoThirdRow: SongInfoThirdRow = SongInfoThirdRow.AlbumTitle,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean, Song) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.songItem_row_paddingHorizontal),
                vertical = dimensionResource(id = R.dimen.songItem_row_paddingVertical)
            ),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Checkbox(
                modifier = Modifier,
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it, song)
                },
                enabled = true
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

            InfoTextSectionSongItem(
                modifier = Modifier
                    .weight(5f)
                    .padding(
                        horizontal = dimensionResource(R.dimen.songItem_infoTextSection_paddingHorizontal),
                        vertical = dimensionResource(R.dimen.songItem_infoTextSection_paddingVertical)
                    )
                    .align(Alignment.CenterVertically),
                song = song,
                subtitleString = subtitleString,
                songInfoThirdRow = songInfoThirdRow
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
//            if(isSongDownloaded && showDownloadedSongMarker) {
//                Card(modifier = Modifier.width(20.dp)) {
//                    Box(
//                        modifier = Modifier
//                            .wrapContentSize()
//                            .padding(2.dp)
//                            .background(Color.Transparent),
//                        contentAlignment = Alignment.BottomStart
//                    ) {
//                        Icon(imageVector = Icons.Outlined.DownloadDone,
//                            contentDescription = "download done")
//                    }
//                }
//            }

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

            Button(
                contentPadding = PaddingValues(all = 11.dp),
                onClick = onMoveUp
            ) {
                Icon(imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = "move up")
            }

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

            Button(
                contentPadding = PaddingValues(all = 11.dp),
                onClick = onMoveDown
            ) {
                Icon(imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = "move down")
            }
        }
    }
}

@Composable
@Preview
fun SongItemForegroundEditPreview() {
    SongItemForegroundEdit(
        song = Song.mockSong,
        isSongDownloaded = true,
        showDownloadedSongMarker = true,
        checked = true,
        onMoveDown = {},
        onMoveUp = {},
    onCheckedChange = { _,_ -> }
    )
}