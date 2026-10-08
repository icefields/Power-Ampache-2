package luci.sixsixsix.powerampache2

import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.MainContentMenuItem
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.drawerItemsFor
import luci.sixsixsix.powerampache2.presentation.screens_detail.podcast_detail.episodeProgressFraction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrawerItemsTest {
    @Test
    fun podcastsHiddenWithoutPodcasts() {
        assertFalse(drawerItemsFor(0).contains(MainContentMenuItem.Podcasts))
    }

    @Test
    fun podcastsShownAfterOffline() {
        val items = drawerItemsFor(4)
        assertTrue(items.contains(MainContentMenuItem.Podcasts))
        assertEquals(items.indexOf(MainContentMenuItem.Offline) + 1, items.indexOf(MainContentMenuItem.Podcasts))
    }

    @Test
    fun podcastsIdRoundTrips() {
        assertEquals(MainContentMenuItem.Podcasts, MainContentMenuItem.toMainContentMenuItem("podcasts"))
    }

    @Test
    fun progressFraction() {
        assertEquals(0f, episodeProgressFraction(null, 3000), 0.0001f)
        assertEquals(0.5f, episodeProgressFraction(EpisodePosition("1", 1_000_000L, 2_000_000L), 3000), 0.0001f)
        // unknown stored duration, fall back to the episode length
        assertEquals(0.5f, episodeProgressFraction(EpisodePosition("1", 50_000L, 0L), 100), 0.0001f)
        assertEquals(0f, episodeProgressFraction(EpisodePosition("1", 50_000L, 0L), 0), 0.0001f)
    }
}
