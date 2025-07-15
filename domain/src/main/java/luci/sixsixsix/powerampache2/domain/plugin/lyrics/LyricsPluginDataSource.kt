package luci.sixsixsix.powerampache2.domain.plugin.lyrics

interface LyricsPluginDataSource {
    fun isLyricsPluginInstalled(): Boolean
    suspend fun getLyrics(songTitle: String, albumTitle: String, artistName: String): PluginSongLyrics
}
