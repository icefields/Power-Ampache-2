package luci.sixsixsix.powerampache2.data.plugins

import luci.sixsixsix.powerampache2.domain.plugin.LyricsPluginDataSource
import javax.inject.Inject

class LyricsPluginDataSourceImpl @Inject constructor(
    private val lyricsPluginClient: LyricsPluginClient

): LyricsPluginDataSource {
    override fun isLyricsPluginInstalled(): Boolean = lyricsPluginClient.isLyricsPluginInstalled()

    override suspend fun getLyricsUrl(songTitle: String, albumTitle: String, artistName: String): String =
        lyricsPluginClient.fetchLyricsPlugin(songTitle, albumTitle, artistName)
}
