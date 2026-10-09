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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.PodcastRepository
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.Podcast
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import luci.sixsixsix.powerampache2.player.EpisodeResumeTracker
import luci.sixsixsix.powerampache2.domain.common.ResumePolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** in-memory positions, the other functions are not used by the tracker */
private class FakePodcastRepository : PodcastRepository {
    val positions = mutableMapOf<String, EpisodePosition>()
    /** counts down on every save, onProgress saves from its own IO scope */
    val saved = CountDownLatch(1)

    override val downloadedEpisodesFlow: Flow<List<PodcastEpisode>> = flowOf(listOf())
    override val episodePositionsFlow: Flow<Map<String, EpisodePosition>> = flowOf(mapOf())
    override suspend fun getPodcasts(fetchRemote: Boolean): Flow<Resource<List<Podcast>>> = flowOf()
    override suspend fun getPodcast(podcastId: String): Podcast? = null
    override suspend fun getPodcastEpisodes(podcastId: String, offset: Int, fetchRemote: Boolean): Flow<Resource<List<PodcastEpisode>>> = flowOf()
    override suspend fun getEpisodeUri(episode: PodcastEpisode): String = ""
    override suspend fun isEpisodeDownloaded(episodeId: String): Boolean = false
    override suspend fun downloadEpisode(episode: PodcastEpisode) {}
    override suspend fun downloadEpisodeAndAddToDb(episodeId: String): PodcastEpisode? = null
    override suspend fun deleteDownloadedEpisode(episode: PodcastEpisode) {}
    override suspend fun getEpisodePosition(episodeId: String) = positions[episodeId]
    override suspend fun saveEpisodePosition(position: EpisodePosition) {
        positions[position.episodeId] = position
        saved.countDown()
    }
    override suspend fun clearEpisodePosition(episodeId: String) { positions.remove(episodeId) }
}

class EpisodeResumeTrackerTest {
    private val repository = FakePodcastRepository()
    private val tracker = EpisodeResumeTracker(repository)
    private val oneHourMs = 3_600_000L

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
    fun firstTicksOfANewEpisodeDoNotSaveBeforeAFullInterval() {
        val startMs = 1_000_000L
        // the async resume seek has not run yet: the player still reports about 0
        tracker.onProgress("pe-123", 0L, oneHourMs, nowMs = startMs)
        tracker.onProgress("pe-123", 1_000L, oneHourMs, nowMs = startMs + 1_000L)
        assertFalse("no save may start in the first interval", repository.saved.await(500, TimeUnit.MILLISECONDS))
        assertNull(repository.positions["123"])

        // after a full interval the tracker saves again
        tracker.onProgress("pe-123", 605_000L, oneHourMs, nowMs = startMs + ResumePolicy.SAVE_INTERVAL_MS)
        assertTrue(repository.saved.await(5, TimeUnit.SECONDS))
        assertEquals(EpisodePosition("123", 605_000L, oneHourMs), repository.positions["123"])
    }

    @Test
    fun startPositionUsesTheSavedPosition() = runBlocking {
        repository.positions["123"] = EpisodePosition("123", 600_000L, oneHourMs)
        assertEquals(600_000L, tracker.startPositionMs("123"))
        assertEquals(0L, tracker.startPositionMs("unknown"))
    }
}
