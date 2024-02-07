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
package luci.sixsixsix.powerampache2.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import javax.inject.Inject

class SimpleMediaServiceHandler @Inject constructor(
    private val player: ExoPlayer,
    private val playlistManager: MusicPlaylistManager
): Player.Listener {
    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState = _simpleMediaState.asStateFlow()
    private var job: Job? = null

    init {
        L("SERVICE- SimpleMediaServiceHandler init")
        player.addListener(this)
        job = Job()
    }

    fun addMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun getMediaItemCount() = player.mediaItemCount

    fun addMediaItemList(mediaItems: List<MediaItem>) {
        L("addMediaItemList",mediaItems.size,"currently in the player: ", player.mediaItemCount)
        if(mediaItems.isNullOrEmpty() && player.mediaItemCount == 0) return
        // find current item in the list
        // split current list in 2, before and after current element
        // remove everything from mediaItems except the current
        if (
            player.mediaItemCount > 0 && playlistManager.getCurrentSong()?.mediaId == player.currentMediaItem?.mediaId
        ) {
            player.removeMediaItems(0, player.currentMediaItemIndex)
            player.removeMediaItems(player.currentMediaItemIndex + 1, player.mediaItemCount)

            val indexInQueue = mediaItems.map { mediaItem ->  mediaItem.mediaId }.indexOf(player.currentMediaItem?.mediaId)
            L("indexInQueue", indexInQueue)
            if (indexInQueue >= 0 && indexInQueue < mediaItems.size) {
                player.addMediaItems(0, mediaItems.subList(0, indexInQueue))
                player.addMediaItems(
                    player.currentMediaItemIndex + 1,
                    mediaItems.subList(indexInQueue + 1, mediaItems.size)
                )
            } else {
                player.setMediaItems(mediaItems)
            }
        } else {
            player.setMediaItems(mediaItems)
        }
        player.prepare()
    }

    fun isPlaying() = player.isPlaying

    suspend fun onPlayerEvent(playerEvent: PlayerEvent) {
        L(playerEvent)
        when (playerEvent) {
            PlayerEvent.Backward -> player.seekBack()
            PlayerEvent.Forward -> player.seekForward()
            PlayerEvent.PlayPause -> {
                if (player.isPlaying) {
                    player.pause()
                    stopProgressUpdate()
                } else {
                    player.play()
                    _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = true)
                    startProgressUpdate()
                }
            }
            is PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.Progress -> player.seekTo((player.duration * playerEvent.newProgress).toLong())
            PlayerEvent.SkipBack -> player.seekToPreviousMediaItem()
            PlayerEvent.SkipForward -> player.seekToNextMediaItem()
            is PlayerEvent.ForcePlay -> {
                var indexToSeekTo = -1
                var i = 0
                while(indexToSeekTo == -1 && i < player.mediaItemCount) {
                    L(player.getMediaItemAt(i).mediaId , playerEvent.mediaItem.mediaId)
                    if (player.getMediaItemAt(i).mediaId == playerEvent.mediaItem.mediaId) {
                        indexToSeekTo = i
                    }
                    ++i
                }

                if (indexToSeekTo >= 0) {
                    player.seekTo(indexToSeekTo, 0)
                }

                try {
                    player.play()
                    _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = true)
                    startProgressUpdate()
                } catch (e: Exception) {
                    L.e(e)
                }
            }
            is PlayerEvent.RepeatToggle -> player.repeatMode =
                when(playerEvent.repeatMode) {
                    RepeatMode.OFF -> Player.REPEAT_MODE_OFF
                    RepeatMode.ONE -> Player.REPEAT_MODE_ONE
                    RepeatMode.ALL -> Player.REPEAT_MODE_ALL
                }
            is PlayerEvent.ShuffleToggle -> player.shuffleModeEnabled = playerEvent.shuffleOn
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        // if the media player is handling a playlist, when changing song update UI accordingly
        try {
            val qq = playlistManager.currentQueueState.value.filter { it.mediaId == mediaItem?.mediaId }
            if (qq.isNotEmpty()) {
                playlistManager.updateCurrentSong(newSong = qq[0])
            }
        } catch (e: Exception) {
            L.e(e)
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> {
                L("STATE_IDLE")
                _simpleMediaState.value = SimpleMediaState.Idle
            }
            ExoPlayer.STATE_BUFFERING -> {
                L("STATE_BUFFERING")
                _simpleMediaState.value = SimpleMediaState.Buffering(player.currentPosition, player.isPlaying)
            }
            ExoPlayer.STATE_READY -> {
                _simpleMediaState.value = SimpleMediaState.Ready(player.duration)
                L("STATE_READY")
            }
            ExoPlayer.STATE_ENDED -> {
                _simpleMediaState.value = SimpleMediaState.Ended
                L("STATE_ENDED")
            }
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        L("STATE_isLoading", isLoading)
        _simpleMediaState.value = SimpleMediaState.Loading(isLoading)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = isPlaying)
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while(true) {
            delay(500)
            _simpleMediaState.value = SimpleMediaState.Progress(player.currentPosition, player.isPlaying)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = false)
    }
}

sealed class SimpleMediaState {
    data object Initial: SimpleMediaState()
    data class Ready(val duration: Long): SimpleMediaState()
    data class Buffering(val progress: Long, val isPlaying: Boolean): SimpleMediaState()
    data class Progress(val progress: Long, val isPlaying: Boolean): SimpleMediaState()
    data class Playing(val isPlaying: Boolean): SimpleMediaState()
    data class Loading(val isLoading: Boolean): SimpleMediaState()
    data object Idle: SimpleMediaState()
    data object Ended: SimpleMediaState()
}

sealed class PlayerEvent {
    data object Backward: PlayerEvent()
    data object Forward: PlayerEvent()
    data object SkipBack: PlayerEvent()
    data object SkipForward: PlayerEvent()
    data object PlayPause: PlayerEvent()
    data class ForcePlay(val mediaItem: MediaItem): PlayerEvent()
    data object Stop: PlayerEvent()
    data class Progress(val newProgress: Float): PlayerEvent()
    data class ShuffleToggle(val shuffleOn: Boolean): PlayerEvent()
    data class RepeatToggle(val repeatMode: RepeatMode): PlayerEvent()
}

enum class RepeatMode { OFF, ONE, ALL }
