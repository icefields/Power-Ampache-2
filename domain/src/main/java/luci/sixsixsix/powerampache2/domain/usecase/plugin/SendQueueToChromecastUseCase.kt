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
package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.PluginRepository
import luci.sixsixsix.powerampache2.domain.errors.Pa2CastQueueException
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

class SendQueueToChromecastUseCase @Inject constructor(
    private val pluginRepository: PluginRepository
) {
    suspend operator fun invoke(queue: List<Song>) = try {
        pluginRepository.sendQueueToChromecast(queue)
        true
    } catch (e: Pa2CastQueueException) {
        L.e("SendQueueToChromecastUseCase Exception", e)
        false
    }
}
