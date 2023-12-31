package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.domain.models.Song

sealed class MainEvent {
    data class OnSearchQueryChange(val query: String): MainEvent()
    data object OnDismissErrorMessage: MainEvent()
    data object OnLogout: MainEvent() // TODO move this to AuthViewModel
    data class Play(val song: Song): MainEvent()
    data object PlayPauseCurrent: MainEvent()
    data object SkipNext: MainEvent()
    data object SkipPrevious: MainEvent()
    data object Forward: MainEvent()
    data object Backwards: MainEvent()
    data object Repeat: MainEvent()
    data object Shuffle: MainEvent()
    data class UpdateProgress(val newProgress: Float): MainEvent()


    data class OnAddSongToQueue(val song: Song): MainEvent()
    data class OnAddSongToPlaylist(val song: Song): MainEvent()
    data class OnAddSongToQueueNext(val song: Song): MainEvent()
    data class OnShareSong(val song: Song): MainEvent()
    data class OnDownloadSong(val song: Song): MainEvent()

}
