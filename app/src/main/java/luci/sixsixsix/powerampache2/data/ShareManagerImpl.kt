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
package luci.sixsixsix.powerampache2.data

import android.content.Context
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.ShareManager
import java.lang.StringBuilder
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareManagerImpl @Inject constructor(
    val songsRepository: SongsRepository
): ShareManager {
    override suspend fun shareSongDeepLink(context: Context, song: Song) {
        StringBuilder("https://")
            .append(context.getString(R.string.deepLink_host))
            .append("/share")
            .append("/song")
            .append("/${song.id}")
            .append("/${URLEncoder.encode(song.title, "UTF-8")}")
            .append("/${URLEncoder.encode(song.album.name,"UTF-8")}")
            .append("/${URLEncoder.encode(song.artist.name,"UTF-8")}")
            .apply {
                songsRepository.getSongShareLink(song).collect { result ->
                    if (result is Resource.Success) {
                        result.data?.let { webShareLink ->
                            append("/${URLEncoder.encode(webShareLink,"UTF-8")}")
                        }
                    }
                }
                context.shareLink(toString())
            }
    }

    override suspend fun shareSongWeb(context: Context, song: Song)  {
        songsRepository.getSongShareLink(song).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    context.shareLink(it)
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }
    }

    override suspend fun fetchDeepLinkedSong(
        id: String,
        title: String,
        artist: String,
        songCallback: (song: Song) -> Unit,
        songsCallback: (songs: List<Song>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val song = songsRepository.getSongFromId(id)
        L("aaaa parse intent",id, title, artist, song?.id, song?.title, song?.album?.name, song?.artist?.name)

        if (song == null || title != song.title || artist != song.artist.name) {
            songsRepository.getSongs(query = title).collect { result ->
                when (result) {
                    is Resource.Success -> result.networkData?.let {
                        if (it.isNotEmpty()) {
                            songsCallback(it)
                        } else {
                            errorCallback()
                        }
                    }
                    is Resource.Error -> errorCallback()
                    is Resource.Loading -> { }
                }

            }
        } else {
            songCallback(song)
        }
    }
}
