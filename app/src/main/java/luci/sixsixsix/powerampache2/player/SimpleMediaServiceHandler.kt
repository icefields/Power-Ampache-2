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

import android.media.session.PlaybackState
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException
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
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.domain.errors.AmpPlaybackError
import luci.sixsixsix.powerampache2.domain.errors.AmpPlaybackException
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.PlaybackError
import luci.sixsixsix.powerampache2.domain.errors.UserNotEnabledException
import javax.inject.Inject


@OptIn(DelicateCoroutinesApi::class)
class SimpleMediaServiceHandler @Inject constructor(
    private val player: ExoPlayer,
    private val playlistManager: MusicPlaylistManager,
    private val errorHandler: ErrorHandler
): Player.Listener {
    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState = _simpleMediaState.asStateFlow()
    private var job: Job? = null
    private var errorCounter = 0


    init {
        L("SERVICE- SimpleMediaServiceHandler init")
        player.addListener(this)
        job = Job()
    }

    fun isPlaying() = player.isPlaying

    fun addMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun addMediaItem(index: Int, mediaItem: MediaItem) {
        player.addMediaItem(index, mediaItem)
        player.prepare()
    }

    fun getMediaItemCount() = player.mediaItemCount

    fun addMediaItemList(mediaItems: List<MediaItem>) {
        if(mediaItems.isNullOrEmpty() && player.mediaItemCount == 0) return
        if (player.mediaItemCount > 0 &&
            playlistManager.currentSongState.value?.mediaId == player.currentMediaItem?.mediaId) {
            // if the current song of the playlist (if playlist is not empty) corresponds to the current
            // player song, ie. there is a song playing or paused and that song is inside both lists:
            // find current item in the list
            // split current list in 2, before and after current element
            // remove everything from mediaItems except the current
            player.removeMediaItems(0, player.currentMediaItemIndex)
            player.removeMediaItems(player.currentMediaItemIndex + 1, player.mediaItemCount)

            val indexInQueue = mediaItems.map { mediaItem ->  mediaItem.mediaId }.indexOf(player.currentMediaItem?.mediaId)
            if (!isPlaying() && indexInQueue > -1) {
                player.addMediaItem(indexInQueue, mediaItems[indexInQueue])
            }
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

    private fun indexOfSong(mediaId: String): Int {
        var indexToSeekTo = -1
        var i = 0
        while(indexToSeekTo == -1 && i < player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == mediaId) {
                indexToSeekTo = i
            }
            ++i
        }
        return indexToSeekTo
    }

    private suspend fun play() {
        // TODO check if there is actually songs in the queue before playing
        //  do the same with forceplay?
        if (simpleMediaState.value == SimpleMediaState.Idle ||
            simpleMediaState.value == SimpleMediaState.Ended ||
            player.playbackState == PlaybackState.STATE_STOPPED ||
            player.playbackState == PlaybackState.STATE_ERROR ||
            player.playbackState == PlaybackState.STATE_NONE) {
            L("onPlayerEvent !player.isPlaying, PrEPARE")

            player.prepare()
        }
        L("onPlayerEvent !player.isPlaying, play now")
        player.play()
        L("onPlayerEvent !player.isPlaying, play now -after called play")
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = player.isPlaying)
        startProgressUpdate()
    }

    suspend fun onPlayerEvent(playerEvent: PlayerEvent) {
        L(playerEvent, player.mediaItemCount, player.currentMediaItem?.mediaId, player.currentMediaItem?.mediaMetadata?.title)
        when (playerEvent) {
            PlayerEvent.Backward -> player.seekBack()
            PlayerEvent.Forward -> player.seekForward()
            PlayerEvent.PlayPause -> {
                if (player.isPlaying) {
                    L("onPlayerEvent player.isPlaying, pause now")
                    player.pause()
                    stopProgressUpdate()
                } else {
                    play()
                }
            }
            is PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.Progress -> player.seekTo((player.duration * playerEvent.newProgress).toLong())
            PlayerEvent.SkipBack -> player.seekToPreviousMediaItem()
            PlayerEvent.SkipForward -> player.seekToNextMediaItem()
            is PlayerEvent.ForcePlay -> {
                val indexToSeekTo = indexOfSong(playerEvent.mediaItem.mediaId)
                if (indexToSeekTo >= 0) {
                    // check if the url is the same
                    val media = player.getMediaItemAt(indexToSeekTo)
                    if (media.localConfiguration?.uri != playerEvent.mediaItem.localConfiguration?.uri) {
                        L("not equals!! ${media.localConfiguration?.uri} ${playerEvent.mediaItem.localConfiguration?.uri}")
                        player.replaceMediaItem(indexToSeekTo, media)
                    }

                    player.seekTo(indexToSeekTo, 0)
                } else {
                    addMediaItem(0, playerEvent.mediaItem)
                    player.seekTo(0, 0)
                }

                play()
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
                val song = qq[0]
                L("onMediaItemTransition.updateCurrentSong(it)")
                playlistManager.updateCurrentSong(newSong = song)

                // update media item if url is invalid
                // TODO generate url from repository and compare here, replace if necessary
//                if (mediaItem?.localConfiguration?.uri != song.songUrl) {
//                    L("equals!! ${media.localConfiguration?.uri} ${playerEvent.mediaItem.localConfiguration?.uri}")
//                    player.replaceMediaItem(indexToSeekTo, media)
//                }
            }
        } catch (e: Exception) {
            L.e("onMediaItemTransition", e)
            _simpleMediaState.value = SimpleMediaState.Error(
                playbackException = AmpPlaybackException(
                    error = PlaybackError(
                        errorCode = AmpPlaybackError.ERROR_MEDIA_TRANSITION,
                        exception = e
                    )
                )
            )
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> {
                L("STATE_IDLE")
                _simpleMediaState.value = SimpleMediaState.Idle
                retryPlay()
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

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onPlayerError(error: PlaybackException) {
        val isUserNotEnabledException =
                (error.cause is InvalidResponseCodeException) &&
                        (error.cause as InvalidResponseCodeException).responseCode == 403

        GlobalScope.launch {
            when (val cause = error.cause) {
                is HttpDataSourceException -> {
                    // An HTTP error occurred.
                    // It's possible to find out more about the error both by casting and by
                    // querying the cause.
                    if (cause is InvalidResponseCodeException) {
                        //(cause as InvalidResponseCodeException).headerFields
                        if (isUserNotEnabledException) {
                            errorHandler<UserNotEnabledException>(label = "onPlayerError Invalid response code ${cause.responseCode} - ${cause.responseMessage}", UserNotEnabledException(cause.responseMessage ?: "Invalid response code",error, cause.responseCode))
                        } else
                            errorHandler<InvalidResponseCodeException>(label = "onPlayerError Invalid response code ${cause.responseCode} - ${cause.responseMessage}", cause)
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.
                        errorHandler<HttpDataSourceException>(label = "onPlayerError HttpDataSourceException", error)
                    }
                }
                else -> {
                    errorHandler<PlaybackException>(label = "onPlayerError PlaybackException", error)
                    updateErrorStateOnPlaybackError(error)
                }
            }
        }
        retryPlay()
    }

    private fun updateErrorStateOnPlaybackError(error: PlaybackException) {
        _simpleMediaState.value = SimpleMediaState.Error(playbackException = AmpPlaybackException(
            error = PlaybackError(
                errorCode = when(error.errorCode) {
                    PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> AmpPlaybackError.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED
                    else -> AmpPlaybackError.OTHER
                },
                exception = error
            )
        ))
    }

    private fun retryPlay() {
        if (!player.isPlaying &&
            player.playbackState == ExoPlayer.STATE_IDLE
            /*&& !player.isLoading  && !isUserNotEnabledException*/) {
            L("retryPlay STATE")
            player.prepare()
            if (errorCounter++ % Constants.config.playbackErrorRetries == 0) {
                player.seekToNextMediaItem()
            }
        }
    }
}


sealed class SimpleMediaState {
    data object Initial: SimpleMediaState()
    data class Ready(val duration: Long): SimpleMediaState()
    data class Buffering(val progress: Long, val isPlaying: Boolean): SimpleMediaState()
    data class Progress(val progress: Long, val isPlaying: Boolean): SimpleMediaState()
    data class Playing(val isPlaying: Boolean): SimpleMediaState()
    data class Loading(val isLoading: Boolean): SimpleMediaState()
    data class Error(val playbackException: AmpPlaybackException): SimpleMediaState()
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
