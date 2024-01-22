package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail

sealed class SongDetailEvent {
    data object Play: SongDetailEvent()
}
