package luci.sixsixsix.powerampache2.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @OptIn(UnstableApi::class)
@Inject constructor(
    @ApplicationContext val context: Context,
    private val audioAttributes: AudioAttributes,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val dataSourceFactory: DefaultDataSource.Factory,
    private val cache: SimpleCache
) {
    private var _player: ExoPlayer? = null

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState = _playerState.asStateFlow()

    val player: ExoPlayer
        @OptIn(UnstableApi::class)
        get() {
            if (_player == null || _player?.isReleased == true) {
                _player = createPlayer()
                _playerState.value = _player
            }
            return _player!!
        }

    @OptIn(UnstableApi::class)
    private fun createPlayer() = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .setRenderersFactory(
            DefaultRenderersFactory(context)
                // make software decoding switchable? (also uncomment ffmpeg in gradle)
//                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
//                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
                .setEnableDecoderFallback(true)
        )
        .setLoadControl(
            DefaultLoadControl.Builder()
                .setPrioritizeTimeOverSizeThresholds(sharedPreferencesManager.prioritizeTimeOverSizeThresholds)
                .setBackBuffer(sharedPreferencesManager.backBuffer, true)  // Retain back buffer data only up to the last keyframe (not very impactful for audio)
                .setTargetBufferBytes(sharedPreferencesManager.targetBufferBytes)
                .setBufferDurationsMs(
                    sharedPreferencesManager.minBufferMs,
                    sharedPreferencesManager.maxBufferMs,
                    sharedPreferencesManager.bufferForPlaybackMs,
                    sharedPreferencesManager.bufferForPlaybackAfterRebufferMs
                )
                .build()
        )
        .setMediaSourceFactory(
            DefaultMediaSourceFactory(context).setDataSourceFactory(
                CacheDataSource.Factory()
                    .setCache(cache)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                    .setUpstreamDataSourceFactory(
                        dataSourceFactory
                    )
            )
        )
        .build()
//        .also { pla ->
//            if (BuildConfig.DEBUG) {
//                pla.addListener(object : Player.Listener {
//                    var job: Job? = null
//                    override fun onPlaybackStateChanged(state: Int) {
//                        job?.cancel()
//                        job = GlobalScope.launch {
//                            while(true) {
//                                Handler(Looper.getMainLooper()).post {
//                                    println("aaaa ExoPlayer " + "Buffered: ${_player?.totalBufferedDuration} ms, Percent: ${_player?.bufferedPercentage}, BufferedPosition: ${_player?.bufferedPosition}")
//                                }
//                                delay(6666)
//                            }
//                        }
//                    }
//                })
//            }
//        }

    fun releasePlayer() {
        _player?.release()
        _player = null
        _playerState.value = null
    }

    // TODO: unused?
    fun isPlayerInitialized(): Boolean = _player != null
}