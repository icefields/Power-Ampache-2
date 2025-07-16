package luci.sixsixsix.powerampache2.domain.plugin.info

data class PluginArtistData(
    val id: String,
    val artistName: String,
    val description: String,
    val shortDescription: String,
    val mbId: String = "",
    val language: String = "",
    val imageUrl: String = "",
    val year: String,

    val url: String? = null,
    // val links // TODO: add links

    val onTour: String? = "",
    val similar: List<SimilarArtist>,
    val listeners: Int,
    val playCount: Int,
    val tags: List<String>
) {
    companion object {
        fun emptyPluginArtistData(artistId: String, artistName: String, artistMbId: String) = PluginArtistData(
            id = artistId,
            artistName = artistName,
            description = "",
            shortDescription = "",
            mbId = artistMbId,
            language = "",
            imageUrl = "",
            year = "",
            url = "",
            onTour = "",
            similar = listOf(),
            listeners = 0,
            playCount = 0,
            tags = listOf()
        )
    }
}

data class SimilarArtist(
    val name: String,
    val url: String,
    val image: String,
    val mbId: String
)

