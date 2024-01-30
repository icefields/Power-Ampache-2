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
import luci.sixsixsix.powerampache2.domain.models.toMediaItem
import javax.inject.Inject

class SimpleMediaServiceHandler @Inject constructor(
    private val player: ExoPlayer,
    private val playlistManager: MusicPlaylistManager
): Player.Listener {
    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState = _simpleMediaState.asStateFlow()
    private var job: Job? = null

    init {
        player.addListener(this)
        job = Job()
    }

    fun addMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun addMediaItemList(mediaItems: List<MediaItem>) {
        // find current item in the list
        // split current list in 2, before and after current element
        // remove everything from mediaItems except the current
        if (
            player.mediaItemCount > 0 && playlistManager.getCurrentSong()?.mediaId == player.currentMediaItem?.mediaId
        ) {
            player.removeMediaItems(0, player.currentMediaItemIndex)
            player.removeMediaItems(player.currentMediaItemIndex + 1, player.mediaItemCount)

            val indexInQueue = mediaItems.indexOf(player.currentMediaItem)

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
                    if (player.getMediaItemAt(i) == playerEvent.mediaItem) {
                        indexToSeekTo = i
                    }
                    ++i
                }
                player.seekTo(indexToSeekTo, 0)
                player.play()
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = true)
                startProgressUpdate()
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
            playlistManager.updateCurrentSong(
                newSong = playlistManager.currentQueueState.value.filter { it.mediaId == mediaItem?.mediaId }[0]
            )
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
                _simpleMediaState.value = SimpleMediaState.Buffering(player.currentPosition)
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
            _simpleMediaState.value = SimpleMediaState.Progress(player.currentPosition)
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
    data class Buffering(val progress: Long): SimpleMediaState()
    data class Progress(val progress: Long): SimpleMediaState()
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
