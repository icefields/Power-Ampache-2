package luci.sixsixsix.powerampache2.domain.plugin.info

data class PluginSongData(
    val id: String,
    val title: String = "",
    val albumName: String = "",
    val artistName: String = "",
    val description: String = "",
    val shortDescription: String = "",
    val mbId: String = "",
    val language: String = "",
    val lyrics: String = "",
    val albumMbId: String = "",
    val artistMbId: String = "",
    val imageUrl: String = "",
    val year: String = "",
    val url: String,
    val artistAlbum: String,
    val imageAlbum: String,
    val urlAlbum: String,
    val imageArtist: String,
    val urlArtist: String,
    val duration: Int,
    val listeners: Int,
    val playCount: Int,
    val topTags: List<String>,
    val position: Int
) {
    companion object {
        fun emptyPluginSongData(
            songId: String,
            musicBrainzId: String,
            songTitle: String,
            albumTitle: String,
            artistName: String
        ) = PluginSongData(
            id = songId,
            mbId = musicBrainzId,
            title = songTitle,
            albumName = albumTitle,
            artistName = artistName,
            description = "",
            shortDescription = "",
            language = "",
            lyrics = "",
            albumMbId = "",
            artistMbId = "",
            imageUrl = "",
            year = "",
            url = "",
            artistAlbum = "",
            imageAlbum = "",
            urlAlbum = "",
            imageArtist = "",
            urlArtist = "",
            duration = 0,
            listeners = 0,
            playCount = 0,
            topTags = listOf(),
            position = 0
        )
    }
}
