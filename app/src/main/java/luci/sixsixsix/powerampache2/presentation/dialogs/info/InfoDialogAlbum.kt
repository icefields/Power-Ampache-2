/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.dialogs.info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.capitalizeWords
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.totalTime
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginAlbumData
import luci.sixsixsix.powerampache2.domain.plugin.info.totalTime
import luci.sixsixsix.powerampache2.presentation.common.MusicChips
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogText
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogTextHorizontal
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogTitleText

@Composable
fun InfoDialogAlbum(album: Album, albumPlugin: PluginAlbumData?, onDismissRequest: () -> Unit) {
    InfoDialogBase(onDismissRequest) {
        Card(
            //border = BorderStroke((0.5).dp, MaterialTheme.colorScheme.tertiary),
            modifier = Modifier
                .background(Color.Transparent)
                .clickable { onDismissRequest() },
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f/1f),
                model = albumPlugin?.imageUrl?.ifBlank { album.artUrl } ?: album.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.placeholder_album),
                contentDescription = album.name,
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.separator_height_dialogInfoImage)))

        val tags: HashSet<String> = hashSetOf<String>().apply {
            album.genre.forEach {
                add(it.name.capitalizeWords())
            }
            albumPlugin?.tags?.forEach { t ->
                add(t.capitalizeWords())
            }
        }
        val name = albumPlugin?.albumName?.ifBlank { album.name } ?: album.name
        val artist = albumPlugin?.artistName?.ifBlank { album.artist.name } ?: album.artist.name
        val duration = albumPlugin?.duration?.let { dur ->
            if (dur > 0) albumPlugin.totalTime() else album.totalTime()
        } ?: album.totalTime()
        val description = albumPlugin?.description?.ifBlank { albumPlugin.shortDescription } ?: albumPlugin?.shortDescription ?: ""
        val playCount = albumPlugin?.playCount ?: 0
        val listeners = albumPlugin?.listeners ?: 0
        val rank = albumPlugin?.rank ?: 0
        val year = albumPlugin?.year?.ifBlank { album.year.toString() } ?: album.year.toString()

        InfoDialogTextHorizontal("$artist - $name", "")
        Divider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))
        if (duration.isNotBlank()) InfoDialogTextHorizontal("Duration", duration)
        if (year.isNotBlank() && year != "0" ) InfoDialogTextHorizontal("Year", year)
        if (album.diskCount > 0) InfoDialogTextHorizontal("Disc Count", album.diskCount.toString())
        if (playCount > 0) InfoDialogTextHorizontal("Play Count", playCount.toString())
        if (listeners > 0) InfoDialogTextHorizontal("Listeners", listeners.toString())
        if (rank > 0) InfoDialogTextHorizontal("Rank", rank.toString())
        if (description.isNotBlank()) {
            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))
            InfoDialogText(null, description)
        }
        if (album.artists.isNotEmpty()) {
            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))

            InfoDialogTitleText(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)), "Artists")
            MusicChips(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_chip_vertical), horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)),
                attributes = album.artists.map { it.name }
            ) { /* onClick */ }
            //InfoDialogText("Artists", album.artists.joinToString(", ") { it.name })
        }
        if (tags.isNotEmpty()) {
            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))
            InfoDialogTitleText(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)), "Tags")
            MusicChips(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_chip_vertical), horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)),
                attributes = tags.toList()
            ) { /* onClick */ }
            //InfoDialogText("Tags", tags.joinToString("\n"))
        }
    }
}
