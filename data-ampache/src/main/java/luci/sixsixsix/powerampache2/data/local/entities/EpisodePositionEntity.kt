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
package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition

@Entity(primaryKeys = ["episodeId", "multiUserId"])
data class EpisodePositionEntity(
    val episodeId: String,
    val positionMs: Long,
    val durationMs: Long,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String,
)

fun EpisodePositionEntity.toEpisodePosition() = EpisodePosition(
    episodeId = episodeId,
    positionMs = positionMs,
    durationMs = durationMs,
)

fun EpisodePosition.toEpisodePositionEntity(multiUserId: String) = EpisodePositionEntity(
    episodeId = episodeId,
    positionMs = positionMs,
    durationMs = durationMs,
    multiUserId = multiUserId,
)
