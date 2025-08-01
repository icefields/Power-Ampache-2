package luci.sixsixsix.powerampache2.data

import luci.sixsixsix.powerampache2.domain.PluginRepository
import luci.sixsixsix.powerampache2.domain.plugin.info.InfoPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.LyricsPluginDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginRepositoryImpl @Inject constructor(
    private val lyricsPluginDataSource: LyricsPluginDataSource,
    private val infoPluginDataSource: InfoPluginDataSource
): PluginRepository {
    override fun isLyricsPluginInstalled() =
        lyricsPluginDataSource.isLyricsPluginInstalled()

    override fun isInfoPluginInstalled(): Boolean =
        infoPluginDataSource.isInfoPluginInstalled()
}
