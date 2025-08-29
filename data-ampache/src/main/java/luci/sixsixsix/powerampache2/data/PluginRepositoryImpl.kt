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
package luci.sixsixsix.powerampache2.data

import luci.sixsixsix.powerampache2.di.PluginDataSource
import luci.sixsixsix.powerampache2.domain.PluginRepository
import luci.sixsixsix.powerampache2.domain.errors.Pa2CastQueueException
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.plugin.chromecast.ChromecastPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.info.InfoPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.LyricsPluginDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class PluginRepositoryImpl @Inject constructor(
    @PluginDataSource private val lyricsPluginDataSource: LyricsPluginDataSource,
    @PluginDataSource private val chromecastPluginDataSource: ChromecastPluginDataSource,
    @PluginDataSource private val infoPluginDataSource: InfoPluginDataSource
): PluginRepository {
    override fun isLyricsPluginInstalled() =
        lyricsPluginDataSource.isLyricsPluginInstalled()

    override fun isInfoPluginInstalled(): Boolean =
        infoPluginDataSource.isInfoPluginInstalled()

    override fun isChromecastPluginInstalled(): Boolean =
        chromecastPluginDataSource.isChromecastPluginInstalled()

    @Throws(Pa2CastQueueException::class)
    override suspend fun sendQueueToChromecast(queue: List<Song>) {
        chromecastPluginDataSource.sendQueueToChromecast(queue)
    }
}
