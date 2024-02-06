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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util.startForegroundService
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.data.remote.worker.SongDownloadWorker
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.toMediaItem
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.player.PlayerEvent
import luci.sixsixsix.powerampache2.player.RepeatMode
import luci.sixsixsix.powerampache2.player.SimpleMediaService
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import luci.sixsixsix.powerampache2.player.SimpleMediaState
import org.acra.ACRA.init
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@kotlin.OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val playlistManager: MusicPlaylistManager,
    private val playlistsRepository: PlaylistsRepository,
    private val musicRepository: MusicRepository,
    private val songsRepository: SongsRepository,
    private val settingsRepository: SettingsRepository,
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
//    var state by mutableStateOf(MainState())
//        private set
    var state by savedStateHandle.saveable { mutableStateOf(MainState()) }

    private var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    var progressStr by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var isBuffering by savedStateHandle.saveable { mutableStateOf(false) }
    var isLoading by savedStateHandle.saveable { mutableStateOf(false) }
    var shuffleOn by savedStateHandle.saveable { mutableStateOf(false) }
    var repeatMode by savedStateHandle.saveable { mutableStateOf(RepeatMode.OFF) }

    private var searchJob: Job? = null
    private var isServiceRunning by savedStateHandle.saveable { mutableStateOf(false) }
    private val emittedDownloads = mutableListOf<String>()

    init {
        L()
        // restore song and queue if saved in statehandle
        if (state.queue.isNotEmpty()) {
            playlistManager.replaceCurrentQueue(state.queue)
        }
        state.song?.let {
            playlistManager.updateCurrentSong(it)
        }
        observePlaylistManager()
        observePlayerEvents()
        observeSession()
        observeDownloads()
    }

    private fun observeDownloads() {
        WorkManager.getInstance(application).pruneWork()
        viewModelScope.launch {
            WorkManager.getInstance(application)
                .getWorkInfosForUniqueWorkLiveData(SongDownloadWorker.getDownloadWorkerId(application))
                //.getWorkInfosForUniqueWorkFlow(SongDownloadWorker.workerName)
                //.getWorkInfoByIdFlow(requestId).mapNotNull { it.outputData.getString(KEY_RESULT_PATH) }.cancellable()
                .observeForever { workInfoList ->
                    L("observeForever")
                    var atLeastOneRunning = false
                    var atLeastOneEnqueued = false
                    var atLeastOneBlocked = false
                    var allCancelled = true // assume all cancelled, reset if if after the loop is still true
                    var allFailed = true // assume all failed, reset if if after the loop is still true
                    workInfoList.forEach { workInfo ->
                        L(workInfo.state.name)
                        if (!atLeastOneRunning && workInfo.state == WorkInfo.State.RUNNING) {
                            atLeastOneRunning = true
                        }
                        if (!atLeastOneEnqueued && workInfo.state == WorkInfo.State.ENQUEUED) {
                            atLeastOneEnqueued = true
                        }
                        if (!atLeastOneBlocked && workInfo.state == WorkInfo.State.BLOCKED) {
                            atLeastOneBlocked = true
                        }
                        if (allCancelled && workInfo.state != WorkInfo.State.CANCELLED) {
                            allCancelled = false
                        }
                        if (allFailed && workInfo.state != WorkInfo.State.FAILED) {
                            allFailed = false
                        }

                        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            workInfo?.outputData?.getString(SongDownloadWorker.KEY_RESULT_SONG)?.let { songId ->
                                viewModelScope.launch {
                                    if (!emittedDownloads.contains(songId)) {
                                        emittedDownloads.add(songId)
                                        songsRepository.getDownloadedSongById(songId)?.let { finishedSong ->
                                            //if(songsRepository.isSongAvailableOffline(song)) {}
                                            playlistManager.updateDownloadedSong(finishedSong)
                                            playlistManager.updateErrorMessage("${finishedSong.name} downloaded")
                                            state = state.copy(isDownloading = false)
                                            //WorkManager.getInstance(application).pruneWork()
                                            L("emitting", finishedSong.name)
                                        }
                                    }
                                }
                            }
                        }
//                        it?.progress?.getInt(SongDownloadWorker.KEY_PROGRESS, ERROR_INT)?.let { progress ->
//                            if (progress != ERROR_INT) {
//                                state = state.copy(isDownloading = progress in 0..99)
//                                L(progress)
//                            }
//                        }
                    }
                    state = state.copy(isDownloading = atLeastOneRunning || atLeastOneEnqueued || atLeastOneBlocked)

                    if(workInfoList.isNotEmpty() && !atLeastOneRunning && !atLeastOneBlocked && !atLeastOneEnqueued && (allCancelled || allFailed)) {
                        // no more work to be done
                        viewModelScope.launch {
                            L("resetDownloadWorkerId(application) ${SongDownloadWorker.getDownloadWorkerId(application)}")
                            SongDownloadWorker.resetDownloadWorkerId(application)
                            delay(200)
                            L("resetDownloadWorkerId(application) AFTER REFRESH ${SongDownloadWorker.getDownloadWorkerId(application)}")

                            observeDownloads()
                        }
                    }
                }
        }
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
                state = state.copy(song = songState.song)
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

                L("queue:", queue.size)
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
                        isPlaying = mediaState.isPlaying
                        calculateProgressValue(mediaState.progress)
                    }
                    SimpleMediaState.Initial -> { /* UI STATE Initial */ }
                    is SimpleMediaState.Playing ->
                        isPlaying = mediaState.isPlaying
                    is SimpleMediaState.Progress -> {
                        isPlaying = mediaState.isPlaying
                        calculateProgressValue(mediaState.progress)
                    }
                    is SimpleMediaState.Ready -> {
                        isBuffering = false
                        duration = mediaState.duration
                    }
                    is SimpleMediaState.Loading ->
                        isLoading = mediaState.isLoading
                    SimpleMediaState.Ended -> { }
                    SimpleMediaState.Idle -> {
                        isBuffering = false
                        isPlaying = false
                    }
                }
            }
        }
    }

    fun isOfflineSong(song: Song, callback: (Boolean) -> Unit) = viewModelScope.launch {
        callback(songsRepository.isSongAvailableOffline(song))
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
                L( "MainEvent.Play", event.song, songsRepository.getSongUri(event.song))
                //delay(1000)
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.ForcePlay(event.song.toMediaItem(songsRepository.getSongUri(event.song))))
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
            is MainEvent.OnDownloadSong ->
                downloadSong(event.song)
            is MainEvent.OnShareSong -> viewModelScope.launch {
                shareSong(event.song)
            }
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
            is MainEvent.OnDownloadedSongDelete ->
                deleteDownloadedSong(event.song)
            is MainEvent.OnDownloadSongs ->
                downloadSongs(event.songs)
            is MainEvent.OnStopDownloadSongs -> viewModelScope.launch {
                SongDownloadWorker.stopAllDownloads(application)
                observeDownloads()
                state = state.copy(isDownloading = false)
            }
            MainEvent.OnFabPress ->
                getSongsForQuickPlay()
        }
    }

    private fun getSongsForQuickPlay() = viewModelScope.launch {
        songsRepository.getSongsForQuickPlay().collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { songs ->
                        playlistManager.updateCurrentSong(songs[0])
                        playlistManager.addToCurrentQueueTop(songs)
                        onEvent(MainEvent.Play(songs[0]))
                    }
                }
                is Resource.Error -> state = state.copy(isFabLoading = false)
                is Resource.Loading -> state = state.copy(isFabLoading = result.isLoading)
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

    private fun shareSong(song: Song) = viewModelScope.launch {
        songsRepository.getSongShareLink(song).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    application.shareLink(it)
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }
    }

    private fun downloadSong(song: Song) = viewModelScope.launch {
        songsRepository.downloadSong(song).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        // song download started successfully
                    }
                }
                is Resource.Error -> state = state.copy(isDownloading = false)
                is Resource.Loading -> state = state.copy(isDownloading = result.isLoading)
            }
        }
    }

    private fun downloadSongs(songs: List<Song>) = viewModelScope.launch {
        songsRepository.downloadSongs(songs)
    }

    //private fun downloadSongs(songs: List<Song>) = songs.forEach { song -> downloadSong(song) }

    private fun deleteDownloadedSong(song: Song) = viewModelScope.launch {
        songsRepository.deleteDownloadedSong(song).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        // song deleted
                        playlistManager.updateErrorMessage("${song.name} deleted from downloads")
                    }
                }
                is Resource.Error -> playlistManager.updateErrorMessage("ERROR deleting ${song.name}")
                is Resource.Loading -> {}
            }
        }
    }

    private fun logout() {
        L( " Logout")
        playlistManager.reset()
        viewModelScope.launch {
            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
        stopMusicService()
//        playlistManager.reset()
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
        if (duration <= 0L) duration = (state.song?.time?.toLong() ?: 1) * 1000
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
                // TODO FIX runBlocking (this functions has to be executed before calling play)
                //  run blocking makes sure the call is sequential. FIND A BETTER SOLUTION.
                //  test: with 10000 songs in queue, thread blocked 1.4s on pixel 6a
                runBlocking {
                    mediaItemList.add(it.toMediaItem(songsRepository.getSongUri(it)))
                }
            }
        }
        simpleMediaServiceHandler.addMediaItemList(mediaItemList)
    }

    @OptIn(UnstableApi::class)
    private fun startMusicServiceIfNecessary() {
        L("SERVICE- startMusicServiceIfNecessary. isServiceRunning? : ", isServiceRunning)
        if (!isServiceRunning) {
            L("SERVICE- startMusicServiceIfNecessary")
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
        L("SERVICE- stopMusicService", isServiceRunning)
        if (isServiceRunning) {
            application.stopService(Intent(application, SimpleMediaService::class.java))
                .also { isServiceRunning = false }
        }
    }

    override fun onCleared() {
//        viewModelScope.launch {
//            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
//        }
//        stopMusicService()
//        playlistManager.reset()
        L("onCleared")
        super.onCleared()
    }

    private fun nextRepeatMode(): RepeatMode =
        when(repeatMode) {
            RepeatMode.OFF -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.OFF
        }
}
