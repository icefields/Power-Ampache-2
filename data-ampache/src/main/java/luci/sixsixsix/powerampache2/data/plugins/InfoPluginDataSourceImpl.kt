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
package luci.sixsixsix.powerampache2.data.plugins

import luci.sixsixsix.powerampache2.domain.plugin.info.InfoPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginAlbumData
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginSongData
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InfoPluginDataSourceImpl @Inject constructor(
    private val infoPluginClient: InfoPluginClient
): InfoPluginDataSource {
    override fun isInfoPluginInstalled(): Boolean = infoPluginClient.isInfoPluginInstalled()

    override suspend fun getArtistInfo(
        artistId: String,
        musicBrainzId: String,
        artistName: String
    ) = infoPluginClient.fetchArtistInfoPlugin(artistId = artistId, artistName = artistName, mbId = musicBrainzId)

    override suspend fun getAlbumInfo(
        albumId: String,
        musicBrainzId: String,
        albumTitle: String,
        artistName: String
    ): PluginAlbumData? = infoPluginClient.fetchAlbumInfoPlugin(
        albumId = albumId,
        mbId = musicBrainzId,
        albumTitle = albumTitle,
        artistName = artistName
    )

    override suspend fun getSongInfo(
        songId: String,
        musicBrainzId: String,
        songTitle: String,
        albumTitle: String,
        artistName: String
    ): PluginSongData = infoPluginClient.fetchSongInfoPlugin(
        songId = songId,
        mbId = musicBrainzId,
        songTitle = songTitle,
        albumTitle = albumTitle,
        artistName = artistName
    ) ?: PluginSongData.emptyPluginSongData(songId = songId, musicBrainzId = musicBrainzId, songTitle = songTitle, albumTitle = albumTitle, artistName = artistName)
}
