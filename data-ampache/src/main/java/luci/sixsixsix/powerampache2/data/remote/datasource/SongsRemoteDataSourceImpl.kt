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
import luci.sixsixsix.powerampache2.di.RemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsRemoteDataSource
import java.io.InputStream
import java.net.HttpURLConnection.HTTP_OK
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RemoteDataSource
class SongsRemoteDataSourceImpl @Inject constructor(private val api: MainNetwork): SongsRemoteDataSource {
    override suspend fun downloadSong(songId: String, authKey: String): InputStream {
        val response = api.downloadSong(authKey = authKey, songId = songId)
        if (response.code() == HTTP_OK) {
            // save file to disk and register in database
            return response.body()?.byteStream() ?: throw Exception("downloadSong, byteStream null}")
        } else {
            throw Exception("Cannot download song $songId, received code ${response.code()}")
        }
    }

    override suspend fun downloadArt(songId: String, authKey: String): InputStream {
        val response = api.getArt(authKey = authKey, songId = songId)
        if (response.code() == HTTP_OK) {
            // save file to disk and register in database
            return response.body()?.byteStream() ?: throw Exception("downloadArt, byteStream null}")
        } else {
            throw Exception("Cannot download art, received code ${response.code()}")
        }
    }
}
