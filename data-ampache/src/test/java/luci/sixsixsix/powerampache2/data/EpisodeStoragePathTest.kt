package luci.sixsixsix.powerampache2.data

import luci.sixsixsix.powerampache2.data.local.episodeFileName
import luci.sixsixsix.powerampache2.data.local.episodeRelativeDir
import luci.sixsixsix.powerampache2.data.local.sanitizeFileName
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EpisodeStoragePathTest {
    private val longTitle = "🎧 Episode title ".repeat(20)

    private fun episode(id: String, title: String, podcastName: String = "Test Radio", filename: String = "x.mp3") =
        PodcastEpisode(id = id, title = title, podcast = MusicAttribute("1", podcastName), filename = filename)

    @Test
    fun relativeDirIsUnderPodcasts() {
        assertEquals("podcasts/Test Radio", episodeRelativeDir(episode("1", "t")))
    }

    @Test
    fun slashInPodcastNameIsReplaced() {
        assertEquals("podcasts/AC_DC talk", episodeRelativeDir(episode("1", "t", podcastName = "AC/DC talk")))
    }

    @Test
    fun fileNameFitsIn255Bytes() {
        val name = episodeFileName(episode("123", longTitle))
        assertTrue(name.toByteArray(Charsets.UTF_8).size <= 255)
        assertTrue(name.endsWith(".mp3"))
    }

    @Test
    fun fileNameHasNoPathSeparators() {
        val name = episodeFileName(episode("1", "a/b\\c:d"))
        assertFalse(name.contains("/"))
        assertFalse(name.contains("\\"))
    }

    @Test
    fun sameTitleDifferentIdGivesDifferentFiles() {
        assertNotEquals(episodeFileName(episode("1", "Bonus")), episodeFileName(episode("2", "Bonus")))
    }

    @Test
    fun extensionComesFromServerFilename() {
        assertTrue(episodeFileName(episode("1", "t", filename = "Pod - t.m4a")).endsWith(".m4a"))
        assertTrue(episodeFileName(episode("1", "t", filename = "")).endsWith(".mp3"))
    }

    @Test
    fun truncationKeepsWholeEmoji() {
        // 🎧 is one surrogate pair of 4 bytes, so 10 bytes keep two whole emoji
        assertEquals("🎧🎧", sanitizeFileName("🎧".repeat(100), maxBytes = 10))
    }

    @Test
    fun blankNameBecomesUnderscore() {
        assertEquals("_", sanitizeFileName("   "))
    }
}
