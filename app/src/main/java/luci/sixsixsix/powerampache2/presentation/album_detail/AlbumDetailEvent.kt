package luci.sixsixsix.powerampache2.presentation.album_detail

import luci.sixsixsix.powerampache2.domain.models.Song


sealed class AlbumDetailEvent {
    data object Refresh: AlbumDetailEvent()
    data class Fetch(val albumId: String): AlbumDetailEvent()
    data class OnSongSelected(val song: Song): AlbumDetailEvent()

}
