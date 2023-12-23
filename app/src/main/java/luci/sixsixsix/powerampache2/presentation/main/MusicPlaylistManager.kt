package luci.sixsixsix.powerampache2.presentation.main

import android.util.Log
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlaylistManager @Inject constructor() {
    private val _state = MutableStateFlow(MusicPlaylistManagerState())
    //val currentSong = _currentSong.asStateFlow()
    val state: StateFlow<MusicPlaylistManagerState> = _state

    fun updateCurrentSong(newSong: Song) {
        Log.d("aaaa", "MusicPlaylistManager updateCurrentSong $newSong")
        _state.value = MusicPlaylistManagerState( song = newSong)
    }

    fun getCurrentSong(): Song? = state.value.song
}

data class MusicPlaylistManagerState(
    var song: Song? = null
)
