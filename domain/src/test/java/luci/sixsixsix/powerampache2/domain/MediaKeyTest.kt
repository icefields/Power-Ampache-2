package luci.sixsixsix.powerampache2.domain

import luci.sixsixsix.powerampache2.domain.models.MediaKey
import luci.sixsixsix.powerampache2.domain.models.MediaType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MediaKeyTest {
    @Test
    fun songPlayerIdIsThePlainServerId() {
        assertEquals("123", MediaKey(MediaType.SONG, "123").playerId)
    }

    @Test
    fun episodePlayerIdHasThePrefix() {
        assertEquals("pe-123", MediaKey(MediaType.PODCAST_EPISODE, "123").playerId)
    }

    @Test
    fun parsesEpisodePlayerId() {
        assertEquals(MediaKey(MediaType.PODCAST_EPISODE, "123"), MediaKey.fromPlayerId("pe-123"))
    }

    @Test
    fun parsesSongPlayerId() {
        assertEquals(MediaKey(MediaType.SONG, "123"), MediaKey.fromPlayerId("123"))
    }

    @Test
    fun songAndEpisodeWithTheSameServerIdAreDifferentKeys() {
        val song = MediaKey(MediaType.SONG, "123")
        val episode = MediaKey(MediaType.PODCAST_EPISODE, "123")
        assertNotEquals(song, episode)
        assertNotEquals(song.playerId, episode.playerId)
    }
}
