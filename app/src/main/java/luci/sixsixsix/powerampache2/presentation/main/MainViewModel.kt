package luci.sixsixsix.powerampache2.presentation.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.toMediaItem
import luci.sixsixsix.powerampache2.player.PlayerEvent
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import luci.sixsixsix.powerampache2.player.SimpleMediaState
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val playlistManager: MusicPlaylistManager,
    private val musicRepository: MusicRepository,
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler
) : AndroidViewModel(application) {

    var state by mutableStateOf(MainState())
    private var searchJob: Job? = null

    var duration by mutableLongStateOf(0L)
    var progress by mutableFloatStateOf(0f)
    var progressStr by mutableStateOf("00:00")
    var isPlaying by mutableStateOf(false)

    init {
        L()
        viewModelScope.launch {
            playlistManager.currentSongState.collect { songState ->
                L(songState.song)
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

                L(errorState.errorMessage)
            }
        }

        viewModelScope.launch {
            playlistManager.currentQueueState.collect { queue ->
                L("queue:", queue)
                // this is used to update the UI
                state = state.copy(queue = queue)
                loadSongData()
            }
        }

        viewModelScope.launch {
            simpleMediaServiceHandler.simpleMediaState.collect { mediaState ->
                when (mediaState) {
                    is SimpleMediaState.Buffering -> calculateProgressValue(mediaState.progress)
                    SimpleMediaState.Initial -> {} // UI STATE Initial
                    is SimpleMediaState.Playing -> isPlaying = mediaState.isPlaying
                    is SimpleMediaState.Progress -> calculateProgressValue(mediaState.progress)
                    is SimpleMediaState.Ready -> {
                        duration = mediaState.duration
                        // UI STATE READY
                    }
                }
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
            is MainEvent.Play -> viewModelScope.launch {
                L( "MainEvent.Play", event.song)
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.ForcePlay(event.song.toMediaItem()))
            }
            MainEvent.PlayPauseCurrent -> state.song?.let {
                viewModelScope.launch {
                    L( "MainEvent.PlayCurrent", it)
                    simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
                }
            }
            MainEvent.OnDismissErrorMessage -> playlistManager.updateErrorMessage("")
            MainEvent.OnLogout -> logout()
            is MainEvent.OnAddSongToQueueNext -> playlistManager.addToCurrentQueueNext(event.song)
            is MainEvent.OnAddSongToQueue -> playlistManager.addToCurrentQueue(event.song)
            is MainEvent.OnAddSongToPlaylist -> {}
            is MainEvent.OnDownloadSong -> {}
            is MainEvent.OnShareSong -> {}
            is MainEvent.Repeat -> TODO()
            is MainEvent.Shuffle -> TODO()
            is MainEvent.SkipNext -> viewModelScope.launch {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.SkipForward)
            }
            is MainEvent.SkipPrevious -> viewModelScope.launch {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.SkipBack)
            }
            is MainEvent.UpdateProgress -> viewModelScope.launch {
                progress = event.newProgress
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Progress(event.newProgress))
            }
            MainEvent.Backwards -> viewModelScope.launch {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            }
            MainEvent.Forward -> viewModelScope.launch {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            }
        }
    }

    private fun logout() {
        L( " Logout")
        playlistManager.reset()

        viewModelScope.launch {
            musicRepository.logout().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { auth ->
                            L(auth)
                        }
                    }
                    is Resource.Error ->  L("MainViewModel", result.exception)
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress = if (currentProgress > 0) (currentProgress.toFloat() / duration) else 0f
        progressStr = formatDuration(currentProgress)
    }

    private fun formatDuration(duration: Long): String {
        val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds: Long = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun loadSongData() {
        val mediaItemList = mutableListOf<MediaItem>()
        for (song in state.queue) {
            mediaItemList.add(song.toMediaItem())
        }
        simpleMediaServiceHandler.addMediaItemList(mediaItemList)
    }

    override fun onCleared() {
        viewModelScope.launch {
            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
        L("onCleared")
        super.onCleared()
    }
}
