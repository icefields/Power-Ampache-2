package luci.sixsixsix.powerampache2.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song
import retrofit2.http.Query

interface PlaylistsRepository {

    val playlistsLiveData: LiveData<List<Playlist>>

    suspend fun getPlaylists(fetchRemote: Boolean = true, query: String = "", offset: Int = 0): Flow<Resource<List<Playlist>>>
    suspend fun likePlaylist(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun addSongToPlaylist(playlistId: String, songId: String): Flow<Resource<Any>>
    suspend fun addSongsToPlaylist(playlist: Playlist, songsToAdd: List<Song>): Flow<Resource<Any>>
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Flow<Resource<Any>>
    suspend fun createNewPlaylist(name: String, playlistType: MainNetwork.PlaylistType): Flow<Resource<Playlist>>
    suspend fun createNewPlaylistAddSongs(name: String, playlistType: MainNetwork.PlaylistType, songsToAdd: List<Song>): Flow<Resource<Playlist>>
    suspend fun deletePlaylist(id: String): Flow<Resource<Any>>
    suspend fun editPlaylist(
        playlistId: String,
        playlistName: String? = null,
        items: List<Song> = listOf(),
        owner: String? = null,
        tracks: String? = null,
        playlistType: MainNetwork.PlaylistType = MainNetwork.PlaylistType.private
    ): Flow<Resource<Any>>
    suspend fun likeAlbum(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>>
    suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>>
}
