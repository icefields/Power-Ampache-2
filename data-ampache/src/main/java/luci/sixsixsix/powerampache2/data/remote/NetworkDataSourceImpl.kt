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
package luci.sixsixsix.powerampache2.data.remote

import luci.sixsixsix.powerampache2.domain.datasource.NetworkDataSource
import java.io.InputStream
import java.net.HttpURLConnection.HTTP_OK
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TODO: this is the network data source, add all the api calls.
 */
@Singleton
class NetworkDataSourceImpl @Inject constructor(private val api: MainNetwork): NetworkDataSource {
    override suspend fun downloadSong(songId: String, authKey: String): InputStream {
        val response = api.downloadSong(authKey = authKey, songId = songId)
        if (response.code() == HTTP_OK) {
                // save file to disk and register in database
            return response.body()?.byteStream() ?: throw Exception("byteStream null}")
        } else {
            throw Exception("Cannot download received code ${response.code()}")
        }
    }
}
