package luci.sixsixsix.powerampache2.presentation.playlist_detail

import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Song

sealed class PlaylistDetailEvent {
    data object Refresh: PlaylistDetailEvent()
    data class Fetch(val playlist: Playlist): PlaylistDetailEvent()
    data class OnSongSelected(val song: Song): PlaylistDetailEvent()
    data object OnPlayPlaylist: PlaylistDetailEvent()
    data object OnSharePlaylist: PlaylistDetailEvent()
    data object OnDownloadPlaylist: PlaylistDetailEvent()
    data object OnShufflePlaylist: PlaylistDetailEvent()
}
