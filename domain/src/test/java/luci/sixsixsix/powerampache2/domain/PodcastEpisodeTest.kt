package luci.sixsixsix.powerampache2.domain

import luci.sixsixsix.powerampache2.domain.models.MediaKey
import luci.sixsixsix.powerampache2.domain.models.MediaType
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PodcastEpisodeTest {
    private fun episode(state: String) =
        PodcastEpisode(id = "123", title = "t", podcast = MusicAttribute("1", "p"), state = state)

    @Test
    fun completedEpisodeIsPlayable() {
        assertTrue(episode("completed").isPlayable)
    }

    @Test
    fun pendingSkippedAndEmptyStatesAreNotPlayable() {
        assertFalse(episode("pending").isPlayable)
        assertFalse(episode("skipped").isPlayable)
        assertFalse(episode("").isPlayable)
    }

    @Test
    fun keyIsAnEpisodeKey() {
        assertEquals(MediaKey(MediaType.PODCAST_EPISODE, "123"), episode("completed").key)
    }
}
