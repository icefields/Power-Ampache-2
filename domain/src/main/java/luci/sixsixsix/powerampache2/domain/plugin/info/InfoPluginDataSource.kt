package luci.sixsixsix.powerampache2.domain.plugin.info

interface InfoPluginDataSource {
    fun isInfoPluginInstalled(): Boolean
    suspend fun getArtistInfo(
        artistId: String,
        musicBrainzId: String,
        artistName: String
    ): PluginArtistData?
    suspend fun getAlbumInfo(
        albumId: String,
        musicBrainzId: String,
        albumTitle: String,
        artistName: String
    ): PluginAlbumData?
    suspend fun getSongInfo(
        songId: String,
        musicBrainzId: String,
        songTitle: String,
        albumTitle: String,
        artistName: String
    ): PluginSongData
}
