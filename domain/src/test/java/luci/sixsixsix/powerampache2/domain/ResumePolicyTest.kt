package luci.sixsixsix.powerampache2.domain

import luci.sixsixsix.powerampache2.domain.common.ResumePolicy
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResumePolicyTest {
    private val oneHourMs = 3_600_000L

    @Test
    fun positionInTheLast30SecondsIsFinished() {
        assertTrue(ResumePolicy.isFinished(positionMs = oneHourMs - 30_000L, durationMs = oneHourMs))
        assertTrue(ResumePolicy.isFinished(positionMs = oneHourMs, durationMs = oneHourMs))
    }

    @Test
    fun positionBeforeTheLast30SecondsIsNotFinished() {
        assertFalse(ResumePolicy.isFinished(positionMs = oneHourMs - 30_001L, durationMs = oneHourMs))
    }

    @Test
    fun unknownDurationIsNeverFinished() {
        // Media3 reports C.TIME_UNSET (a large negative number) before it knows the duration
        assertFalse(ResumePolicy.isFinished(positionMs = 10_000L, durationMs = Long.MIN_VALUE + 1))
        assertFalse(ResumePolicy.isFinished(positionMs = 10_000L, durationMs = 0L))
    }

    @Test
    fun savesOnlyAfterTheInterval() {
        assertFalse(ResumePolicy.shouldSave(lastSavedAtMs = 10_000L, nowMs = 14_999L))
        assertTrue(ResumePolicy.shouldSave(lastSavedAtMs = 10_000L, nowMs = 15_000L))
    }

    @Test
    fun startsAtZeroWithoutASavedPosition() {
        assertEquals(0L, ResumePolicy.startPositionMs(null))
    }

    @Test
    fun startsAtTheSavedPosition() {
        assertEquals(600_000L, ResumePolicy.startPositionMs(EpisodePosition("1", 600_000L, oneHourMs)))
    }

    @Test
    fun finishedEpisodeStartsAtZero() {
        assertEquals(0L, ResumePolicy.startPositionMs(EpisodePosition("1", oneHourMs - 1_000L, oneHourMs)))
    }

    @Test
    fun savedPositionWithUnknownDurationIsUsed() {
        assertEquals(600_000L, ResumePolicy.startPositionMs(EpisodePosition("1", 600_000L, 0L)))
    }
}
