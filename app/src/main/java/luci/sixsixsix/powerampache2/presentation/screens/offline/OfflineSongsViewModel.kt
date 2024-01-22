package luci.sixsixsix.powerampache2.presentation.screens.offline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class OfflineSongsViewModel @Inject constructor(
    private val repository: SongsRepository,
    private val playlistManager: MusicPlaylistManager
) : ViewModel() {
    var state by mutableStateOf(OfflineSongsState())

    init {
        state = state.copy(isLoading = true)
        repository.offlineSongsLiveData.observeForever { songs ->
            state = state.copy(songs = songs, isLoading = false)
            // TODO check consistency of downloaded songs and database entries every time, delete data accordingly
        }
    }

    fun onEvent(event: OfflineSongsEvent) {
        when(event) {
            is OfflineSongsEvent.OnSongSelected -> {
                L("OfflineSongsEvent.OnSongSelected", event.song)
                playlistManager.updateTopSong(event.song) }
        }
    }
}
