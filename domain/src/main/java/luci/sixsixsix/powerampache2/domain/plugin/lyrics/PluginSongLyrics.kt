package luci.sixsixsix.powerampache2.domain.plugin.lyrics

data class PluginSongLyrics(
    val lyricsUrl: String = "",
    val lyrics: String = ""
)

fun PluginSongLyrics.getAvailableLyrics() = if (lyrics.isNotBlank()) lyrics else lyricsUrl
