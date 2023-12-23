package luci.sixsixsix.powerampache2.presentation.main

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.presentation.artists.ArtistEvent
import luci.sixsixsix.powerampache2.presentation.artists.ArtistsState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    application: Application,
    val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application) {

    var state by mutableStateOf(MainState())

    init {
        viewModelScope.launch {
            playlistManager.state.collect { songState ->
                Log.d("aaaa", "SongDetailViewModel collect ${songState.song}")

                songState.song?.let {
                    state = state.copy(currentSong = it)
                }
            }
        }
    }

    fun onEvent(event: MainEvent) {
        when(event) {
            is MainEvent.OnSearchQueryChange -> {
            }

            else -> {}
        }
    }
}
