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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.player.NOTIFICATION_INTENT_REQUEST_CODE
import luci.sixsixsix.powerampache2.player.PlayerManager
import luci.sixsixsix.powerampache2.player.SimpleMediaNotificationManager
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import luci.sixsixsix.powerampache2.presentation.MainActivity
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
@OptIn(UnstableApi::class)
object ServiceModule {

    @OptIn(UnstableApi::class)
    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        playerManager: PlayerManager
    ): SimpleMediaNotificationManager = SimpleMediaNotificationManager(
        context = context,
        playerManager = playerManager
    )

    @ServiceScoped
    @Provides
    fun provideMediaSession(
        @ApplicationContext context: Context,
        playerManager: PlayerManager
    ) = MediaSession.Builder(context, playerManager.player)
        .setSessionActivity(SimpleMediaNotificationManager.notificationPendingIntent(context))
        .build()

    //@ServiceScoped
    @Singleton
    @Provides
    fun provideServiceHandler(
        playerManager: PlayerManager,
        playlistManager: MusicPlaylistManager,
        errorHandler: ErrorHandler,
        @ApplicationContext context: Context
    ) =
        SimpleMediaServiceHandler(
            playlistManager = playlistManager,
            playerManager = playerManager,
            errorHandler = errorHandler,
            context = context
        )
}
