package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail

import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MainEvent


sealed class AlbumDetailEvent {
    data class Fetch(val albumId: String): AlbumDetailEvent()
    data class OnSongSelected(val song: Song): AlbumDetailEvent()
    data object OnPlayAlbum: AlbumDetailEvent()
    data object OnShareAlbum: AlbumDetailEvent()
    data object OnShuffleAlbum: AlbumDetailEvent()
    data object OnFavouriteAlbum: AlbumDetailEvent()
    data object RefreshFromCache: AlbumDetailEvent()
}
