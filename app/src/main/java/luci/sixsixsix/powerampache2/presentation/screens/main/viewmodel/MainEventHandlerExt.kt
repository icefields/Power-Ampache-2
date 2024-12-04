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

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.SEARCH_TIMEOUT
import luci.sixsixsix.powerampache2.common.exportSong
import luci.sixsixsix.powerampache2.data.remote.worker.SongDownloadWorker
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.toMediaItem
import luci.sixsixsix.powerampache2.player.PlayerEvent

/**
 * UI ACTIONS AND EVENTS (play, stop, skip, like, download, etc ...)
 */
fun MainViewModel.handleEvent(event: MainEvent, context: Context) {
    when(event) {
        is MainEvent.OnSearchQueryChange -> {
            state = state.copy(searchQuery = event.query)
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(SEARCH_TIMEOUT)
                playlistManager.updateSearchQuery(event.query)
            }
        }
        MainEvent.PlayPauseCurrent -> currentSong()?.let { song ->
            if (loadSongDataJob?.isActive == true) {
                L( "MainEvent.PlayPauseCurrent", "loadSongDataJob?.isActive")
                loadSongDataJob?.invokeOnCompletion { thr ->
                    thr?.let {
                        // an error has occurred waiting for job completion
                        logToErrorLogs(thr.stackTraceToString())
                    } ?: run {
                        L( "MainEvent.PlayPauseCurrent", "invokeOnCompletion")
                        playPauseSong()
                    }
                }
            } else {
                L( "MainEvent.PlayPauseCurrent", "play directly", song)
                playPauseSong()
            }
        }
        is MainEvent.AddSongsToQueueAndPlay ->
            addSongsToQueueAndPlay(event.song, event.songList)
        is MainEvent.PlaySong ->
            playSong(event.song)
        is MainEvent.PlaySongReplacePlaylist ->
            playSongReplacePlaylist(event.song, event.songList)
        is MainEvent.PlaySongAddToQueueTop ->
            playSongAddToQueueTop(event.song, event.songList)
        is MainEvent.AddSongsToQueueAndPlayShuffled ->
            addSongsToQueueAndPlayShuffled(event.songList)

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
            shareManager.shareSongDeepLink(context, event.song)
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
        MainEvent.FavouriteSong -> currentSong()?.let {
            favouriteSong(it)
        }
        is MainEvent.OnDownloadedSongDelete ->
            deleteDownloadedSong(event.song)
        is MainEvent.OnDownloadSongs ->
            downloadSongs(event.songs)
        is MainEvent.OnStopDownloadSongs -> viewModelScope.launch {
            SongDownloadWorker.stopAllDownloads(context)
            observeDownloads(context)
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
                context.exportSong(event.song, songsRepository.getSongUri(event.song))
            } catch (e: Exception) {
                playlistManager.updateErrorLogMessage(e.stackTraceToString())
            }
        }

        is MainEvent.OnRateSong -> viewModelScope.launch {
            rateSong(event.song, event.rate)
        }

        MainEvent.OnEnableOfflineMode -> viewModelScope.launch {
            settingsRepository.toggleOfflineMode()
        }
    }
}

/**
 * to play albums and playlists
 */
fun MainViewModel.addSongsToQueueAndPlay(song: Song, songList: List<Song>) {
    startPlayLoading()
    playlistManager.updateCurrentSong(song)
    playlistManager.addToCurrentQueueTop(songList)
    play(song)
}

/**
 * select a single song, play, and put it on the top of the queue
 * the song list is just for verification (TODO: should that be optional?)
 */
private fun MainViewModel.playSongAddToQueueTop(song: Song, songList: List<Song>) {
    startPlayLoading()
    playlistManager.addToCurrentQueueUpdateTopSong(song, songList)
    play(song)
}

/**
 * select a single song, play, and put it on the top of the queue
 * the song list is just for verification (TODO: should that be optional?)
 */
private fun MainViewModel.playSongReplacePlaylist(song: Song, songList: List<Song>) {
    startPlayLoading()
    playlistManager.replaceQueuePlaySong(songList, song)
    play(song)
}

/**
 * select song from current queue and play
 */
private fun MainViewModel.playSong(song: Song) {
    startPlayLoading()
    playlistManager.updateCurrentSong(song)
    play(song)
}

private fun  MainViewModel.addSongsToQueueAndPlayShuffled(songList: List<Song>) {
    startPlayLoading()
    val shuffled = songList.shuffled()
    playlistManager.replaceCurrentQueue(shuffled)
    playlistManager.updateCurrentSong(shuffled[0])

    if (!isPlaying) {
        onEvent(MainEvent.PlayPauseCurrent)
    }
}

/**
 * the single duty of this function is to call playSongForce, either right away or upon
 * completion of loadSongDataJob
 *
 * call stopPlayLoading() in case of errors
 */
private fun MainViewModel.play(song: Song) {
    startPlayLoading()
    if (loadSongDataJob?.isActive == true) {
        loadSongDataJob?.invokeOnCompletion {
            it?.let { e ->
                stopPlayLoading()
                logToErrorLogs(e.stackTraceToString())
            } ?: run {
                playSongForce(song)
            }
        }
    } else {
        L( "MainEvent.Play", "play directly")
        playSongForce(song)
    }
}

private fun MainViewModel.playSongForce(song: Song) = viewModelScope.launch {
    L( "MainEvent.Play", "playing song")
    try {
        simpleMediaServiceHandler.onPlayerEvent(
            PlayerEvent.ForcePlay(
                song.toMediaItem(songsRepository.getSongUri(song)))
        )
    } catch (e: Exception) {
        logToErrorLogs("fun MainViewModel.playSongForce EXCEPTION, loading song data now")
        logToErrorLogs(e.stackTraceToString())
    }
    stopPlayLoading()
    L( "MainEvent.Play", "aaaa play song launched. After")
}

private fun MainViewModel.playPauseSong() = viewModelScope.launch {
    startPlayLoading()
    startMusicServiceIfNecessary()
    L( "MainEvent.Play", "playing song")
    try {
        simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
    } catch (e: Exception) {
        stopPlayLoading()
        logToErrorLogs(e.stackTraceToString())
    }
    stopPlayLoading()
    L("aaaa", "after play pause")
}
