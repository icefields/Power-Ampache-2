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
package luci.sixsixsix.powerampache2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.PodcastRepository
import luci.sixsixsix.powerampache2.domain.common.ResumePolicy
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.Podcast
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import luci.sixsixsix.powerampache2.player.EpisodeResumeTracker
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

/** in-memory positions, the other functions are not used by the tracker */
private class FakePodcastRepository : PodcastRepository {
    val positions = ConcurrentHashMap<String, EpisodePosition>()
    /** a slow first save: without ordering, a later save would finish first and be overwritten */
    var firstSaveDelayMs = 0L
    private var saves = 0

    override val downloadedEpisodesFlow: Flow<List<PodcastEpisode>> = flowOf(listOf())
    override val episodePositionsFlow: Flow<Map<String, EpisodePosition>> = flowOf(mapOf())
    override suspend fun getPodcasts(fetchRemote: Boolean): Flow<Resource<List<Podcast>>> = flowOf()
    override suspend fun getPodcast(podcastId: String): Podcast? = null
    override suspend fun getPodcastEpisodes(podcastId: String, offset: Int, fetchRemote: Boolean): Flow<Resource<List<PodcastEpisode>>> = flowOf()
    override suspend fun getEpisodeUri(episode: PodcastEpisode): String = ""
    override suspend fun isEpisodeDownloaded(episodeId: String): Boolean = false
    override suspend fun downloadEpisode(episode: PodcastEpisode) {}
    override suspend fun downloadEpisodeAndAddToDb(episodeId: String, multiUserId: String?): PodcastEpisode? = null
    override suspend fun deleteDownloadedEpisode(episode: PodcastEpisode) {}
    override suspend fun getEpisodePosition(episodeId: String) = positions[episodeId]
    override suspend fun saveEpisodePosition(position: EpisodePosition) {
        if (saves++ == 0) delay(firstSaveDelayMs)
        positions[position.episodeId] = position
    }
    override suspend fun clearEpisodePosition(episodeId: String) {
        if (saves++ == 0) delay(firstSaveDelayMs)
        positions.remove(episodeId)
    }
}

class EpisodeResumeTrackerTest {
    private val repository = FakePodcastRepository()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val tracker = EpisodeResumeTracker(repository, scope)
    private val oneHourMs = 3_600_000L
    private val startMs = 1_000_000L

    @After
    fun tearDown() = scope.cancel()

    /** reads run after the saves requested before them, so a read waits for the pending saves */
    private fun savedPosition(episodeId: String): EpisodePosition? = runBlocking {
        tracker.startPositionMs(episodeId)
        repository.positions[episodeId]
    }

    /** starts [playerId] and finishes its resume lookup at [positionMs] */
    private fun startResumed(playerId: String, positionMs: Long = 0L) {
        val generation = tracker.onItemStarted(playerId, nowMs = startMs)
        tracker.onResumeApplied(generation, positionMs)
    }

    @Test
    fun savesEpisodePosition() = runBlocking {
        tracker.save("pe-123", 600_000L, oneHourMs)
        assertEquals(EpisodePosition("123", 600_000L, oneHourMs), repository.positions["123"])
    }

    @Test
    fun ignoresSongs() = runBlocking {
        tracker.save("123", 600_000L, oneHourMs)
        assertNull(repository.positions["123"])
    }

    @Test
    fun finishedEpisodeClearsItsPosition() = runBlocking {
        repository.positions["123"] = EpisodePosition("123", 600_000L, oneHourMs)
        tracker.save("pe-123", oneHourMs - 10_000L, oneHourMs)
        assertNull(repository.positions["123"])
    }

    @Test
    fun unknownDurationIsStoredAsZero() = runBlocking {
        tracker.save("pe-1", 5_000L, Long.MIN_VALUE + 1)
        assertEquals(EpisodePosition("1", 5_000L, 0L), repository.positions["1"])
    }

    @Test
    fun startPositionUsesTheSavedPosition() = runBlocking {
        repository.positions["123"] = EpisodePosition("123", 600_000L, oneHourMs)
        assertEquals(600_000L, tracker.startPositionMs("123"))
        assertEquals(0L, tracker.startPositionMs("unknown"))
    }

    @Test
    fun ticksSaveOnlyAfterAFullInterval() {
        startResumed("pe-123")
        tracker.onProgress("pe-123", 1_000L, oneHourMs, nowMs = startMs + 1_000L)
        assertNull(savedPosition("123"))

        tracker.onProgress("pe-123", 605_000L, oneHourMs, nowMs = startMs + ResumePolicy.SAVE_INTERVAL_MS)
        assertEquals(EpisodePosition("123", 605_000L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun nothingIsSavedWhileTheResumeLookupIsPending() {
        repository.positions["123"] = EpisodePosition("123", 600_000L, oneHourMs)
        tracker.onItemStarted("pe-123", nowMs = startMs)
        // the player still reports the pre-resume position
        tracker.onProgress("pe-123", 500L, oneHourMs, nowMs = startMs + ResumePolicy.SAVE_INTERVAL_MS)
        tracker.onPause("pe-123", 500L, oneHourMs)
        tracker.onItemLeft("pe-123", 500L)
        assertEquals(EpisodePosition("123", 600_000L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun seekThenPauseSavesTheLivePosition() {
        startResumed("pe-123")
        tracker.onProgress("pe-123", 600_000L, oneHourMs, nowMs = startMs + ResumePolicy.SAVE_INTERVAL_MS)
        tracker.onSeek("pe-123", 2_400_000L, oneHourMs)
        tracker.onPause("pe-123", 2_400_100L, oneHourMs)
        assertEquals(EpisodePosition("123", 2_400_100L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun seekWhilePausedIsSaved() {
        startResumed("pe-123")
        tracker.onPause("pe-123", 600_000L, oneHourMs)
        tracker.onSeek("pe-123", 1_200_000L, oneHourMs)
        assertEquals(EpisodePosition("123", 1_200_000L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun seekThenSkipSavesTheDepartingPosition() {
        startResumed("pe-123")
        tracker.onSeek("pe-123", 2_400_000L, oneHourMs)
        tracker.onProgress("pe-123", 2_400_500L, oneHourMs, nowMs = startMs + 1_000L)
        // the player reports the position it left the item at, then the next item starts
        tracker.onItemLeft("pe-123", 2_401_000L)
        tracker.onItemStarted("pe-456", nowMs = startMs + 2_000L)
        assertEquals(EpisodePosition("123", 2_401_000L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun aNewItemSavesThePreviousOneIfItWasNotLeftExplicitly() {
        startResumed("pe-123")
        tracker.onProgress("pe-123", 700_000L, oneHourMs, nowMs = startMs + 1_000L)
        tracker.onItemStarted("pe-456", nowMs = startMs + 2_000L)
        assertEquals(EpisodePosition("123", 700_000L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun aUserSeekMakesThePendingLookupStale() {
        val generation = tracker.onItemStarted("pe-123", nowMs = startMs)
        tracker.onSeek("pe-123", 1_800_000L, oneHourMs)
        assertFalse(tracker.isResumePending(generation))
        // the late lookup result is ignored
        tracker.onResumeApplied(generation, 600_000L)
        tracker.onPause("pe-123", 1_800_000L, oneHourMs)
        assertEquals(EpisodePosition("123", 1_800_000L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun aNewerVisitMakesTheOlderLookupStale() {
        val first = tracker.onItemStarted("pe-123", nowMs = startMs)
        tracker.onItemStarted("pe-456", nowMs = startMs)
        val second = tracker.onItemStarted("pe-123", nowMs = startMs)
        assertFalse(tracker.isResumePending(first))
        assertTrue(tracker.isResumePending(second))
    }

    @Test
    fun savesFinishInTheOrderTheyWereRequested() {
        repository.firstSaveDelayMs = 300L
        startResumed("pe-123")
        tracker.onProgress("pe-123", 600_000L, oneHourMs, nowMs = startMs + ResumePolicy.SAVE_INTERVAL_MS)
        tracker.onPause("pe-123", 610_000L, oneHourMs)
        assertEquals(EpisodePosition("123", 610_000L, oneHourMs), savedPosition("123"))
    }

    @Test
    fun aClearIsNotUndoneByAnOlderSave() {
        repository.firstSaveDelayMs = 300L
        startResumed("pe-123")
        tracker.onProgress("pe-123", 600_000L, oneHourMs, nowMs = startMs + ResumePolicy.SAVE_INTERVAL_MS)
        // the episode ends: inside the last 30 seconds the position is cleared
        tracker.onItemLeft("pe-123", oneHourMs)
        assertNull(savedPosition("123"))
    }
}
