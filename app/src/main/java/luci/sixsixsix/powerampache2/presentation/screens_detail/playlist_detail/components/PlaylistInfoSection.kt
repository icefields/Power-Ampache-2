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
package luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.AttributeText
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.MusicAttributeChips

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
    isPlayingPlaylist: Boolean,
    isDownloading: Boolean,
    songs: List<Song>,
    eventListener: (playlistInfoViewEvents: PlaylistInfoViewEvents) -> Unit
) {
    Column(modifier = modifier) {

        HashSet<MusicAttribute>().apply {
            songs.forEach { song ->
                add(song.artist)
            }
            if (isNotEmpty()) {
                MusicAttributeChips(
                    attributes = toList(),
                    containerColor = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        HashSet<MusicAttribute>().apply {
            songs.forEach { song ->
                addAll(song.genre)
            }
            if (isNotEmpty()) {
                MusicAttributeChips(
                    attributes = toList(),
                    containerColor = MaterialTheme.colorScheme.background
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }



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
        PlaylistInfoButtonsRow(
            modifier = Modifier.fillMaxWidth(),
            isPlayingPlaylist = isPlayingPlaylist,
            playlist = playlist,
            eventListener = eventListener,
            isDownloading = isDownloading,
        )
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun PlaylistInfoSectionPreview() {
    PlaylistInfoSection(
        Modifier,
        Playlist.mock(),
        isPlayingPlaylist = true,
        isDownloading = false,
        listOf(Song.mockSong),
        eventListener = {}
    )
}
