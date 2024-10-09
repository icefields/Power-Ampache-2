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
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song

interface SongsRepository {
    val offlineSongsLiveData: LiveData<List<Song>>

    suspend fun getSongs(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Song>>>
    suspend fun getSongFromId(songId: String): Song?
    suspend fun getSongsFromAlbum(albumId: String, fetchRemote: Boolean = true): Flow<Resource<List<Song>>>
    suspend fun getRecentSongs(fetchRemote: Boolean = true): Flow<Resource<List<Song>>>
    suspend fun getNewestSongs(): Flow<Resource<List<Song>>>
    suspend fun getHighestSongs(): Flow<Resource<List<Song>>>
    suspend fun getFrequentSongs(): Flow<Resource<List<Song>>>
    suspend fun getFlaggedSongs(): Flow<Resource<List<Song>>>
    suspend fun getRandomSongs(): Flow<Resource<List<Song>>>
    suspend fun getSongsForQuickPlay(): Flow<Resource<List<Song>>>
    suspend fun getSongUri(song: Song): String
    suspend fun downloadSong(song: Song): Flow<Resource<Any>>
    suspend fun downloadSongs(songs: List<Song>)
    suspend fun getDownloadedSongById(songId: String): Song?
    suspend fun deleteDownloadedSong(song: Song): Flow<Resource<Any>>
    suspend fun isSongAvailableOffline(song: Song): Boolean
    suspend fun getSongShareLink(song: Song): Flow<Resource<String>>
    suspend fun rateSong(songId: String, rate: Int): Flow<Resource<Any>>
    suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun scrobble(song: Song): Flow<Resource<Any>>
    suspend fun addToHistory(song: Song): Boolean
    suspend fun getSongsByGenre(genreId: Genre, fetchRemote: Boolean = true, offset: Int = 0): Flow<Resource<List<Song>>>
}
