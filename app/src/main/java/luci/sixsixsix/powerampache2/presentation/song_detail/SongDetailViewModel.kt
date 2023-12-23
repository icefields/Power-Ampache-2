package luci.sixsixsix.powerampache2.presentation.song_detail

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val application: Application,
    val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application) {

    var state by mutableStateOf(SongDetailState())

    init {
        viewModelScope.launch {
            playlistManager.state.collect { songState ->
                Log.d("aaaa", "SongDetailViewModel collect ${songState.song}")

                songState.song?.let {
                    state = state.copy(song = it)
                    launchVLC(it)
                }
            }
        }
    }

    fun onEvent(event: SongDetailEvent) {
        when(event) {
            is SongDetailEvent.Play -> {
                playlistManager.getCurrentSong()?.let { song ->
                    launchVLC(song)
                }
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
        Log.d("aaaa","${song.mime} ${song.songUrl}")
        val uri: Uri = Uri.parse(song.songUrl)
        val vlcIntent = Intent(Intent.ACTION_VIEW)
        vlcIntent
            .setPackage("org.videolan.vlc")
            .setDataAndTypeAndNormalize(uri, song.mime ?:"audio/*")
            .putExtra("title", song?.title)
            .flags = FLAG_ACTIVITY_NEW_TASK
        application.startActivity(vlcIntent)

    }
}
