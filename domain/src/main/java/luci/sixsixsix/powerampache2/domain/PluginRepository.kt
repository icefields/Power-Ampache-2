package luci.sixsixsix.powerampache2.domain

interface PluginRepository {
    fun isLyricsPluginInstalled(): Boolean
    fun isInfoPluginInstalled(): Boolean
}