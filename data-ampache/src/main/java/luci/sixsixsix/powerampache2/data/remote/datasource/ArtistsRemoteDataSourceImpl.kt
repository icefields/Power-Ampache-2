package luci.sixsixsix.powerampache2.data.remote.datasource

import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.ArtistDto
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toArtist
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toSong
import luci.sixsixsix.powerampache2.di.RemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.NullDataException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

@RemoteDataSource
class ArtistsRemoteDataSourceImpl @Inject constructor(
    private val api: MainNetwork
) : ArtistsRemoteDataSource {

    override suspend fun getArtist(auth: String, artistId: String): Artist =
        api.getArtistInfo(authKey = auth, artistId = artistId).toArtist()

    override suspend fun getArtists(
        auth: String,
        query: String,
        offset: Int,
        fetchAlbumsWithArtist: Boolean,
        albumsCallback: (List<Album>) -> Unit
    ): List<Artist> =
        api.getArtists(auth,
            filter = query,
            offset = offset,
            include = if (fetchAlbumsWithArtist) "albums" else null
        ).let { artistsResponse ->
            artistsResponse.error?.let { error -> throw MusicException(error.toError()) }
            artistsResponse.artists?.let { artistsDto ->
                if (fetchAlbumsWithArtist) {
                    albumsCallback(extractAlbumsFromArtists(artistsDto))
                }
                artistsDto.map { it.toArtist() }
            } ?: throw NullDataException("getArtists")
        }

    private fun extractAlbumsFromArtists(artistsDto: List<ArtistDto>): List<Album> =
        mutableListOf<Album>().apply { try {
            artistsDto.map { artistDto ->
                addAll(artistDto.albums?.filterNotNull()?.map { albumDto -> albumDto.toAlbum() }
                        ?: emptyList())
            }
        } catch (saveAlbumsException: Exception) { saveAlbumsException.printStackTrace() }
    }

    override suspend fun getArtistsByGenre(
        auth: String,
        genreId: String,
        offset: Int
    ): List<Artist> =
        api.getArtistsByGenre(authKey = auth, filter = genreId, offset = offset).let { artistsResponse ->
            artistsResponse.error?.let { error -> throw MusicException(error.toError()) }
            artistsResponse.artists?.let {
                artistsDto -> artistsDto.map { it.toArtist() }
            } ?: throw NullDataException("getArtistsByGenre")
        }

    override suspend fun likeArtist(auth: String, id: String, like: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    @Throws(MusicException::class, NullDataException::class, NullPointerException::class)
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