package luci.sixsixsix.powerampache2.domain.datasource

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song

interface ArtistsOfflineModeDataSource {
    val recommendedFlow: Flow<List<Artist>>

    suspend fun getArtist(artistId: String): Artist?
    suspend fun getArtists(owner: String, query: String = ""): List<Artist>
    suspend fun getArtistsByGenre(genreId: Genre): List<Artist>
    suspend fun likeArtist(id: String, like: Boolean): Boolean
    suspend fun getMostPlayedArtists(): List<Artist>
    suspend fun getSongsFromArtist(artistId: String): List<Song>
    suspend fun getRecommendedArtists(baseArtistId: String): List<Artist>
}
