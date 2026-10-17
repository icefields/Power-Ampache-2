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

package luci.sixsixsix.powerampache2.domain.common

import luci.sixsixsix.powerampache2.domain.models.EpisodePosition

object ResumePolicy {
    const val FINISHED_THRESHOLD_MS = 30_000L
    const val SAVE_INTERVAL_MS = 5_000L
    const val SEEK_TOLERANCE_MS = 2_000L

    /** an unknown duration (0 or negative, ie. C.TIME_UNSET) never counts as finished */
    fun isFinished(positionMs: Long, durationMs: Long): Boolean =
        durationMs > 0 && durationMs - positionMs <= FINISHED_THRESHOLD_MS

    fun shouldSave(lastSavedAtMs: Long, nowMs: Long): Boolean =
        nowMs - lastSavedAtMs >= SAVE_INTERVAL_MS

    fun startPositionMs(saved: EpisodePosition?): Long =
        if (saved == null || isFinished(saved.positionMs, saved.durationMs)) 0L else saved.positionMs
}
