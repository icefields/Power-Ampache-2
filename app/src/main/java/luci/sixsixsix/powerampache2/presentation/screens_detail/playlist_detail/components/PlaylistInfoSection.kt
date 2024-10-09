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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.ArtistId
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistType
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components.AttributeText
import luci.sixsixsix.powerampache2.presentation.common.MusicAttributeChips

enum class PlaylistInfoViewEvents {
    PLAY_PLAYLIST,
    SHARE_PLAYLIST,
    DOWNLOAD_PLAYLIST,
    STOP_DOWNLOAD_PLAYLIST,
    SHUFFLE_PLAY_PLAYLIST,
    LIKE_PLAYLIST,
    ADD_PLAYLIST_TO_PLAYLIST
}

@Composable
fun PlaylistInfoSection(
    modifier: Modifier,
    playlist: Playlist,
    isLoading: Boolean,
    isPlayingPlaylist: Boolean,
    isBuffering: Boolean,
    isDownloading: Boolean,
    isGlobalShuffleOn: Boolean,
    isPlaylistEditLoading: Boolean,
    isLikeAvailable: Boolean,
    isLikeLoading: Boolean,
    isPlayLoading: Boolean,
    enabled: Boolean,
    songs: List<Song>,
    eventListener: (playlistInfoViewEvents: PlaylistInfoViewEvents) -> Unit,
    artistClickListener: (ArtistId) -> Unit
) {
    val iconTint = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier) {
        HashSet<MusicAttribute>().apply {
            songs.forEach { song ->
                add(song.artist)
            }
            if (isNotEmpty()) {
                MusicAttributeChips(
                    attributes = toList(),
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    artistClickListener(it.id)
                }
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
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    // TODO navigate to genre screen
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row {
            playlist.type?.let { type ->
                Icon(
                    imageVector = when(type) {
                        PlaylistType.private ->
                            Icons.Outlined.Lock
                        PlaylistType.public ->
                            Icons.Outlined.Public
                    },
                    contentDescription = "playlist private or public",
                    tint = iconTint
                )
            }

            if (songs.isNotEmpty()) {
                AttributeText(
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                    title = stringResource(id = R.string.albumDetailScreen_infoSection_songs),
                    name = "${songs.size}"
                )
            }

        }
        if (playlist.averageRating > 0) {
            AttributeText(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                title = stringResource(id = R.string.playlistDetailScreen_infoSection_avgRating),
                name = "${playlist.averageRating}"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        PlaylistInfoButtonsRow(
            modifier = Modifier.fillMaxWidth(),
            isPlayingPlaylist = isPlayingPlaylist,
            playlist = playlist,
            iconTint = iconTint,
            eventListener = eventListener,
            isDownloading = isDownloading,
            isPlaylistEditLoading = isPlaylistEditLoading,
            isGlobalShuffleOn = isGlobalShuffleOn,
            isLikeAvailable = isLikeAvailable,
            isLikeLoading = isLikeLoading,
            isPlayLoading = isPlayLoading,
            isBuffering = isBuffering,
            enabled = enabled,
            isLoading = isLoading,
            isLiked = playlist.flag == 1
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
        isGlobalShuffleOn = true,
        songs = listOf(Song.mockSong, Song.mockSong, Song.mockSong, Song.mockSong, Song.mockSong),
        isPlaylistEditLoading = true,
        isLikeLoading = false,
        isLikeAvailable = true,
        enabled = true,
        isPlayLoading = false,
        isBuffering = false,
        isLoading = true,
        eventListener = { },
        artistClickListener = { }
    )
}
