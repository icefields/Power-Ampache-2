package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist

interface ArtistsRepository {
    suspend fun getArtist(artistId: String, fetchRemote: Boolean = true): Flow<Resource<Artist>>
    suspend fun getArtists(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Artist>>>
}