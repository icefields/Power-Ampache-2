package luci.sixsixsix.powerampache2.presentation.main

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.artists.ArtistEvent
import luci.sixsixsix.powerampache2.presentation.artists.ArtistsState
import luci.sixsixsix.powerampache2.presentation.song_detail.SongDetailEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val application: Application,
    private val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application) {

    var state by mutableStateOf(MainState())
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            playlistManager.currentSongState.collect { songState ->
                L("MainViewModel collect ${songState.song}")
                // this is used to update the UI
                songState.song?.let {
                    state = state.copy(song = it)
                }
            }
        }

        viewModelScope.launch {
            playlistManager.errorMessageState.collect { errorState ->
                errorState.errorMessage?.let {
                    state = state.copy(errorMessage = it)
                }

                L("MainViewModel collect errorState ${errorState.errorMessage}")
            }
        }
    }


    fun onEvent(event: MainEvent) {
        when(event) {
            is MainEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(2000L)
                    playlistManager.updateSearchQuery(event.query)
                }
            }
            is MainEvent.Play -> {
                L( "MainViewModel Play ${event.song}")
                launchVLC(song = event.song)
            }
            MainEvent.OnDismissErrorMessage -> {
                // this will update the state in MainViewModel because we're listening to changes to
                // variable
                playlistManager.updateErrorMessage("")
            }
        }
    }

    /**
     * launches VLC media player
     * other options:
     * - vlcIntent.component = ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity")
     * - vlcIntent.putExtra("from_start", false)
     * - vlcIntent.putExtra("position", 90000L)
     * - vlcIntent.putExtra("subtitles_location", "/sdcard/Movies/Fifty-Fifty.srt")
     */
    private fun launchVLC(song: Song) {
        Toast.makeText(application, "${song.mime} ${song.songUrl}", Toast.LENGTH_LONG).show()
        L("launchVLC ${song.mime} ${song.songUrl}")
        val uri: Uri = Uri.parse(song.songUrl)
        val vlcIntent = Intent(Intent.ACTION_VIEW)
        vlcIntent
            .setPackage("org.videolan.vlc")
            .setDataAndTypeAndNormalize(uri, song.mime ?:"audio/*")
            .putExtra("title", song?.title)
            .flags = Intent.FLAG_ACTIVITY_NEW_TASK
        application.startActivity(vlcIntent)
    }
}
