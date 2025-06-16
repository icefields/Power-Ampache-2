package luci.sixsixsix.powerampache2.domain.datasource

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song

interface ArtistsRemoteDataSource {
    suspend fun getArtist(auth: String, artistId: String): Artist
    suspend fun getArtists(
        auth: String,
        query: String = "",
        offset: Int = 0,
        fetchAlbumsWithArtist: Boolean,
        albumsCallback: (List<Album>) -> Unit
    ): List<Artist>
    suspend fun getArtistsByGenre(auth: String, genreId: String, offset: Int = 0): List<Artist>
    suspend fun likeArtist(auth: String, id: String, like: Boolean): Boolean
    suspend fun getSongsFromArtist(auth: String, artistId: String): List<Song>
    suspend fun getRecommendedArtists(auth: String, baseArtistId: String, offset: Int = 0): List<Artist>
}
