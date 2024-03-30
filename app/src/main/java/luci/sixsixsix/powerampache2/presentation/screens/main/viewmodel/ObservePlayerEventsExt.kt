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
import kotlinx.coroutines.launch
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
                    //isPlaying = mediaState.isPlaying
                }
                is SimpleMediaState.Buffering -> {
                    isBuffering = true
                    isPlaying = mediaState.isPlaying
                    calculateProgressValue(mediaState.progress)
                }
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
                SimpleMediaState.Idle -> {
                    isBuffering = false
                    isPlaying = false
                }
            }
        }
    }
}

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
