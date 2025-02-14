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
package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.ArtistId
import luci.sixsixsix.powerampache2.domain.models.totalTime
import luci.sixsixsix.powerampache2.presentation.common.LikeButton
import luci.sixsixsix.powerampache2.presentation.common.MusicAttributeChips

enum class AlbumInfoViewEvents {
    PLAY_ALBUM,
    SHARE_ALBUM,
    DOWNLOAD_ALBUM,
    STOP_DOWNLOAD_ALBUM,
    SHUFFLE_PLAY_ALBUM,
    ADD_ALBUM_TO_PLAYLIST,
    FAVOURITE_ALBUM
}

@Composable
fun AlbumInfoSection(
    album: Album,
    isPlayingAlbum: Boolean,
    isBuffering: Boolean,
    isPlayLoading: Boolean,
    isLikeLoading: Boolean,
    isDownloading: Boolean,
    isAlbumDownloaded: Boolean,
    isPlaylistEditLoading: Boolean,
    isGlobalShuffleOn: Boolean,
    modifier: Modifier,
    eventListener: (albumInfoViewEvents: AlbumInfoViewEvents) -> Unit,
    artistClickListener: (ArtistId) -> Unit
) {
    Column(modifier = modifier) {
        MusicAttributeChips(
            attributes = album.genre,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            // TODO go to genre page
        }

        Spacer(modifier = Modifier.height(4.dp))
        MusicAttributeChips(
            attributes = album.artists,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            artistClickListener(it.id)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                if (album.year > 0) {
                    AttributeText(
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                        title = stringResource(id = R.string.albumDetailScreen_infoSection_year),
                        name = "${album.year}"
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                if (album.songCount > 0) {
                    AttributeText(
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                        title = stringResource(id = R.string.albumDetailScreen_infoSection_songs),
                        name = "${album.songCount}"
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                if (album.time > 0) {
                    AttributeText(
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_attribute_paddingHorizontal)),
                        title = stringResource(id = R.string.albumDetailScreen_infoSection_time),
                        name = album.totalTime()
                    )
                }
            }

            LikeButton(
                modifier = Modifier.align(Alignment.BottomEnd).size(32.dp),
                isLikeLoading = isLikeLoading, isFavourite = album.flag == 1) {
                eventListener(AlbumInfoViewEvents.FAVOURITE_ALBUM)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        AlbumInfoButtonsRow(
            modifier = Modifier.fillMaxWidth(),
            album = album,
            isPlayLoading = isPlayLoading,
            isAlbumDownloaded = isAlbumDownloaded,
            isPlayingAlbum = isPlayingAlbum,
            isPlaylistEditLoading = isPlaylistEditLoading,
            isDownloading = isDownloading,
            eventListener = eventListener,
            isBuffering = isBuffering,
            isGlobalShuffleOn = isGlobalShuffleOn
        )
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun AlbumInfoSectionPreview() {
    AlbumInfoSection(
        modifier = Modifier,
        album = Album.mock(),
        isPlayingAlbum = false,
        isLikeLoading = false,
        isPlaylistEditLoading = false,
        isDownloading = false,
        eventListener = { },
        isBuffering = false,
        isGlobalShuffleOn = true,
        isAlbumDownloaded = true,
        isPlayLoading = false,
        artistClickListener = { }
    )
}
