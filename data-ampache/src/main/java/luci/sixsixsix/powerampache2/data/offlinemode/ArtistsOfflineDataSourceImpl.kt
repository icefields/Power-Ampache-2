package luci.sixsixsix.powerampache2.data.offlinemode

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toArtist
import luci.sixsixsix.powerampache2.di.OfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsOfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

@OfflineModeDataSource
class ArtistsOfflineDataSourceImpl @Inject constructor(
    db: MusicDatabase,
): ArtistsOfflineModeDataSource {

    private val dao = db.dao

    override val recommendedFlow: Flow<List<Artist>>
        get() = dao.getRecommendedOfflineArtists().mapNotNull { list -> list.map { it.toArtist() } }

    override suspend fun getArtist(artistId: String): Artist {
        TODO("Not yet implemented")
    }

    override suspend fun getArtists(query: String): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistsByGenre(genreId: Genre): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun likeArtist(id: String, like: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getMostPlayedArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun getSongsFromArtist(artistId: String): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommendedArtists(baseArtistId: String): List<Artist> =
        dao.getRecommendedOfflineArtists(baseArtistId).map { it.toArtist() }
}
