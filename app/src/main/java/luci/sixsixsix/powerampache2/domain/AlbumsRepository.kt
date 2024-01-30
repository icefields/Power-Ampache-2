package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Album

interface AlbumsRepository {
    suspend fun getAlbums(fetchRemote: Boolean = true, query: String = "", offset: Int = 0, limit: Int = 0): Flow<Resource<List<Album>>>
    suspend fun getAlbumsFromArtist(artistId: String, fetchRemote: Boolean = true): Flow<Resource<List<Album>>>
    suspend fun getRecentAlbums(): Flow<Resource<List<Album>>>
    suspend fun getNewestAlbums(): Flow<Resource<List<Album>>>
    suspend fun getHighestAlbums(): Flow<Resource<List<Album>>>
    suspend fun getFrequentAlbums(): Flow<Resource<List<Album>>>
    suspend fun getFlaggedAlbums(): Flow<Resource<List<Album>>>
    suspend fun getRandomAlbums(): Flow<Resource<List<Album>>>
    suspend fun getAlbum(albumId: String, fetchRemote: Boolean, ): Flow<Resource<Album>>
    suspend fun getAlbumShareLink(albumId: String): Flow<Resource<String>>
}
