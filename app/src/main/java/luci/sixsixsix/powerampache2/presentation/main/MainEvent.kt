package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.album_detail.AlbumDetailEvent

sealed class MainEvent {
    data class OnSearchQueryChange(val query: String): MainEvent()
    data object OnDismissErrorMessage: MainEvent()
    data object OnLogout: MainEvent() // TODO move this to AuthViewModel
    data class Play(val song: Song): MainEvent()
    data object PlayCurrent: MainEvent()


    data class OnAddSongToQueue(val song: Song): MainEvent()
    data class OnAddSongToPlaylist(val song: Song): MainEvent()
    data class OnAddSongToQueueNext(val song: Song): MainEvent()
    data class OnShareSong(val song: Song): MainEvent()
    data class OnDownloadSong(val song: Song): MainEvent()

}
