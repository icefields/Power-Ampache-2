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
package luci.sixsixsix.powerampache2.presentation.screens_detail.artist_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginArtistData
import luci.sixsixsix.powerampache2.presentation.common.LikeButton
import luci.sixsixsix.powerampache2.presentation.common.MusicAttributeChips
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.AttributeText
import org.acra.log.info

@Composable
fun ArtistInfoSection(
    modifier: Modifier,
    artist: Artist,
    infoPluginArtist: PluginArtistData?,
    summaryOpen: MutableState<Boolean>,
    isLikeLoading: Boolean,
    isBuffering: Boolean,
    isPlayLoading: Boolean,
    isPlaylistEditLoading: Boolean,
    isGlobalShuffleOn: Boolean,
    isDownloading: Boolean,
    eventListener: (albumInfoViewEvents: ArtistInfoEvent) -> Unit
) {
    Column(modifier = modifier) {
        MusicAttributeChips(
            attributes = artist.genre,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            // TODO navigate to genre
        }

        Spacer(modifier = Modifier.height(6.dp))
        if (artist.yearFormed > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.albumDetailScreen_infoSection_year),
                name = "${artist.yearFormed}"
            )
        }
        Spacer(modifier = Modifier.height(2.dp))


        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (artist.songCount > 0) {
                AttributeText(
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                    title = stringResource(id = R.string.albumDetailScreen_infoSection_songs),
                    name = "${artist.songCount}"
                )
            }

            LikeButton(
                modifier = Modifier.size(32.dp),
                isLikeLoading = isLikeLoading, isFavourite = artist.flag == 1) {
                eventListener(ArtistInfoEvent.FAVOURITE_ARTIST)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        val isSummaryAvailable = !artist.summary.isNullOrBlank()
        val isInfoPluginAvailable = infoPluginArtist != null && infoPluginArtist.description.isNotBlank()
        if (isSummaryAvailable || isInfoPluginAvailable) {
            val artistSummary = if (isSummaryAvailable)
                artist.summary
            else
                infoPluginArtist?.description

            Text( // name
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .clickable {
                        summaryOpen.value = !summaryOpen.value
                    },
                text = artistSummary ?: "",
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                maxLines = if (summaryOpen.value) { 300 } else { 5 },
                lineHeight = 17.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        ArtistInfoButtonsRow(
            isPlaylistEditLoading = isPlaylistEditLoading,
            isPlayLoading = isPlayLoading,
            isBuffering = isBuffering,
            isGlobalShuffleOn = isGlobalShuffleOn,
            isDownloading = isDownloading,
            modifier = Modifier.fillMaxWidth(),
            eventListener
        )
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun ArtistInfoSectionPreview() {
    ArtistInfoSection(
        Modifier,
        Artist.mockArtist(),
        eventListener = {},
        isLikeLoading = false,
        isBuffering = false,
        isPlayLoading = false,
        isPlaylistEditLoading = false,
        isGlobalShuffleOn = true,
        isDownloading = true,
        infoPluginArtist = null,
        summaryOpen = remember { mutableStateOf(true) }
    )
}
