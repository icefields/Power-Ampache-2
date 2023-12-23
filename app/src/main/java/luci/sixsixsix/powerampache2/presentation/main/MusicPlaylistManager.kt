package luci.sixsixsix.powerampache2.presentation.main

import android.util.Log
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject
import javax.inject.Singleton

data class CurrentSongState(val song: Song? = null)
data class ErrorMessageState(var errorMessage: String? = null)

@Singleton
class MusicPlaylistManager @Inject constructor() {
    private val _currentSongState = MutableStateFlow(CurrentSongState())
    val currentSongState: StateFlow<CurrentSongState> = _currentSongState //val currentSong = _currentSong.asStateFlow()

    private val _errorMessageState = MutableStateFlow(ErrorMessageState())
    val errorMessageState: StateFlow<ErrorMessageState> = _errorMessageState //val currentSong = _currentSong.asStateFlow()

    private val _currentSearchQuery = MutableStateFlow("")
    val currentSearchQuery: StateFlow<String> = _currentSearchQuery

    fun updateCurrentSong(newSong: Song?) {
        L( "MusicPlaylistManager updateCurrentSong $newSong")
        _currentSongState.value = CurrentSongState(song = newSong)
    }

    fun updateErrorMessage(errorMessage: String) {
        L("MusicPlaylistManager updateErrorMessage $errorMessage")
        _errorMessageState.value = ErrorMessageState(errorMessage = errorMessage)
    }

    fun updateSearchQuery(searchQuery: String) {
        L( "MusicPlaylistManager updateSearchQuery $searchQuery")
        _currentSearchQuery.value = searchQuery
    }

    fun getCurrentSong(): Song? = currentSongState.value.song

    fun getErrorMessage(): String? = errorMessageState.value.errorMessage
}
