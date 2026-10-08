package luci.sixsixsix.powerampache2

import luci.sixsixsix.powerampache2.domain.models.MediaKey
import luci.sixsixsix.powerampache2.domain.models.MediaType
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.models.PlayableUI
import luci.sixsixsix.powerampache2.presentation.models.distinctByKey
import luci.sixsixsix.powerampache2.presentation.models.queueEntryKeys
import luci.sixsixsix.powerampache2.presentation.models.toPodcastEpisodeUI
import luci.sixsixsix.powerampache2.presentation.models.toSongUI
import luci.sixsixsix.powerampache2.presentation.models.totalTime
import luci.sixsixsix.powerampache2.presentation.models.withDownloadState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

fun testSong(id: String) = Song(
    mediaId = id, title = "song $id", artist = MusicAttribute("9", "Artist"),
    album = MusicAttribute.emptyInstance(), albumArtist = MusicAttribute.emptyInstance(),
    genre = listOf(), songUrl = "", imageUrl = "", time = 125,
    averageRating = 0f, preciseRating = 0f, rating = 0f
).toSongUI()

fun testEpisode(id: String, state: String = "completed") = PodcastEpisode(
    id = id, title = "episode $id", podcast = MusicAttribute("1", "Podcast"), state = state, time = 3000
).toPodcastEpisodeUI()

class PlayableUITest {
    @Test
    fun songKeyIsASongKey() {
        assertEquals(MediaKey(MediaType.SONG, "123"), testSong("123").key)
    }

    @Test
    fun episodeKeyIsAnEpisodeKey() {
        assertEquals(MediaKey(MediaType.PODCAST_EPISODE, "123"), testEpisode("123").key)
    }

    @Test
    fun subtitles() {
        assertEquals("Artist", testSong("1").subtitle)
        assertEquals("Podcast", testEpisode("1").subtitle)
    }

    @Test
    fun totalTimeFormatsMinutesAndSeconds() {
        assertEquals("2:05", testSong("1").totalTime())
        assertEquals("50:00", testEpisode("1").totalTime())
    }

    @Test
    fun distinctByKeyKeepsSongAndEpisodeWithTheSameId() {
        val list: List<PlayableUI> = listOf(testSong("123"), testEpisode("123"), testSong("123"))
        assertEquals(listOf(testSong("123").key, testEpisode("123").key), list.distinctByKey().map { it.key })
    }

    @Test
    fun queueEntryKeysAreUniqueForRepeatedItems() {
        val queue: List<PlayableUI> = listOf(testSong("123"), testEpisode("123"), testSong("123"), testSong("7"))
        assertEquals(listOf("123", "pe-123", "123#2", "7"), queue.queueEntryKeys())
    }

    @Test
    fun withDownloadStateFollowsTheDownloadedIds() {
        val episode = testEpisode("123")
        assertTrue(episode.withDownloadState(setOf("123")).isDownloaded)
        val downloaded = episode.copy(isDownloaded = true)
        assertFalse(downloaded.withDownloadState(setOf()).isDownloaded)
        assertSame(downloaded, downloaded.withDownloadState(setOf("123")))
    }
}
