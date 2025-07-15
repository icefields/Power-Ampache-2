package luci.sixsixsix.powerampache2.data.plugins

import luci.sixsixsix.powerampache2.domain.plugin.lyrics.LyricsPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.PluginSongLyrics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsPluginDataSourceImpl @Inject constructor(
    private val lyricsPluginClient: LyricsPluginClient
): LyricsPluginDataSource {
    override fun isLyricsPluginInstalled(): Boolean = lyricsPluginClient.isLyricsPluginInstalled()

    override suspend fun getLyrics(
        songTitle: String,
        albumTitle: String,
        artistName: String
    ): PluginSongLyrics =
        lyricsPluginClient.fetchLyricsPlugin(songTitle, albumTitle, artistName) ?: PluginSongLyrics()
}
