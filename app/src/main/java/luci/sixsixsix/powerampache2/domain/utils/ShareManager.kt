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
package luci.sixsixsix.powerampache2.domain.utils

import android.content.Context
import android.content.Intent
import luci.sixsixsix.powerampache2.domain.models.Song
import java.net.URLDecoder

interface ShareManager {
    suspend fun shareSongDeepLink(context: Context, song: Song)
    suspend fun shareSongWeb(context: Context, song: Song)

     suspend fun fetchDeepLinkedSong(id: String, title: String, artist: String,
                                     songCallback: (song: Song) -> Unit,
                                     songsCallback: (songs: List<Song>) -> Unit,
                                     errorCallback: () -> Unit)

     companion object {
        fun parseDeepLinkIntent(
            intent: Intent,
            callback: (type: String, id: String, title: String, artist: String, webLink: String) -> Unit
        ) {
            val action: String? = intent.action
            intent.data?.let { dataUri ->
                var type = ""
                var id = ""
                var title = ""
                var album = ""
                var artist = ""
                var webLink = ""
                dataUri.pathSegments.forEachIndexed { i, value ->
                    // https url schemes has one extra path segment at the beginning
                    val ii = if (dataUri.scheme == "ampache") i else i-1
                    when(ii) {
                        0 -> type = value
                        1 -> id = value
                        2 -> title = URLDecoder.decode(value, "UTF-8")
                        3 -> album = URLDecoder.decode(value, "UTF-8")
                        4 -> artist = URLDecoder.decode(value, "UTF-8")
                        5 -> webLink = URLDecoder.decode(value, "UTF-8")
                    }
                }
                callback(type, id, title, artist, webLink)
            }
        }
    }
}
