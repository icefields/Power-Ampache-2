package luci.sixsixsix.powerampache2.presentation.album_detail

import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MainEvent


sealed class AlbumDetailEvent {
    data class Fetch(val albumId: String): AlbumDetailEvent()
    data class OnSongSelected(val song: Song): AlbumDetailEvent()
    data object OnPlayAlbum: AlbumDetailEvent()
    data object OnShareAlbum: AlbumDetailEvent()
    data object OnDownloadAlbum: AlbumDetailEvent()
    data object OnShuffleAlbum: AlbumDetailEvent()
    data object OnAddAlbumToQueue: AlbumDetailEvent()
    data object OnFavouriteAlbum: AlbumDetailEvent()
}
