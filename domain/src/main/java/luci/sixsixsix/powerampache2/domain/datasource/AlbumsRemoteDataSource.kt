package luci.sixsixsix.powerampache2.domain.datasource

import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AlbumSortOrder
import luci.sixsixsix.powerampache2.domain.models.SortOrder

interface AlbumsRemoteDataSource {
    suspend fun getAlbums(
        auth: String,
        query: String = "",
        offset: Int = 0,
        limit: Int,
        sort: AlbumSortOrder,
        order: SortOrder
    ): List<Album>

    suspend fun getAlbumFromId(auth: String, albumId: String): Album
}
