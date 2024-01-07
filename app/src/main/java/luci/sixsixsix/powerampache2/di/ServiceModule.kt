package luci.sixsixsix.powerampache2.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.player.SimpleMediaNotificationManager
import luci.sixsixsix.powerampache2.player.SimpleMediaServiceHandler
import luci.sixsixsix.powerampache2.presentation.main.MusicPlaylistManager
import javax.inject.Singleton

@Module
//@InstallIn(ServiceComponent::class) // TODO: double check if Singleton is better
@InstallIn(SingletonComponent::class)
@OptIn(UnstableApi::class)
object ServiceModule {

    @Provides
    @Singleton
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

    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ) = MediaSession.Builder(context, player).build()

    @Provides
    @Singleton
    fun provideServiceHandler(player: ExoPlayer, playlistManager: MusicPlaylistManager) = SimpleMediaServiceHandler(playlistManager = playlistManager, player = player)

//    @ServiceScoped
//    @Provides
//    fun provideDataSourceFactory(
//        @ApplicationContext context: Context
//    ) = DefaultDataSource.Factory(context, DefaultDataSource.Factory(context))
}
