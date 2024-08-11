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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.domain.errors.AmpPlaybackError
import luci.sixsixsix.powerampache2.player.SimpleMediaState
import java.util.concurrent.TimeUnit

fun MainViewModel.observePlayerEvents() {
    viewModelScope.launch {
        simpleMediaServiceHandler.simpleMediaState.collect { mediaState ->
            when (mediaState) {
                SimpleMediaState.Initial -> {
                    /* UI STATE Initial */
                }
                SimpleMediaState.Ended -> {
                    stopPlayLoading()
                    //isPlaying = mediaState.isPlaying
                }
                is SimpleMediaState.Buffering -> {
                    isBuffering = true
                    isPlaying = mediaState.isPlaying
                    calculateProgressValue(mediaState.progress)
                }
                is SimpleMediaState.Playing -> {
                    isPlaying = mediaState.isPlaying
                    if (isPlaying)
                        stopPlayLoading()
                }
                is SimpleMediaState.Progress -> {
                    isPlaying = mediaState.isPlaying
                    calculateProgressValue(mediaState.progress)
                    if (isPlaying)
                        stopPlayLoading()
                }
                is SimpleMediaState.Ready -> {
                    isBuffering = false
                    duration = mediaState.duration
                }
                is SimpleMediaState.Loading ->
                    isLoading = mediaState.isLoading
                SimpleMediaState.Idle -> {
                    isBuffering = false
                    isPlaying = false
                    stopPlayLoading()
                }

                is SimpleMediaState.Error ->  when (mediaState.playbackException.error.errorCode) {
                    AmpPlaybackError.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> {
                        // count how many of those errors in the next 30s
                        // if count>30 before 60s stop service
                        if (++playbackErrorCount > 30) {
                            playlistManager.reset()
                            stopMusicService()
                        }
                        // restart the timer at every error, if no error in the next 30s, reset the
                        // error count
                        errorJob?.cancel()
                        errorJob = viewModelScope.launch {
                            delay(Constants.PLAYBACK_ERROR_COUNT_TIMEOUT_MS)
                            playbackErrorCount = 0
                        }
                    }

                    AmpPlaybackError.OTHER -> { }
                    AmpPlaybackError.ERROR_MEDIA_TRANSITION -> { }
                }
            }
        }
    }
}

private var errorJob: Job? = null
private var playbackErrorCount = 0

private fun MainViewModel.calculateProgressValue(currentProgress: Long) {
    if (duration <= 0L) duration = (currentSong()?.time?.toLong() ?: 1) * 1000
    progress = if (currentProgress > 0) (currentProgress.toFloat() / duration) else 0f
    progressStr = formatDuration(currentProgress)
}

private fun formatDuration(duration: Long): String {
    val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds: Long = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
    return String.format("%02d:%02d", minutes, seconds)
}
