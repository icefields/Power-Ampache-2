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
package luci.sixsixsix.powerampache2.presentation.screens_detail.playlist_detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.domain.models.FlaggedPlaylist
import luci.sixsixsix.powerampache2.domain.models.FrequentPlaylist
import luci.sixsixsix.powerampache2.domain.models.HighestPlaylist
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.RecentPlaylist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.SortMode
import luci.sixsixsix.powerampache2.domain.models.defaultPlaylistSort
import luci.sixsixsix.powerampache2.domain.models.isSmartPlaylist
import luci.sixsixsix.powerampache2.presentation.common.SongWrapper

@Parcelize
data class PlaylistDetailState (
    val playlist: Playlist = Playlist("", ""),
    val songs: List<SongWrapper> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPlaylistRemoveLoading: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false,
    val sortMode: SortMode = defaultPlaylistSort,
    val isUserOwner: Boolean = false,
    val isGlobalShuffleOn: Boolean = LocalSettings.SETTINGS_DEFAULTS_GLOBAL_SHUFFLE
): Parcelable {
    fun getSongList(): List<Song> = songs.map { it.song }
    fun isGeneratedPlaylist() = when(playlist) {
        is HighestPlaylist -> true
        is RecentPlaylist -> true
        is FlaggedPlaylist -> true
        is FrequentPlaylist -> true
        else -> playlist.isSmartPlaylist()
    }
}
