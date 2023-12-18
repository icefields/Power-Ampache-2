package luci.sixsixsix.powerampache2.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import luci.sixsixsix.powerampache2.common.Constants.mockSongs
import luci.sixsixsix.powerampache2.data.entities.Song
import luci.sixsixsix.powerampache2.data.remote.MusicDatabase

@Module
@InstallIn(ServiceComponent::class)
@OptIn(UnstableApi::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideMusicDatabase():MusicDatabase = object : MusicDatabase {
        override suspend fun getAllSongs(): List<Song> = mockSongs
    }

    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @ServiceScoped
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ) = ExoPlayer.Builder(context).build().apply {
        setAudioAttributes(audioAttributes, true)
        setHandleAudioBecomingNoisy(true)
    }

    /*ExoPlayer.Builder(context).build().apply {
    setAudioAttributes(audioAttributes, true)
    setHandleAudioBecomingNoisy(true)
}*/

    @ServiceScoped
    @Provides
    fun provideDataSourceFactory(
        @ApplicationContext context: Context
    ) = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Spotify App"))
}
