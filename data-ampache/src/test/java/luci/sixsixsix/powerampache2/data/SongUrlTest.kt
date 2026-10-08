package luci.sixsixsix.powerampache2.data

import luci.sixsixsix.powerampache2.data.local.models.SongUrl
import org.junit.Assert.assertEquals
import org.junit.Test

class SongUrlTest {
    @Test
    fun buildsEpisodeStreamUrl() {
        assertEquals(
            "https://example.org/ampache/server/json.server.php?action=stream&stats=0&auth=tok&type=podcast_episode&id=123",
            SongUrl(authToken = "tok", serverUrl = "https://example.org/ampache").getEpisodeUrl("123")
        )
    }

    @Test
    fun episodeUrlIgnoresLowBitrate() {
        val url = SongUrl(authToken = "tok", serverUrl = "https://example.org", bitrate = 96).getEpisodeUrl("1")
        assertEquals(false, url.contains("bitrate"))
    }

    @Test
    fun songUrlIsUnchanged() {
        assertEquals(
            "https://example.org/server/json.server.php?action=stream&stats=0&auth=tok&type=song&id=123",
            SongUrl(authToken = "tok", serverUrl = "https://example.org").getUrl("123")
        )
    }
}
