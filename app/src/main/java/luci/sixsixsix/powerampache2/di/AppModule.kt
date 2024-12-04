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

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.DB_LOCAL_NAME
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_CONNECTION_S
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_READ_S
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_WRITE_S
import luci.sixsixsix.powerampache2.common.WeakContext
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.mapping.AmpacheDateMapper
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.MainNetwork.Companion.BASE_URL
import luci.sixsixsix.powerampache2.data.remote.PingScheduler
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.utils.AlarmScheduler
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(interceptor: Interceptor): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(TIMEOUT_CONNECTION_S, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ_S, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE_S, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideDateMapper(): DateMapper =
        AmpacheDateMapper()

    @Provides
    @Singleton
    fun provideAmpacheApi(retrofit: Retrofit): MainNetwork =
        retrofit.create(MainNetwork::class.java)

    @Provides
    @Singleton
    fun provideWeakApplicationContext(application: Application) =
        WeakContext(application.applicationContext)

    @Provides
    @Singleton
    fun provideAlarmScheduler(application: Application): AlarmScheduler =
        PingScheduler(application)

    @Provides
    @Singleton
    fun provideMusicDatabase(application: Application): MusicDatabase =
        Room.databaseBuilder(
            application,
            MusicDatabase::class.java,
            DB_LOCAL_NAME
        )
        //.fallbackToDestructiveMigration()
        //.addMigrations(MIGRATION_73_74())
        .build()


    @Singleton
    @Provides
    fun provideServiceHandler(player: ExoPlayer, playlistManager: MusicPlaylistManager, errorHandler: ErrorHandler) =
        SimpleMediaServiceHandler(
            playlistManager = playlistManager,
            player = player,
            errorHandler = errorHandler
        )

    //@ServiceScoped
    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun providePlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes,
        sharedPreferencesManager: SharedPreferencesManager
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .setLoadControl(DefaultLoadControl.Builder()
            .setPrioritizeTimeOverSizeThresholds(true)
            .setBackBuffer(sharedPreferencesManager.backBuffer, true)  // Retain back buffer data only up to the last keyframe (not very impactful for audio)
            //.setTargetBufferBytes(20 * 1024 * 1024)
            .setBufferDurationsMs(
                sharedPreferencesManager.minBufferMs,
                sharedPreferencesManager.maxBufferMs,
                sharedPreferencesManager.bufferForPlaybackMs,
                sharedPreferencesManager.bufferForPlaybackAfterRebufferMs
            )
            .build())
        .build()

    //@ServiceScoped
    @Singleton
    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()
}
