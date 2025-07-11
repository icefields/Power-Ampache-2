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
package luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util.startForegroundService
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.LOCAL_SCROBBLE_TIMEOUT_MS
import luci.sixsixsix.powerampache2.common.Constants.PLAY_LOAD_TIMEOUT
import luci.sixsixsix.powerampache2.common.Constants.SERVICE_STOP_TIMEOUT
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.shareLink
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.common.WeakContext
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.isFavourite
import luci.sixsixsix.powerampache2.domain.models.toMediaItem
import luci.sixsixsix.powerampache2.domain.usecase.DownloadSongUseCase
import luci.sixsixsix.powerampache2.domain.usecase.SessionFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.GetLocalSettingsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.IsOfflineModeEnabledUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.OfflineModeFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.ToggleOfflineModeUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.IsSongAvailableOfflineUseCase
import luci.sixsixsix.powerampache2.domain.utils.ShareManager
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.player.PlayerEvent
import luci.sixsixsix.powerampache2.player.RepeatMode
import luci.sixsixsix.powerampache2.player.SimpleMediaService
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import javax.inject.Inject
import kotlin.math.abs

@UnstableApi
@kotlin.OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    val weakContext: WeakContext,
    val playlistManager: MusicPlaylistManager,
    val downloadSongUseCase: DownloadSongUseCase,
    val localSettingsUseCase: GetLocalSettingsUseCase,
    val isOfflineModeEnabledUseCase: IsOfflineModeEnabledUseCase,
    val toggleOfflineMode: ToggleOfflineModeUseCase,
    val offlineModeFlowUseCase: OfflineModeFlowUseCase,
    val sessionFlowUseCase: SessionFlowUseCase,
    val isSongAvailableOfflineUseCase: IsSongAvailableOfflineUseCase,
    val musicRepository: MusicRepository,
    val songsRepository: SongsRepository,
    val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    val shareManager: ShareManager,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) /*, MainQueueManager*/ {
    var state by savedStateHandle.saveable { mutableStateOf(MainState()) }

    val notificationQueueEmptyState = playlistManager.notificationsListStateFlow
        .map { it.isEmpty() }
        .distinctUntilChanged()

    var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    var progressStr by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var isBuffering by savedStateHandle.saveable { mutableStateOf(false) }
    var isLoading by savedStateHandle.saveable { mutableStateOf(false) }
    var shuffleOn by savedStateHandle.saveable { mutableStateOf(false) }
    var repeatMode by savedStateHandle.saveable { mutableStateOf(RepeatMode.OFF) }

    // auth token used to figure out if the media items should be refreshed
    var authToken by savedStateHandle.saveable { mutableStateOf("") }

    var loadSongDataJob: Job? = null
    private var playLoadingJob: Job? = null
    var searchJob: Job? = null
    private var scrobbleJob: Job? = null
    private var downloadAfterPlaybackJob: Job? = null
    private var deepLinkJob: Job? = null

    private var startMusicServiceCalled = false // ensure called only once

    var emittedDownloads by savedStateHandle.saveable { mutableStateOf(listOf<String>()) }

    // TODO: there is no queue to restore! because the queue is in MusicPlaylistManager
    var restoredSong: Song? = null
    var restoredQueue = listOf<Song>()

    val mainLock = Any()

    private val serviceIntent = Intent(application, SimpleMediaService::class.java)

    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    init {
        L("SERVICE- MainViewModel init")
        isPlaying = simpleMediaServiceHandler.isPlaying()
        observePlaylistManager()
        observePlayerEvents()
        observeSession()
        observeDownloads(application)

        if (SimpleMediaService.isRunning) {
            // initialize the controllers if returning from killed state and service running
            initController(application)
        }
    }

    private fun initController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, SimpleMediaService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                controller = controllerFuture?.get()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun releaseController() {
        controller?.release()
        controller = null
        controllerFuture?.cancel(true)
        controllerFuture = null
    }

    fun currentQueue() = playlistManager.currentQueueState
    fun currentSongStateFlow() = playlistManager.currentSongState
    fun currentSong() = playlistManager.currentSongState.value

    fun onEvent(event: MainEvent) =
        weakContext.get()?.applicationContext?.let { handleEvent(event, it) }

    fun isOfflineSong(song: Song, callback: (Boolean) -> Unit) = viewModelScope.launch {
        callback(isSongAvailableOfflineUseCase(song))
    }

    /**
     * set isPlayLoading to true, the play button is listening to this variable
     */
    fun startPlayLoading() {
        if (!state.isPlayLoading)
            state = state.copy(isPlayLoading = true)
        playLoadingJob?.cancel()
        // safety net, stop loading view after timeout
        playLoadingJob = viewModelScope.launch {
            delay(PLAY_LOAD_TIMEOUT)
            stopPlayLoading()
        }
    }

    fun stopPlayLoading() {
        if (state.isPlayLoading)
            state = state.copy(isPlayLoading = false)
    }

    /**
     * useful to disallow play actions during loading and buffering
     */
    fun isPlayLoading() =
        state.isPlayLoading

    fun getSongsForQuickPlay() = viewModelScope.launch {
        songsRepository.getSongsForQuickPlay().collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { songs ->
                        if (songs.isNotEmpty()) {
                            addSongsToQueueAndPlay(songs[0], songs)
                        }
                    }
                }

                is Resource.Error -> state = state.copy(isFabLoading = false)
                is Resource.Loading -> state = state.copy(isFabLoading = result.isLoading)
            }
        }
    }

    fun favouriteSong(song: Song) = viewModelScope.launch {
        songsRepository.likeSong(song.mediaId, (song.flag != 1)).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    // refresh song
                    playlistManager.updateCurrentSong(song.copy(flag = abs(song.flag - 1)))
                }

                is Resource.Error -> state = state.copy(isLikeLoading = false)
                is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
            }
        }
    }

    fun rateSong(song: Song, rate: Int) = viewModelScope.launch {
        songsRepository.rateSong(song.mediaId, rate).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    // refresh song
                    playlistManager.updateCurrentSong(song.copy(rating = rate.toFloat()))
                }

                is Resource.Error -> state = state.copy(isLikeLoading = false)
                is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
            }
        }
    }

    fun onDeepLink(type: String, id: String, title: String, artist: String, webLink: String) {
        if (deepLinkJob == null) {
            deepLinkJob = viewModelScope.launch {
                // wait for a session
                // TODO: is this the best way? (probably not)
                sessionFlowUseCase().filterNotNull().first()
                when (type) {
                    "song" -> {

                        // TODO: this is a hack! Wait for loading finished the proper way!
                        delay(2000)

                        withContext(Dispatchers.Main) {
                            playDeepLinkedSong(id, title, artist, webLink)
                        }
                        deepLinkJob?.cancel()
                        deepLinkJob = null
                    }

                    "album" -> {}
                    "playlist" -> {}
                    else -> {}
                }
            }
        }
    }

    private suspend fun playDeepLinkedSong(id: String, title: String, artist: String, webLink: String) {
        shareManager.fetchDeepLinkedSong(id, title, artist,
            songCallback = {
                onEvent(MainEvent.PlaySongAddToQueueTop(it, currentQueue().value))
            },
            songsCallback = {
                onEvent(MainEvent.AddSongsToQueueAndPlayShuffled(it))
            },
            errorCallback = {
                weakContext.get()?.let { context ->
                    if (webLink.isNotBlank()) {
                        context.shareLink(webLink)
                    } else {
                        Toast.makeText(context, context.getString(R.string.share_song_cannot_find), Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    fun downloadSong(song: Song) = viewModelScope.launch {
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

    fun downloadSongs(songs: List<Song>) {
        viewModelScope.launch { songsRepository.downloadSongs(songs) }
    }

    fun deleteDownloadedSong(song: Song) = viewModelScope.launch {
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

    fun logout() {
        viewModelScope.launch {
            if (isOfflineModeEnabledUseCase()) {
                L(" isOfflineModeEnabled")
                playlistManager.updateUserMessage(weakContext.get()?.resources?.getString(R.string.logout_offline_warning))
            } else {
                L(" Logout")
                playlistManager.reset()
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
                stopMusicService()
                musicRepository.logout().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { auth ->
                                L(auth)
                            }
                        }

                        is Resource.Error ->
                            L.e("MainViewModel", result.exception)

                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    fun loadSongData() {
        logToErrorLogs("Load song data ")
        loadSongDataJob?.cancel()
        loadSongDataJob = viewModelScope.launch {
            isLoading = true
            //isBuffering = true
            state = state.copy(isFabLoading = true)
            logToErrorLogs("Load song data START")

            val mediaItemList = mutableListOf<MediaItem>()
            for (song: Song? in playlistManager.currentQueueState.value) {
                song?.let {
                    mediaItemList.add(it.toMediaItem(songsRepository.getSongUri(it)))
                }
            }

            logToErrorLogs("Load song data before addMediaItemList")
            simpleMediaServiceHandler.addMediaItemList(mediaItemList)
            logToErrorLogs("Load song data END")

            isLoading = false
            //isBuffering = false
            state = state.copy(isFabLoading = false)
        }
    }

    @OptIn(UnstableApi::class)
    fun startMusicServiceIfNecessary() {
        if(!SimpleMediaService.isRunning && !startMusicServiceCalled) {
            L("SERVICE- startMusicServiceIfNecessary")
            weakContext.get()?.applicationContext?.let { applicationContext ->
                startForegroundService(applicationContext, serviceIntent)
                initController(applicationContext)
                startMusicServiceCalled = true
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun stopService() {
        L("SERVICE- stopMusicService isRunning: ${SimpleMediaService.isRunning}")

        if (!SimpleMediaService.isRunning) return
        releaseController()

        weakContext.get()?.applicationContext?.let { applicationContext ->
            try {
                L("SERVICE- stopMusicService")
                applicationContext.stopService(serviceIntent)
                startMusicServiceCalled = false
            } catch (e: Exception) {
                startMusicServiceCalled = false
                L.e(e, "SERVICE-")
            }
        }
    }

    fun stopMusicService(addDelay: Boolean = true) {
        if (addDelay) {
            viewModelScope.launch {
                delay(SERVICE_STOP_TIMEOUT) // safety net, delay stopping the service in case the application just got restored from background
                stopService()
            }
        } else {
            stopService()
        }
    }

    fun nextRepeatMode(): RepeatMode =
        when (repeatMode) {
            RepeatMode.OFF -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.OFF
        }

    fun scrobble(song: Song) {
        scrobbleJob?.cancel()
        scrobbleJob = viewModelScope.launch {
            delay(LOCAL_SCROBBLE_TIMEOUT_MS) // add song to history after 30s
            songsRepository.addToHistory(song)

            // send scrobble to backend
            songsRepository.scrobble(song).collect { response ->
                when (response) {
                    is Resource.Error -> { }
                    is Resource.Loading -> { }
                    is Resource.Success -> { }
                }
            }
        }
    }

    fun downloadAfterPlayback(song: Song) {
        // do not cancel, let the previous finish download // downloadAfterPlaybackJob?.cancel()
        downloadAfterPlaybackJob = viewModelScope.launch {
            // start downloading half way
            delay((song.time.toLong() * 1000L) / 2) // add song to history after 30s

            localSettingsUseCase().let { settings ->
                if (settings.saveSongAfterPlayback) {
                    if (!settings.saveFavouriteSongAfterPlayback || song.isFavourite()) {
                        downloadSongUseCase(song.mediaId)
                    }
                }
            }
        }
    }

    /**
     * updates the error log, accessible via settings
     */
    fun logToErrorLogs(mess: String) {
        L(mess)
        if (BuildConfig.DEBUG)
            playlistManager.updateErrorLogMessage(mess)
    }

    @OptIn(UnstableApi::class)
    override fun onCleared() {
        searchJob?.cancel()
        loadSongDataJob?.cancel()
        playLoadingJob?.cancel()
        scrobbleJob?.cancel()
        searchJob = null
        loadSongDataJob = null
        playLoadingJob = null
        scrobbleJob = null

        isLoading = false
        isBuffering = false
        state = state.copy(
            isFabLoading = false,
            isDownloading = false,
            isLikeLoading = false,
            isPlayLoading = false
        )

        releaseController()

        // attempt to stop the service
//        try {
//            if (!simpleMediaServiceHandler.isPlaying()) {
//                stopMusicService(addDelay = false)
//            }
//        } catch (e: Exception) {
//            if (!isPlaying) {
//                stopMusicService(addDelay = false)
//            }
//            L.e(e)
//        }

        super.onCleared()
        logToErrorLogs("onCleared")
    }
}
