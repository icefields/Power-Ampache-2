package luci.sixsixsix.powerampache2.domain.plugin

interface LyricsPluginDataSource {
    fun isLyricsPluginInstalled(): Boolean
    suspend fun getLyricsUrl(songTitle: String, albumTitle: String, artistName: String): String
}
