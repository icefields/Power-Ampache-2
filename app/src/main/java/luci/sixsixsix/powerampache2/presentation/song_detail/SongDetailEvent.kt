package luci.sixsixsix.powerampache2.presentation.song_detail

sealed class SongDetailEvent {
    data object Play: SongDetailEvent()
}
