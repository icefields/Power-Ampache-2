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
package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import retrofit2.http.Query

interface PlaylistsRepository {

    val playlistsLiveData: LiveData<List<Playlist>>

    suspend fun getPlaylists(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Playlist>>>
    suspend fun likePlaylist(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun addSongToPlaylist(playlistId: String, songId: String): Flow<Resource<Any>>
    suspend fun addSongsToPlaylist(playlist: Playlist, songsToAdd: List<Song>): Flow<Resource<Any>>
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Flow<Resource<Any>>
    suspend fun createNewPlaylist(name: String, playlistType: MainNetwork.PlaylistType): Flow<Resource<Playlist>>
    suspend fun createNewPlaylistAddSongs(name: String, playlistType: MainNetwork.PlaylistType, songsToAdd: List<Song>): Flow<Resource<Playlist>>
    suspend fun deletePlaylist(id: String): Flow<Resource<Any>>
    suspend fun editPlaylist(
        playlistId: String,
        playlistName: String? = null,
        items: List<Song> = listOf(),
        owner: String? = null,
        tracks: String? = null,
        playlistType: MainNetwork.PlaylistType = MainNetwork.PlaylistType.private
    ): Flow<Resource<Any>>
    suspend fun likeAlbum(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun getPlaylistShareLink(playlistId: String): Flow<Resource<String>>
}
