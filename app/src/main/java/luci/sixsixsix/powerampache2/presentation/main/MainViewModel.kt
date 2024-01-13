package luci.sixsixsix.powerampache2.presentation.main

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util.startForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.toMediaItem
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.player.PlayerEvent
import luci.sixsixsix.powerampache2.player.RepeatMode
import luci.sixsixsix.powerampache2.player.SimpleMediaService
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import luci.sixsixsix.powerampache2.player.SimpleMediaState
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val playlistManager: MusicPlaylistManager,
    private val playlistsRepository: PlaylistsRepository,
    private val musicRepository: MusicRepository,
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler
) : AndroidViewModel(application) {

    var state by mutableStateOf(MainState())
        private set
    private var searchJob: Job? = null
    private var isServiceRunning = false

    var duration by mutableLongStateOf(0L)
    var progress by mutableFloatStateOf(0f)
    var progressStr by mutableStateOf("00:00")
    var isPlaying by mutableStateOf(false)
    var isBuffering by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var shuffleOn by mutableStateOf(false)
    var repeatMode by mutableStateOf(RepeatMode.OFF)

    init {
        L()
        observePlaylistManager()
        observePlayerEvents()
        observeSession()
    }

    private fun observeSession() {
        musicRepository.sessionLiveData.observeForever {
            // if sessions is null, stop service and invalidate queue and current song
            if (it == null) {
                if (!isPlaying) stopMusicService()
                playlistManager.reset() // this will trigger the observables in observePlaylistManager() and reset mainviewmodel as well
            }
        }
    }

    private fun observePlaylistManager() {
        viewModelScope.launch {
            playlistManager.currentSongState.collect { songState ->
                songState.song?.let {
                    startMusicServiceIfNecessary()
                } ?: stopMusicService()
                // this is used to update the UI
                //songState.song?.let {
                    state = state.copy(song = songState.song)
                //}
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
            playlistManager.currentQueueState.collect { q ->
                val queue = q.filterNotNull()
                if (!queue.isNullOrEmpty()) {
                    startMusicServiceIfNecessary()
                } else if (queue.isNullOrEmpty() && state.song == null) {
                    stopMusicService()
                }

                L("queue:", queue)
                // this is used to update the UI
                state = state.copy(queue = queue)
                loadSongData()
            }
        }
    }

    private fun observePlayerEvents() {
        viewModelScope.launch {
            simpleMediaServiceHandler.simpleMediaState.collect { mediaState ->
                when (mediaState) {
                    is SimpleMediaState.Buffering -> {
                        isBuffering = true
                        calculateProgressValue(mediaState.progress)
                    }
                    SimpleMediaState.Initial -> {
                    } // UI STATE Initial
                    is SimpleMediaState.Playing -> {
                        isPlaying = mediaState.isPlaying
                    }
                    is SimpleMediaState.Progress ->
                        calculateProgressValue(mediaState.progress)
                    is SimpleMediaState.Ready -> {
                        isBuffering = false
                        duration = mediaState.duration
                        // UI STATE READY
                    }

                    is SimpleMediaState.Loading -> isLoading = mediaState.isLoading
                }
            }
        }
    }

    /**
     * UI ACTIONS AND EVENTS (play, stop, skip, like, download, etc ...)
     */
    fun onEvent(event: MainEvent) {
        when(event) {
            is MainEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1500)
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
            MainEvent.OnDismissErrorMessage ->
                playlistManager.updateErrorMessage("")
            MainEvent.OnLogout ->
                logout()
            is MainEvent.OnAddSongToQueueNext ->
                playlistManager.addToCurrentQueueNext(event.song)
            is MainEvent.OnAddSongToQueue ->
                playlistManager.addToCurrentQueue(event.song)
            is MainEvent.OnAddSongToPlaylist -> {}
            is MainEvent.OnDownloadSong -> {}
            is MainEvent.OnShareSong -> {}
            is MainEvent.Repeat -> viewModelScope.launch {
                val nextRepeatMode = nextRepeatMode()
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.RepeatToggle(nextRepeatMode))
                repeatMode = nextRepeatMode
            }
            is MainEvent.Shuffle -> viewModelScope.launch {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.ShuffleToggle(event.shuffleOn))
                shuffleOn = event.shuffleOn
            }
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
            MainEvent.FavouriteSong -> state.song?.let {
                favouriteSong(it)
            }
        }
    }

    private fun favouriteSong(song: Song) = viewModelScope.launch {
        playlistsRepository.likeSong(song.mediaId, (song.flag != 1)).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh song
                            state = state.copy(song = song.copy(flag = abs(song.flag - 1)))
                        }
                    }
                    is Resource.Error -> state = state.copy(isLikeLoading = false)
                    is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
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
        for (song: Song? in state.queue) {
            song?.let {
                mediaItemList.add(it.toMediaItem())
            }
        }
        simpleMediaServiceHandler.addMediaItemList(mediaItemList)
    }

    @OptIn(UnstableApi::class)
    private fun startMusicServiceIfNecessary() {
        L("startMusicServiceIfNecessary", isServiceRunning)
        if (!isServiceRunning) {
            Intent(application, SimpleMediaService::class.java).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(application, this)
                    isServiceRunning = true
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun stopMusicService() {
        L("stopMusicService", isServiceRunning)
        if (isServiceRunning) {
            application.stopService(Intent(application, SimpleMediaService::class.java))
                .also { isServiceRunning = false }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
        stopMusicService()
        playlistManager.reset()
        L("onCleared")
        super.onCleared()
    }

    fun nextRepeatMode(): RepeatMode =
        when(repeatMode) {
            RepeatMode.OFF -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.OFF
        }
}
