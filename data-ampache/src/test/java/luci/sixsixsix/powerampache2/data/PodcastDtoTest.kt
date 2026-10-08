package luci.sixsixsix.powerampache2.data

import com.google.gson.Gson
import luci.sixsixsix.powerampache2.data.remote.dto.PodcastEpisodesResponse
import luci.sixsixsix.powerampache2.data.remote.dto.PodcastsResponse
import luci.sixsixsix.powerampache2.data.remote.dto.toEpisodeList
import luci.sixsixsix.powerampache2.data.remote.dto.toPodcastList
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.PodcastsDisabledException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PodcastDtoTest {
    private val gson = Gson()

    private val podcastsJson = """
        {"total_count":1,"md5":"x","podcast":[{"id":"2","name":"Example Talk",
        "description":"A weekly show","language":"en","copyright":"","feed_url":"https://podcast.example.com/feed",
        "generator":"","website":"https://podcast.example.com","build_date":"","sync_date":"2026-01-15T10:00:00+00:00",
        "public_url":"","art":"https://example.org/image.php?object_id=2&object_type=podcast&auth=TOKEN",
        "has_art":true,"flag":false,"rating":null,"averagerating":null,"podcast_episode":[]}]}
    """.trimIndent()

    private val episodesJson = """
        {"total_count":2,"md5":"x","podcast_episode":[
        {"id":"123","title":"Episode 42","name":"Episode 42",
         "podcast":{"id":"1","name":"Example Radio"},
         "description":"&lt;p&gt;news&lt;/p&gt;","category":"","author":"","author_full":"",
         "website":"https://podcast.example.com/episode/42","pubdate":"2026-01-15T08:30:00+00:00",
         "state":"completed","filelength":"00:50:00","filesize":"45.78 MB",
         "filename":"Example Radio - Episode 42.mp3","mime":"audio/mpeg","time":3000,
         "size":48000000,"bitrate":128001,"stream_bitrate":128001,"rate":44100,"mode":"cbr","channels":2,
         "public_url":"","url":"https://example.org/play/index.php?ssid=TOKEN&type=podcast_episode&oid=123",
         "catalog":"3","art":"https://example.org/image.php?object_id=1&object_type=podcast&auth=TOKEN",
         "has_art":true,"flag":false,"rating":null,"averagerating":null,"playcount":1,"played":"1"},
        {"id":"7","title":"Old","name":"Old","podcast":{"id":"1","name":"P"},"pubdate":"",
         "state":"pending","time":0,"size":0,"filename":"","mime":"","url":"","art":"","has_art":false}
        ]}
    """.trimIndent()

    @Test
    fun mapsPodcasts() {
        val podcasts = gson.fromJson(podcastsJson, PodcastsResponse::class.java).toPodcastList()
        assertEquals(1, podcasts.size)
        assertEquals("2", podcasts[0].id)
        assertEquals("Example Talk", podcasts[0].name)
        assertEquals("https://podcast.example.com", podcasts[0].website)
        assertTrue(podcasts[0].artUrl.contains("object_type=podcast"))
    }

    @Test
    fun mapsEpisodes() {
        val episodes = gson.fromJson(episodesJson, PodcastEpisodesResponse::class.java).toEpisodeList()
        val episode = episodes[0]
        assertEquals("123", episode.id)
        assertEquals("Episode 42", episode.title)
        assertEquals("1", episode.podcast.id)
        assertEquals(3000, episode.time)
        assertEquals(48000000L, episode.size)
        assertEquals("audio/mpeg", episode.mime)
        assertEquals(1768465800L, episode.pubDateEpochSec)
        assertTrue(episode.isPlayable)
    }

    @Test
    fun pendingEpisodeWithEmptyUrlIsNotPlayable() {
        val episodes = gson.fromJson(episodesJson, PodcastEpisodesResponse::class.java).toEpisodeList()
        assertFalse(episodes[1].isPlayable)
        assertEquals(0L, episodes[1].pubDateEpochSec)
    }

    @Test(expected = PodcastsDisabledException::class)
    fun accessDeniedMeansPodcastsDisabled() {
        val json = """{"error":{"errorAction":"podcasts","errorCode":"4703","errorMessage":"Enable: podcast","errorType":"system"}}"""
        gson.fromJson(json, PodcastsResponse::class.java).toPodcastList()
    }

    @Test
    fun emptyErrorMeansEmptyList() {
        val json = """{"error":{"errorAction":"podcasts","errorCode":"4704","errorMessage":"No Results","errorType":"empty"}}"""
        assertEquals(0, gson.fromJson(json, PodcastsResponse::class.java).toPodcastList().size)
    }

    @Test
    fun emptyArrayMeansEmptyList() {
        assertEquals(0, gson.fromJson("""{"podcast":[]}""", PodcastsResponse::class.java).toPodcastList().size)
        assertEquals(0, gson.fromJson("""{"podcast_episode":[]}""", PodcastEpisodesResponse::class.java).toEpisodeList().size)
    }

    @Test(expected = MusicException::class)
    fun otherErrorsAreMusicExceptions() {
        val json = """{"error":{"errorAction":"podcast_episodes","errorCode":"4701","errorMessage":"Session Expired","errorType":"account"}}"""
        gson.fromJson(json, PodcastEpisodesResponse::class.java).toEpisodeList()
    }
}
