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
package luci.sixsixsix.powerampache2.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.common.ConfigProviderImpl
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //@ServiceScoped
    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun providePlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes,
        sharedPreferencesManager: SharedPreferencesManager,
        dataSourceFactory: DefaultDataSource.Factory,
        cache: SimpleCache
    ) = ExoPlayer.Builder(context)
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



    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun providePlayerCache(
        @ApplicationContext context: Context,
        sharedPreferencesManager: SharedPreferencesManager
    ) = SimpleCache(
        File(context.cacheDir, "pa2_media_cache"),
        LeastRecentlyUsedCacheEvictor(sharedPreferencesManager.cacheSizeMb.toLong() * 1024L * 1024L),
        StandaloneDatabaseProvider(context)
    )

    //@ServiceScoped
    @Singleton
    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()


    @Provides
    @Singleton
    fun provideConfigProvider(): ConfigProvider = ConfigProviderImpl()
}
