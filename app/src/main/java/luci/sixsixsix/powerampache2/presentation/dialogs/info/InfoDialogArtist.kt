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
import androidx.compose.material3.HorizontalDivider
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
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginArtistData
import luci.sixsixsix.powerampache2.presentation.common.MusicChips
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogText
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogTextHorizontal
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogTitleText

@Composable
fun InfoDialogArtist(artist: Artist, artistPlugin: PluginArtistData?, onDismissRequest: () -> Unit) {
    InfoDialogBase(onDismissRequest) {
        Card(
            //border = BorderStroke((0.5).dp, MaterialTheme.colorScheme.background),
            modifier = Modifier.background(Color.Transparent)
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
                model = artistPlugin?.imageUrl?.ifBlank { artist.artUrl } ?: artist.artUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_album),
                error = painterResource(id = R.drawable.placeholder_album),
                contentDescription = artist.name,
            )
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.separator_height_dialogInfoImage)))

        val tags: HashSet<String> = hashSetOf<String>().apply {
            artist.genre.forEach {
                add(it.name.capitalizeWords())
            }
            artistPlugin?.tags?.forEach { t ->
                add(t.capitalizeWords())
            }
        }
        val name = artistPlugin?.artistName?.ifBlank { artist.name } ?: artist.name
        val description = artistPlugin?.description?.ifBlank { artistPlugin.shortDescription.ifBlank { artist.summary } } ?: artist.summary ?: ""
        val playCount = artistPlugin?.playCount ?: 0
        val songCount = artist.songCount
        val listeners = artistPlugin?.listeners ?: 0
        val yearFormed = artist.yearFormed

        InfoDialogTextHorizontal(name, "")
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))
        if (artist.albumCount > 0)
            InfoDialogTextHorizontal("Album Count", artist.albumCount.toString())
        if (playCount > 0)
            InfoDialogTextHorizontal("Play Count", playCount.toString())
        if (listeners > 0)
            InfoDialogTextHorizontal("Listeners", listeners.toString())
        if (songCount > 0)
            InfoDialogTextHorizontal("Song Count", songCount.toString())
        if (yearFormed > 0)
            InfoDialogTextHorizontal("Year Formed", yearFormed.toString())
        if (artist.placeFormed?.isNotBlank() == true && artist.placeFormed != "null") {
            InfoDialogTextHorizontal("Place Formed", artist.placeFormed ?: "")
        }
        if (artistPlugin?.language?.isNotBlank() == true) {
            InfoDialogTextHorizontal("Language", artistPlugin.language)
        }
        artistPlugin?.onTour?.let { onTour ->
            if (onTour.isNotBlank() == true && onTour == "1") {
                InfoDialogTextHorizontal("Currently On Tour", "Yes")
            }
        }

        if (description.isNotBlank()) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))
            InfoDialogText(null, description)
        }
        if (tags.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))
            //InfoDialogText("Tags", tags.joinToString(", "))
            InfoDialogTitleText(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)), "Tags")
            MusicChips(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_chip_vertical), horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)),
                attributes = tags.toList()
            ) { /* onClick */ }
        }

        if (artistPlugin?.similar?.isNotEmpty() == true) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_divider_vertical)))
            //InfoDialogText("Similar", artistPlugin.similar.joinToString("\n") { it.name })
            InfoDialogTitleText(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)), "Similar")
            MusicChips(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.dialogInfo_padding_chip_vertical), horizontal = dimensionResource(R.dimen.dialogInfo_padding_text_horizontal)),
                attributes = artistPlugin.similar.map { it.name }.toList()
            ) { /* onClick */ }
        }
    }
}
