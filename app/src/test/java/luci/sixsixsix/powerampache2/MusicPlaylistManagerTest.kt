package luci.sixsixsix.powerampache2

import luci.sixsixsix.powerampache2.common.Pa2Config
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MusicPlaylistManagerTest {
    private lateinit var manager: MusicPlaylistManager

    @Before
    @Suppress("DEPRECATION") // the app still sets Constants.config this way
    fun setUp() {
        // Pa2Config has no defaults for these, the values do not matter for the queue
        Constants.config = Pa2Config(
            queueResetOnNewSession = false, dogmazicDemoUser = "", forceLoginDialogsOnAllVersions = false,
            enableTokenLogin = false, dogmazicDemoToken = "", dogmazicDemoUrl = "",
            playlistsUserFetch = false, smartlistsUserFetch = false, playlistsAdminFetch = false,
            smartlistsAdminFetch = false, playlistsServerAllFetch = false, useIncrementalLimitForAlbums = false
        )
        manager = MusicPlaylistManager()
    }

    private fun queueKeys() = manager.currentQueueState.value.map { it.key }

    @Test
    fun songAndEpisodeWithTheSameIdAreBothQueued() {
        manager.addToCurrentQueue(listOf(testSong("123"), testEpisode("123")))
        assertEquals(listOf(testSong("123").key, testEpisode("123").key), queueKeys())
    }

    @Test
    fun addingTheSameEpisodeTwiceKeepsOneCopy() {
        manager.addToCurrentQueue(testEpisode("1"))
        manager.addToCurrentQueue(testEpisode("1").copy(isDownloaded = true))
        assertEquals(1, manager.currentQueueState.value.size)
    }

    @Test
    fun firstItemBecomesCurrent() {
        manager.addToCurrentQueue(listOf(testEpisode("1"), testSong("2")))
        assertEquals(testEpisode("1").key, manager.currentItemState.value?.key)
    }

    @Test
    fun playNextInsertsAfterTheCurrentItem() {
        manager.addToCurrentQueue(listOf(testSong("1"), testSong("2")))
        manager.addToCurrentQueueNext(testEpisode("1"))
        assertEquals(listOf(testSong("1").key, testEpisode("1").key, testSong("2").key), queueKeys())
    }

    @Test
    fun removingAnEpisodeKeepsTheSongWithTheSameId() {
        manager.addToCurrentQueue(listOf(testSong("123"), testEpisode("123")))
        manager.removeFromCurrentQueue(testEpisode("123"))
        assertEquals(listOf(testSong("123").key), queueKeys())
    }

    @Test
    fun updateTopSongMovesTheItemToTheTop() {
        manager.addToCurrentQueue(listOf(testSong("1"), testEpisode("2")))
        manager.updateTopSong(testEpisode("2"))
        assertEquals(listOf(testEpisode("2").key, testSong("1").key), queueKeys())
        assertEquals(testEpisode("2").key, manager.currentItemState.value?.key)
    }

    @Test
    fun playEpisodeOnTopKeepsTheRestOfTheQueue() {
        manager.addToCurrentQueue(listOf(testSong("1"), testSong("2")))
        manager.addToCurrentQueueUpdateTopSong(testEpisode("9"), listOf())
        assertEquals(listOf(testSong("1").key, testEpisode("9").key, testSong("2").key), queueKeys())
        assertEquals(testEpisode("9").key, manager.currentItemState.value?.key)
    }

    @Test
    fun songPlayNextWhenTheCurrentSongIsNotInTheQueueInsertsAtTheTop() {
        manager.replaceQueuePlaySong(listOf(testSong("1"), testSong("2")), testSong("9"))
        manager.addToCurrentQueueNext(testSong("3"))
        assertEquals(listOf(testSong("3").key, testSong("1").key, testSong("2").key), queueKeys())
    }

    @Test
    fun songAddToQueueTopKeepsDuplicates() {
        manager.addToCurrentQueue(listOf(testSong("1"), testSong("2")))
        manager.addToCurrentQueueTop(listOf(testSong("1")))
        assertEquals(listOf(testSong("1").key, testSong("1").key, testSong("2").key), queueKeys())
    }
}
