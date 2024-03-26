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
package luci.sixsixsix.powerampache2.presentation.main.viewmodel

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.WeakContext
import luci.sixsixsix.powerampache2.common.shareLink
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
import javax.inject.Inject
import kotlin.math.abs

@kotlin.OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val weakContext: WeakContext,
    val playlistManager: MusicPlaylistManager,
    val musicRepository: MusicRepository,
    val songsRepository: SongsRepository,
    val settingsRepository: SettingsRepository,
    val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) /*, MainQueueManager*/ {
//    var state by mutableStateOf(MainState())
//        private set
    var state by savedStateHandle.saveable { mutableStateOf(MainState()) }

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
    var searchJob: Job? = null

    //private var isServiceRunning by savedStateHandle.saveable { mutableStateOf(false) }
    private var isServiceRunning = false
    var emittedDownloads by savedStateHandle.saveable { mutableStateOf(listOf<String>()) }

    // TODO: there is no queue to restore! because the queue is in MusicPlaylistManager
    var restoredSong: Song? = null
    var restoredQueue = listOf<Song>()

    val mainLock = Any()

    init {
        L()
        // TODO: there is no queue or song to restore! because the queue is in MusicPlaylistManager
        // restoredSong = state.song
        //restoredQueue = state.queue

        isPlaying = simpleMediaServiceHandler.isPlaying()
        observePlaylistManager()
        observePlayerEvents()
        observeSession()
        observeDownloads(application)
    }

    fun currentQueue() = playlistManager.currentQueueState
    fun currentSongStateFlow() = playlistManager.currentSongState
    fun currentSong() = playlistManager.currentSongState.value

    fun onEvent(event: MainEvent) =
        weakContext.get()?.applicationContext?.let { handleEvent(event, it) }

    fun isOfflineSong(song: Song, callback: (Boolean) -> Unit) = viewModelScope.launch {
        callback(songsRepository.isSongAvailableOffline(song))
    }

    fun getSongsForQuickPlay() = viewModelScope.launch {
        songsRepository.getSongsForQuickPlay().collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { songs ->
                        if (songs.isNotEmpty()) {
                            addSongsToQueueAndPlay(songs[0], songs)
//                            playlistManager.updateCurrentSong(songs[0])
//                            playlistManager.addToCurrentQueueTop(songs)
//                            onEvent(MainEvent.Play(songs[0]))
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
                is Resource.Success -> {
                    result.data?.let {
                        // refresh song
                        playlistManager.updateCurrentSong(song.copy(flag = abs(song.flag - 1)))
                        //state = state.copy(song = song.copy(flag = abs(song.flag - 1)))
                    }
                }
                is Resource.Error -> state = state.copy(isLikeLoading = false)
                is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
            }
        }
    }

    fun rateSong(song: Song, rate: Int) = viewModelScope.launch {
        songsRepository.rateSong(song.mediaId, rate).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        // refresh song
                        playlistManager.updateCurrentSong(song.copy(rating = rate.toFloat()))
                        // state = state.copy(song = song.copy(rating = rate.toFloat()))
                    }
                }
                is Resource.Error -> state = state.copy(isLikeLoading = false)
                is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
            }
        }
    }

    fun shareSong(song: Song) = viewModelScope.launch {
        songsRepository.getSongShareLink(song).collect { result ->
            when (result) {
                is Resource.Success -> result.data?.let {
                    weakContext.get()?.shareLink(it)
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }
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

    fun downloadSongs(songs: List<Song>) =
        viewModelScope.launch { songsRepository.downloadSongs(songs) }

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
        L( " Logout")
        playlistManager.reset()
        viewModelScope.launch {
            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
        stopMusicService()
        viewModelScope.launch {
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

    fun loadSongData() {
        logToErrorLogs("Load song data ")
        loadSongDataJob?.cancel()
        loadSongDataJob = viewModelScope.launch {
            isLoading = true
            isBuffering = true
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
            isBuffering = false
            state = state.copy(isFabLoading = false)
        }
    }

    @OptIn(UnstableApi::class)
    fun startMusicServiceIfNecessary() {
        logToErrorLogs("SERVICE- startMusicServiceIfNecessary. isServiceRunning? : $isServiceRunning")
        if (!isServiceRunning) {
            weakContext.get()?.applicationContext?.let { applicationContext ->
                Intent(applicationContext, SimpleMediaService::class.java).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        isServiceRunning = true
                        startForegroundService(applicationContext, this)
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun stopMusicService() = viewModelScope.launch {
        delay(2000) // safety net, delay stopping the service in case the application just got restored from background
        logToErrorLogs("SERVICE- stopMusicService $isServiceRunning")
        weakContext.get()?.applicationContext?.let { applicationContext ->
            try {
                applicationContext.stopService(Intent(applicationContext, SimpleMediaService::class.java))
                    .also { isServiceRunning = false }
            } catch (e: Exception) {
                L.e(e)
                isServiceRunning = false
            }
        }
    }

    // TODO remove this after bug is fixed
    fun logToErrorLogs(mess: String) {
        L(mess)
        if (BuildConfig.DEBUG)
            playlistManager.updateErrorLogMessage(mess)
    }

    fun nextRepeatMode(): RepeatMode =
        when(repeatMode) {
            RepeatMode.OFF -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.OFF
        }

    override fun onCleared() {
        logToErrorLogs("onCleared")
        searchJob?.cancel()
        loadSongDataJob?.cancel()
        searchJob = null
        loadSongDataJob = null
        isLoading = false
        isBuffering = false
        state = state.copy(isFabLoading = false, isDownloading = false, isLikeLoading = false)

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

    @Deprecated("completely rewrite before use, those cases might not exist anymore")
    fun onActivityRestart() {
        // if the link to play the song is not valid reload all the data
        // when the app goes in background without playing anything the token might be invalidated
        // in this case reload the song data
        currentSong()?.songUrl?.let { songUrl ->
            logToErrorLogs("1.a songUrl is not null $songUrl")
            if (songUrl.startsWith("http")) {
                if (!songUrl.contains(authToken)) {
                    logToErrorLogs("2 songUrl does not contain the updated token LOADING SONG DATA NOW $authToken, $songUrl")
                    loadSongData()
                } else
                    logToErrorLogs("3 songUrl contains the updated token, not realoading data")
            } else {
                logToErrorLogs("1.b songUrl NULL, looping queue, size: ${playlistManager.currentQueueState.value.size}")
                // that's a local song, check the rest of the queue
                playlistManager.currentQueueState.value.forEach { song ->
                    if (song.songUrl.startsWith("http") && !song.songUrl.contains(authToken)) {
                        logToErrorLogs("4 song.songUrl does not contain the updated token LOADING SONG DATA NOW $authToken, $songUrl")
                        loadSongData()
                        return
                    }
                }
            }
            logToErrorLogs("5 (songUrl is not null) reached the end of function")
        }
            ?: logToErrorLogs("6 song is null? ${(currentSong() == null)} . songUrl: ${currentSong()?.songUrl}.")
    }
}
