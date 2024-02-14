/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.main

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.common.Constants.RESET_QUEUE_ON_NEW_SESSION
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.exportSong
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
import org.xml.sax.ErrorHandler
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
    // auth token used to figure out if the media items should be refreshed
    var authToken by savedStateHandle.saveable { mutableStateOf("") }

    private var loadSongDataJob: Job? = null
    private var searchJob: Job? = null
    private var isServiceRunning by savedStateHandle.saveable { mutableStateOf(false) }
    private val emittedDownloads by savedStateHandle.saveable { mutableStateOf(mutableListOf<String>()) }

    private val mainLock = Any()

    init {
        L()

        isPlaying = simpleMediaServiceHandler.isPlaying()

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
                    L("observeDownloads.observeForever")
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
                                            playlistManager.updateUserMessage("${finishedSong.name} downloaded")
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
            //if (lock == null) lock = Any()
            synchronized(mainLock) {
                val oldToken = authToken
                authToken = it?.auth ?: ""
                L(oldToken, authToken)
                if (authToken.isNotBlank()) {
                    // refresh the playlist with new urls with the new token
                    // only if a queue exists
                    if (oldToken != authToken && state.queue.isNotEmpty()) {
                        //authToken = newToken
                        if (RESET_QUEUE_ON_NEW_SESSION && !isPlaying) {
                            L("REFRESH AUTH !isPlaying")
                            playlistManager.reset()
                            stopMusicService()
                        } else {
                            L("REFRESH AUTH LOAD SONGS DATA")
                            loadSongData()
                        }
                    }
                    //authToken = newToken
                } else {
                    // if sessions is null, stop service and invalidate queue and current song
                    if (state.song == null) {
                        L(" && state.song == null")
                        //if (!isPlaying)
                        //stopMusicService()
                        //playlistManager.reset() // this will trigger the observables in observePlaylistManager() and reset mainviewmodel as well
                    }
                }
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
            playlistManager.logMessageUserReadableState.collect { logMessageState ->
                logMessageState.logMessage?.let {
                    state = state.copy(errorMessage = it)
                }

                L(logMessageState.logMessage)
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

    private fun playSong(song: Song) = viewModelScope.launch {
        L( "MainEvent.Play", "playing song")
        simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.ForcePlay(song.toMediaItem(songsRepository.getSongUri(song))))
        L( "MainEvent.Play", "play song launched. AFter")
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
            is MainEvent.Play -> {
                if (loadSongDataJob?.isActive == true) {
                    L( "MainEvent.Play", "loadSongDataJob?.isActive")
                    loadSongDataJob?.invokeOnCompletion {
                        //loadSongDataJob = null
                        it?.let {
                            L.e(it)
                        } ?: run {
                            L( "MainEvent.Play", "invokeOnCompletion")
                            playSong(event.song)
                        }
                    }
                } else {
                    L( "MainEvent.Play", "play directly")
                    playSong(event.song)
                }
            }
            MainEvent.PlayPauseCurrent -> state.song?.let {
                viewModelScope.launch {
                    L( "MainEvent.PlayCurrent", it)
                    simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
                }
            }
            MainEvent.OnDismissUserMessage ->
                playlistManager.updateUserMessage("")
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

            MainEvent.Reset -> {
                try {
                    playlistManager.reset()
                    stopMusicService()
                } catch (e: Exception) {
                    L.e(e)
                }
            }
            is MainEvent.OnExportDownloadedSong -> viewModelScope.launch {
                try {
                    application.exportSong(event.song, songsRepository.getSongUri(event.song))
                } catch (e: Exception) {
                    playlistManager.updateErrorLogMessage(e.stackTraceToString())
                }
            }
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
                        playlistManager.updateUserMessage("${song.name} deleted from downloads")
                    }
                }
                is Resource.Error -> playlistManager.updateUserMessage("ERROR deleting ${song.name}")
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
        L("Load song data ")
        loadSongDataJob?.cancel()
        loadSongDataJob = viewModelScope.launch {
            L("Load song data START")
            isLoading = true
            isBuffering = true
            state = state.copy(isFabLoading = true)
            val mediaItemList = mutableListOf<MediaItem>()
            for (song: Song? in state.queue) {
                song?.let {
                    mediaItemList.add(it.toMediaItem(songsRepository.getSongUri(it)))
                }
            }
            simpleMediaServiceHandler.addMediaItemList(mediaItemList)
            logToErrorLogs("Load song data END")

            isLoading = false
            isBuffering = false
            state = state.copy(isFabLoading = false)
        }
    }

    @OptIn(UnstableApi::class)
    private fun startMusicServiceIfNecessary() {
        L("SERVICE- startMusicServiceIfNecessary. isServiceRunning? : ", isServiceRunning)
        if (!isServiceRunning) {
            logToErrorLogs("SERVICE- startMusicServiceIfNecessary")
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
        logToErrorLogs("SERVICE- stopMusicService $isServiceRunning")
        //if (isServiceRunning) {
        try {
            application.stopService(Intent(application, SimpleMediaService::class.java))
                .also { isServiceRunning = false }
        } catch (e: Exception) {
            L.e(e)
        }

        //}
    }

    fun onActivityRestart() {
        // if the link to play the song is not valid reload all the data
        // when the app goes in background without playing anything the token might be invalidated
        // in this case reload the song data
        state.song?.songUrl?.let { songUrl ->
            logToErrorLogs("1 songUrl is not null $songUrl")
            if (songUrl.startsWith("http")) {
                if (!songUrl.contains(authToken)) {
                    logToErrorLogs("2 songUrl does not contain the updated token LOADING SONG DATA NOW $authToken, $songUrl")
                    loadSongData()
                } else
                    logToErrorLogs("3 songUrl contains the updated token, not realoading data")
            } else {
                // that's a local song, check the rest of the queue
                state.queue.forEach { song ->
                    if (song.songUrl.startsWith("http") && !song.songUrl.contains(authToken)) {
                        logToErrorLogs("4 song.songUrl does not contain the updated token LOADING SONG DATA NOW $authToken, $songUrl")
                        loadSongData()
                        return
                    }
                }
            }
            logToErrorLogs("5 (songUrl is not null) reached the end of function")
        }
            ?: logToErrorLogs("6 song is null? ${(state.song == null)} . songUrl: ${state.song?.songUrl}.")
    }

    // TODO remove this after bug is fixed
    private fun logToErrorLogs(mess: String) {
        L(mess)
        if (BuildConfig.DEBUG)
            playlistManager.updateErrorLogMessage(mess)
    }

    override fun onCleared() {
        logToErrorLogs("onCleared")
        searchJob?.cancel()
        loadSongDataJob?.cancel()
        isLoading = false
        isBuffering = false
        state = state.copy(isFabLoading = false)

        // attempt to stop the service
        try {
            if (!simpleMediaServiceHandler.isPlaying()) {
                stopMusicService()
            }
        } catch (e: Exception) {
            if (!isPlaying) {
                stopMusicService()
            }
            L.e(e)
        }

        super.onCleared()
    }

    private fun nextRepeatMode(): RepeatMode =
        when(repeatMode) {
            RepeatMode.OFF -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.OFF
        }
}
