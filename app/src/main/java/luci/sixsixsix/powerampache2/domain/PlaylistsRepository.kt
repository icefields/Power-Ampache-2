package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.models.Playlist
import retrofit2.http.Query

interface PlaylistsRepository {

    val playlistsLiveData: LiveData<List<Playlist>>

    suspend fun getPlaylists(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Playlist>>>
    suspend fun likePlaylist(id: String, like: Boolean): Flow<Resource<Any>>

    // playlist_add_song
    suspend fun addSongToPlaylist(playlistId: String, songId: String): Flow<Resource<Any>>
    // playlist_remove_song
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Flow<Resource<Any>>

    // playlist_create
    suspend fun createNewPlaylist(name: String, playlistType: MainNetwork.PlaylistType): Flow<Resource<Playlist>>
    // playlist_delete
    suspend fun deletePlaylist(id: String): Flow<Resource<Any>>
    // playlist_edit
    suspend fun editPlaylist(playlistId: String, owner: String? = null,
                             items: String? = null, tracks: String? = null,
                             playlistType: MainNetwork.PlaylistType): Flow<Resource<Any>>

    suspend fun likeAlbum(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>>

}
