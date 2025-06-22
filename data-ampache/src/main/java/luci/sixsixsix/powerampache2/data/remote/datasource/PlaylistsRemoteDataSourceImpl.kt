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
package luci.sixsixsix.powerampache2.data.remote.datasource

import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.di.RemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.NullDataException
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

@RemoteDataSource
class PlaylistsRemoteDataSourceImpl @Inject constructor(
    private val api: MainNetwork
) : PlaylistsRemoteDataSource {

    @Throws(MusicException::class, NullDataException::class)
    override suspend fun getSongsFromPlaylist(
        authToken: String,
        playlistId: String,
        limit: Int,
        offset: Int
    ): List<Song> = api.getSongsFromPlaylist(authToken,
        albumId = playlistId,
        limit = limit,
        offset = offset
    ).let { songsResponse ->
        songsResponse.error?.let { error -> throw MusicException(error.toError()) }
        songsResponse.songs?.let { songsDto ->
            songsDto.map { songDto -> songDto.toSong() }
        } ?: throw NullDataException("getSongsFromPlaylist")
    }
}