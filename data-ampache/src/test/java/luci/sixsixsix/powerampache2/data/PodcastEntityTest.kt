package luci.sixsixsix.powerampache2.data

import luci.sixsixsix.powerampache2.data.local.entities.toDownloadedPodcastEpisodeEntity
import luci.sixsixsix.powerampache2.data.local.entities.toEpisodePosition
import luci.sixsixsix.powerampache2.data.local.entities.toEpisodePositionEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPodcast
import luci.sixsixsix.powerampache2.data.local.entities.toPodcastEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPodcastEpisode
import luci.sixsixsix.powerampache2.data.local.entities.toPodcastEpisodeEntity
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Podcast
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import org.junit.Assert.assertEquals
import org.junit.Test

class PodcastEntityTest {
    private val episode = PodcastEpisode(
        id = "123", title = "t", podcast = MusicAttribute("1", "p"), description = "d",
        author = "a", pubDateEpochSec = 1768379400L, state = "completed", time = 3000,
        size = 48000000L, mime = "audio/mpeg", filename = "p - t.mp3", artUrl = "https://art", website = "https://w"
    )

    @Test
    fun podcastRoundTrip() {
        val podcast = Podcast("2", "Example Talk", "d", "https://podcast.example.com", "https://art", "2026-01-15T10:00:00+00:00")
        assertEquals(podcast, podcast.toPodcastEntity("user@server").toPodcast())
    }

    @Test
    fun episodeRoundTrip() {
        assertEquals(episode, episode.toPodcastEpisodeEntity("user@server").toPodcastEpisode())
    }

    @Test
    fun downloadedEpisodeUsesLocalImage() {
        val restored = episode.toDownloadedPodcastEpisodeEntity("user@server", "/files/x.mp3", "/files/1.png").toPodcastEpisode()
        assertEquals(episode.copy(artUrl = "/files/1.png"), restored)
    }

    @Test
    fun positionRoundTrip() {
        val position = EpisodePosition("123", 600_000L, 3_000_000L)
        assertEquals(position, position.toEpisodePositionEntity("user@server").toEpisodePosition())
    }
}
