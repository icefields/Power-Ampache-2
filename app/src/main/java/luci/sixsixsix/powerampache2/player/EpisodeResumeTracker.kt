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

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
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
 * Fed by SimpleMediaServiceHandler on the main thread: item changes, progress ticks, seeks and pause.
 *
 * Saves, clears and reads run one at a time, in the order they were requested, so an older save
 * cannot overwrite a newer one and a read always sees the saves requested before it.
 */
@Singleton
class EpisodeResumeTracker internal constructor(
    private val podcastRepository: PodcastRepository,
    scope: CoroutineScope,
) {
    @Inject constructor(podcastRepository: PodcastRepository) :
        this(podcastRepository, CoroutineScope(Dispatchers.IO + SupervisorJob()))

    private sealed interface Command {
        data class Save(val playerId: String, val positionMs: Long, val durationMs: Long) : Command
        data class Read(val episodeId: String, val result: CompletableDeferred<Long>) : Command
    }

    private val commands = Channel<Command>(Channel.UNLIMITED)

    private var currentPlayerId: String? = null
    private var positionMs = 0L
    private var durationMs = 0L
    private var lastSavedAtMs = 0L
    // the saved position of the current episode is still being looked up: the player position
    // is the pre-resume one and must not be saved
    private var resumePending = false
    private var generation = 0

    init {
        scope.launch {
            for (command in commands) {
                when (command) {
                    is Command.Save -> save(command.playerId, command.positionMs, command.durationMs)
                    is Command.Read -> command.result.complete(readStartPositionMs(command.episodeId))
                }
            }
        }
    }

    /**
     * A new item became the current one. Saves the previous item if [onItemLeft] did not.
     * Returns the generation to pass to [isResumePending] and [onResumeApplied].
     */
    fun onItemStarted(playerId: String?, nowMs: Long = System.currentTimeMillis()): Int {
        currentPlayerId?.takeIf { it != playerId && !resumePending }?.let { previous ->
            requestSave(previous, positionMs, durationMs)
        }
        generation++
        currentPlayerId = playerId
        positionMs = 0L
        durationMs = 0L
        lastSavedAtMs = nowMs
        resumePending = playerId != null && isEpisode(playerId)
        return generation
    }

    /** true while the resume lookup started by [onItemStarted] for [generation] is still valid */
    fun isResumePending(generation: Int) = resumePending && generation == this.generation

    /** the resume lookup finished and the player is at [positionMs] */
    fun onResumeApplied(generation: Int, positionMs: Long) {
        if (!isResumePending(generation)) return
        resumePending = false
        this.positionMs = positionMs
    }

    /** the player leaves [playerId] at [positionMs], before the next item starts */
    fun onItemLeft(playerId: String?, positionMs: Long) {
        if (playerId == null || playerId != currentPlayerId) return
        if (!resumePending) requestSave(playerId, positionMs, durationMs)
        currentPlayerId = null
    }

    /** called on every progress tick, saves at most every ResumePolicy.SAVE_INTERVAL_MS */
    fun onProgress(playerId: String?, positionMs: Long, durationMs: Long, nowMs: Long = System.currentTimeMillis()) {
        if (playerId == null || playerId != currentPlayerId || resumePending) return
        this.positionMs = positionMs
        this.durationMs = durationMs
        if (ResumePolicy.shouldSave(lastSavedAtMs, nowMs)) {
            lastSavedAtMs = nowMs
            requestSave(playerId, positionMs, durationMs)
        }
    }

    /** the user moved inside the current item: this position wins over a pending resume lookup */
    fun onSeek(playerId: String?, positionMs: Long, durationMs: Long) {
        if (playerId == null || playerId != currentPlayerId) return
        resumePending = false
        savePositionNow(playerId, positionMs, durationMs)
    }

    /** playback paused at the live [positionMs] */
    fun onPause(playerId: String?, positionMs: Long, durationMs: Long) {
        if (playerId == null || playerId != currentPlayerId || resumePending) return
        savePositionNow(playerId, positionMs, durationMs)
    }

    /** the position to start [episodeId] from, after the saves requested before this call */
    suspend fun startPositionMs(episodeId: String): Long {
        val result = CompletableDeferred<Long>()
        commands.trySend(Command.Read(episodeId, result))
        return result.await()
    }

    private fun savePositionNow(playerId: String, positionMs: Long, durationMs: Long) {
        this.positionMs = positionMs
        this.durationMs = durationMs
        requestSave(playerId, positionMs, durationMs)
    }

    private fun requestSave(playerId: String, positionMs: Long, durationMs: Long) {
        if (isEpisode(playerId)) commands.trySend(Command.Save(playerId, positionMs, durationMs))
    }

    private fun isEpisode(playerId: String) = MediaKey.fromPlayerId(playerId).type == MediaType.PODCAST_EPISODE

    internal suspend fun save(playerId: String, positionMs: Long, durationMs: Long) {
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

    private suspend fun readStartPositionMs(episodeId: String): Long = try {
        ResumePolicy.startPositionMs(podcastRepository.getEpisodePosition(episodeId))
    } catch (e: Exception) {
        L.e("EpisodeResumeTracker.startPositionMs", e)
        0L
    }
}
