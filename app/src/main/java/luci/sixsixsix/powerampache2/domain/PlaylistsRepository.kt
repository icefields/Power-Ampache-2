package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Playlist

interface PlaylistsRepository {
    suspend fun getPlaylists(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Playlist>>>
    suspend fun likePlaylist(id: String, like: Boolean): Flow<Resource<Any>>

    // playlist_add_song

    // playlist_create

    // playlist_delete

    // playlist_edit

    // playlist_remove_song

    suspend fun likeAlbum(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>>

}