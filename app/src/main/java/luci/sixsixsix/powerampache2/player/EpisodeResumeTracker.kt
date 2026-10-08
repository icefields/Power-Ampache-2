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
package luci.sixsixsix.powerampache2.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.PodcastRepository
import luci.sixsixsix.powerampache2.domain.common.ResumePolicy
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.MediaKey
import luci.sixsixsix.powerampache2.domain.models.MediaType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remembers where the user stopped in a podcast episode. Songs are ignored and keep starting at 0.
 * Fed by SimpleMediaServiceHandler: progress ticks, pause and media item transitions.
 */
@Singleton
class EpisodeResumeTracker @Inject constructor(
    private val podcastRepository: PodcastRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastPlayerId: String? = null
    private var lastPositionMs = 0L
    private var lastDurationMs = 0L
    private var lastSavedAtMs = 0L

    /** called on every progress tick, saves at most every ResumePolicy.SAVE_INTERVAL_MS */
    fun onProgress(playerId: String?, positionMs: Long, durationMs: Long, nowMs: Long = System.currentTimeMillis()) {
        if (playerId == null) return
        if (playerId != lastPlayerId) {
            flush()
            // the resume seek is async: the first ticks of a new item can still report about 0,
            // so the first save happens only after a full interval
            lastSavedAtMs = nowMs
        }
        lastPlayerId = playerId
        lastPositionMs = positionMs
        lastDurationMs = durationMs
        if (ResumePolicy.shouldSave(lastSavedAtMs, nowMs)) {
            lastSavedAtMs = nowMs
            scope.launch { save(playerId, positionMs, durationMs) }
        }
    }

    /** saves the last known position now, call on pause and before the player moves to another item */
    fun flush() {
        val playerId = lastPlayerId ?: return
        val positionMs = lastPositionMs
        val durationMs = lastDurationMs
        scope.launch { save(playerId, positionMs, durationMs) }
    }

    suspend fun save(playerId: String, positionMs: Long, durationMs: Long) {
        val key = MediaKey.fromPlayerId(playerId)
        if (key.type != MediaType.PODCAST_EPISODE) return
        try {
            if (ResumePolicy.isFinished(positionMs, durationMs)) {
                podcastRepository.clearEpisodePosition(key.id)
            } else {
                podcastRepository.saveEpisodePosition(
                    EpisodePosition(key.id, positionMs, durationMs.coerceAtLeast(0L))
                )
            }
        } catch (e: Exception) {
            L.e("EpisodeResumeTracker.save", e)
        }
    }

    suspend fun startPositionMs(episodeId: String): Long = try {
        ResumePolicy.startPositionMs(podcastRepository.getEpisodePosition(episodeId))
    } catch (e: Exception) {
        L.e("EpisodeResumeTracker.startPositionMs", e)
        0L
    }
}
