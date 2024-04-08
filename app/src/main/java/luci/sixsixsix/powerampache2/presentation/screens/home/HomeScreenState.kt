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
package luci.sixsixsix.powerampache2.presentation.screens.home

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Playlist

data class HomeScreenState (
    val playlists: List<Playlist> = emptyList(),
    val recentAlbums: List<Album> = emptyList(),
    val newestAlbums: List<Album> = emptyList(),
    val highestAlbums: List<Album> = emptyList(),
    val frequentAlbums: List<AmpacheModel> = emptyList(),
    val flaggedAlbums: List<Album> = emptyList(),
    val randomAlbums: List<Album> = emptyList(),
    val isPlaylistsLoading: Boolean = false,
    val isRecentAlbumsLoading: Boolean = false,
    val isNewestAlbumsLoading: Boolean = false,
    val isHighestAlbumsLoading: Boolean = false,
    val isFrequentAlbumsLoading: Boolean = false,
    val isFlaggedAlbumsLoading: Boolean = false,
    val isRandomAlbumsLoading: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)
