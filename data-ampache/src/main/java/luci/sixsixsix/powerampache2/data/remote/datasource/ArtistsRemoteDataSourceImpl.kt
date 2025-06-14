package luci.sixsixsix.powerampache2.data.remote.datasource

import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toArtistEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.NullDataException
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import kotlin.jvm.Throws

class ArtistsRemoteDataSourceImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase
) : ArtistsRemoteDataSource {
    override suspend fun getArtist(auth: String, artistId: String): Artist {
        TODO("Not yet implemented")
    }

    override suspend fun getArtists(auth: String, query: String, offset: Int): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistsByGenre(
        auth: String,
        genreId: Genre,
        offset: Int
    ): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun likeArtist(auth: String, id: String, like: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getMostPlayedArtists(auth: String): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun getSongsFromArtist(auth: String, artistId: String): List<Song> =
        api.getSongsFromArtist(auth, artistId = artistId).let { response ->
            response.error?.let { error -> throw MusicException(error.toError()) }
            response.songs?.let { songsDto ->
                songsDto.map { songDto -> songDto.toSong() }
            } ?: throw NullDataException("getSongsFromArtist")
        }

    @Throws(MusicException::class, NullDataException::class, NullPointerException::class)
    override suspend fun getRecommendedArtists(auth: String, baseArtistId: String, offset: Int): List<Artist> =
        api.getSimilarArtists(authKey = auth, filter = baseArtistId).let { artistsResponse ->
            artistsResponse.error?.let { error -> throw MusicException(error.toError()) }
            artistsResponse.artists?.let { artistsDto ->
                artistsDto.map { it.toArtist() }
            } ?: throw NullDataException("getRecommendedArtists")
        }
}