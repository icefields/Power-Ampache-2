package luci.sixsixsix.powerampache2.data.plugins

import luci.sixsixsix.powerampache2.domain.plugin.info.InfoPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginAlbumData
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginArtistData
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginSongData
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InfoPluginDataSourceImpl @Inject constructor(
    private val infoPluginClient: InfoPluginClient
): InfoPluginDataSource {
    override fun isInfoPluginInstalled(): Boolean = infoPluginClient.isInfoPluginInstalled()

    override suspend fun getArtistInfo(
        artistId: String,
        musicBrainzId: String,
        artistName: String
    ) = infoPluginClient.fetchArtistInfoPlugin(artistId = artistId, artistName = artistName, mbId = musicBrainzId)

    override suspend fun getAlbumInfo(
        albumId: String,
        musicBrainzId: String,
        albumTitle: String,
        artistName: String
    ): PluginAlbumData? = infoPluginClient.fetchAlbumInfoPlugin(
        albumId = albumId,
        mbId = musicBrainzId,
        albumTitle = albumTitle,
        artistName = artistName
    )

    override suspend fun getSongInfo(
        songId: String,
        musicBrainzId: String,
        songTitle: String,
        albumTitle: String,
        artistName: String
    ): PluginSongData = infoPluginClient.fetchSongInfoPlugin(
        songId = songId,
        mbId = musicBrainzId,
        songTitle = songTitle,
        albumTitle = albumTitle,
        artistName = artistName
    ) ?: PluginSongData.emptyPluginSongData(songId = songId, musicBrainzId = musicBrainzId, songTitle = songTitle, albumTitle = albumTitle, artistName = artistName)
}
