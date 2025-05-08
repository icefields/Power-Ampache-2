package luci.sixsixsix.powerampache2.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import dagger.hilt.android.qualifiers.ApplicationContext
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

    val player: ExoPlayer
        get() {
            if (_player == null) {
                _player = createPlayer()
            }
            return _player!!
        }

    private fun createPlayer() = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .setLoadControl(
            DefaultLoadControl.Builder()
                .setPrioritizeTimeOverSizeThresholds(true)
                .setBackBuffer(sharedPreferencesManager.backBuffer, true)  // Retain back buffer data only up to the last keyframe (not very impactful for audio)
                //.setTargetBufferBytes(20 * 1024 * 1024)
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

    fun releasePlayer() {
        _player?.release()
        _player = null
    }

    fun isPlayerInitialized(): Boolean = _player != null
}