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
import dagger.hilt.android.lifecycle.HiltViewModel
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MusicRepository,
    private val application: Application
) : AndroidViewModel(application) {
    var state by mutableStateOf(SongDetailState(song = savedStateHandle.get<Song>("song")!!))

    init {
    }

    fun onEvent(event: SongDetailEvent) {
        when(event) {
            is SongDetailEvent.Play -> {
                launchVLC()
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
    private fun launchVLC() {
        Toast.makeText(application, "${state.song.mime} ${state.song.songUrl}", Toast.LENGTH_LONG).show()
        Log.d("aaaa","${state.song.mime} ${state.song.songUrl}")
        val uri: Uri = Uri.parse(state.song.songUrl)
        val vlcIntent = Intent(Intent.ACTION_VIEW)
        vlcIntent
            .setPackage("org.videolan.vlc")
            .setDataAndTypeAndNormalize(uri, state.song.mime ?:"audio/*")
            .putExtra("title", state.song.title)
            .flags = FLAG_ACTIVITY_NEW_TASK
        application.startActivity(vlcIntent)
    }
}
