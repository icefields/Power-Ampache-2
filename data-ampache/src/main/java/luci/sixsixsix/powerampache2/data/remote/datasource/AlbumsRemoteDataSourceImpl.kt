package luci.sixsixsix.powerampache2.data.remote.datasource

import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAlbum
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.di.RemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.NullDataException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder
import luci.sixsixsix.powerampache2.domain.models.SortOrder
import javax.inject.Inject


@RemoteDataSource
class AlbumsRemoteDataSourceImpl @Inject constructor(
    private val api: MainNetwork
) : AlbumsRemoteDataSource {

    @Throws(MusicException::class, NullDataException::class)
    override suspend fun getAlbums(
        auth: String,
        query: String,
        offset: Int,
        limit: Int,
        sort: AlbumSortOrder,
        order: SortOrder
    ): List<Album> =
        api.getAlbums(auth, filter = query, offset = offset, limit = limit,
            sort = "${sort.columnName},${order.order}"
        ).let { albumsResponse ->
            albumsResponse.error?.let { error -> throw MusicException(error.toError()) }
            albumsResponse.albums?.let { albumsDto ->
                albumsDto.map { it.toAlbum() }
            } ?: throw NullDataException("getAlbums")
    }

    // TODO: error handling
    override suspend fun getAlbumFromId(auth: String, albumId: String) =
        api.getAlbumInfo(authKey = auth, albumId = albumId).toAlbum()
}
