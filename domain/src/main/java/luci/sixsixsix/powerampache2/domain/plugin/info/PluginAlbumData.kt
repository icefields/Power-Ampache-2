package luci.sixsixsix.powerampache2.domain.plugin.info

data class PluginAlbumData(
    val id: String,
    val albumName: String,
    val artistName: String,
    val description: String,
    val shortDescription: String,
    val mbId: String,
    val language: String,
    val lyrics: String,
    val artistMbId: String,
    val albumArtistMbId: String,
    val imageUrl: String,
    val year: String,
    val tags: List<String>,
    val rank: Int,
    val url: String,
    val imageArtist: String,
    val urlArtist: String,
    val duration: Int,
    val listeners: Int,
    val playCount: Int,
    val tracks: List<AlbumTrack>,
) {
    companion object {
        fun emptyPluginAlbumData(
            albumId: String,
            musicBrainzId: String,
            albumTitle: String,
            artistName: String
        ) = PluginAlbumData(
            id = albumId, mbId = musicBrainzId, albumName = albumTitle, artistName = artistName,
            description = "",
            shortDescription = "",
            language = "",
            lyrics = "",
            artistMbId = "",
            albumArtistMbId = "",
            imageUrl = "",
            year = "",
            tags = listOf(),
            rank = 0,
            url = "",
            imageArtist = "",
            urlArtist = "",
            duration = 0,
            listeners = 0,
            playCount = 0,
            tracks = listOf()
        )
    }
}

data class AlbumTrack(
    val mbId: String,
    val title: String,
    val duration: Int,
    val url: String,
    val songArtistName: String,
    val songArtistMbId: String
)
