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

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.totalTime


@Composable
fun InfoTextSectionSongItem(
    modifier: Modifier,
    song: Song,
    subtitleString: SubtitleString,
    songInfoThirdRow: SongInfoThirdRow = SongInfoThirdRow.AlbumTitle
) {
    val songInfoThirdRowText = when(songInfoThirdRow) {
        SongInfoThirdRow.AlbumTitle -> song.album.name
        SongInfoThirdRow.Time -> song.totalTime()
    }

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.basicMarquee(),
            text = song.title,
            fontWeight = FontWeight.Normal,
            fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_title),
            maxLines = 1,
        )
        Spacer(modifier = Modifier
            .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

        when(subtitleString) {
            SubtitleString.NOTHING -> {

            }
            SubtitleString.ARTIST -> Text(
                modifier = Modifier.basicMarquee(),
                text = song.artist.name,
                fontWeight = FontWeight.Normal,
                fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_artist),
                maxLines = 1,
                textAlign = TextAlign.Start
            )
            SubtitleString.ALBUM -> Text(
                modifier = Modifier.basicMarquee(),
                text = song.album.name,
                fontWeight = FontWeight.Normal,
                fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_artist),
                maxLines = 1,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier
            .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
        Text(
            modifier = Modifier.basicMarquee(),
            text = songInfoThirdRowText,
            fontWeight = FontWeight.Light,
            fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_album),
            maxLines = 1,
            textAlign = TextAlign.Start
        )
    }
}