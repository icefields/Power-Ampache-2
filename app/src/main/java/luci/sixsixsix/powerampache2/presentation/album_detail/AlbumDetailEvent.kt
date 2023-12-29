package luci.sixsixsix.powerampache2.presentation.album_detail

import luci.sixsixsix.powerampache2.domain.models.Song


sealed class AlbumDetailEvent {
    data object Refresh: AlbumDetailEvent()
    data class Fetch(val albumId: String): AlbumDetailEvent()
    data class OnSongSelected(val song: Song): AlbumDetailEvent()
    data object OnPlayAlbum: AlbumDetailEvent()
    data object OnShareAlbum: AlbumDetailEvent()
    data object OnDownloadAlbum: AlbumDetailEvent()
    data object OnShuffleAlbum: AlbumDetailEvent()
    data class OnAddSongToQueue(val song: Song): AlbumDetailEvent()
    data class OnAddSongToPlaylist(val song: Song): AlbumDetailEvent()
    data class OnAddSongToQueueNext(val song: Song): AlbumDetailEvent()
    data object OnAddAlbumToQueue: AlbumDetailEvent()
    data class OnShareSong(val song: Song): AlbumDetailEvent()
    data class OnDownloadSong(val song: Song): AlbumDetailEvent()
}
