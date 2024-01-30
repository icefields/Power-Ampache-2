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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.player.SimpleMediaNotificationManager
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import javax.inject.Singleton

@Module
//@InstallIn(ServiceComponent::class) // TODO: double check if Singleton is better
@InstallIn(SingletonComponent::class)
@OptIn(UnstableApi::class)
object ServiceModule {
    //@ServiceScoped
    @Singleton
    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    //@ServiceScoped
    @Singleton
    @Provides
    fun providePlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(context))
            .build()

    //@ServiceScoped
    @Singleton
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): SimpleMediaNotificationManager = SimpleMediaNotificationManager(
        context = context,
        player = player
    )

    //@ServiceScoped
    @Singleton
    @Provides
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ) = MediaSession.Builder(context, player).build()

    //@ServiceScoped
    @Singleton
    @Provides
    fun provideServiceHandler(player: ExoPlayer, playlistManager: MusicPlaylistManager) =
        SimpleMediaServiceHandler(playlistManager = playlistManager, player = player)
}
