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
package luci.sixsixsix.powerampache2.data.remote

import luci.sixsixsix.powerampache2.common.Stack
import luci.sixsixsix.powerampache2.domain.models.Song

object OfflineData {
    val songsToScrobble: Stack<ScrobbleData> = mutableListOf()
    val likedOffline: Stack<LikeData> = mutableListOf()
    val ratedOffline: Stack<RateData> = mutableListOf()
}

data class RateData(
    val id: String,
    val rating: Int,
    val type: MainNetwork.Type
)

data class LikeData(
    val id: String,
    val like: Boolean,
    val type: MainNetwork.Type
)

data class ScrobbleData(
    val song: Song,
    val unixTimestamp: Long = (System.currentTimeMillis() / 1000)
)
