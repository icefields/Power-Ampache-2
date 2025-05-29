package luci.sixsixsix.powerampache2.di

import android.app.Application
import android.content.Context
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import luci.sixsixsix.powerampache2.ImageLoaderProviderImpl
import luci.sixsixsix.powerampache2.data.common.Constants.DB_LOCAL_NAME
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.mapping.AmpacheDateMapper
import luci.sixsixsix.powerampache2.data.remote.AmpacheOkHttpClientBuilder
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.MainNetwork.Companion.BASE_URL
import luci.sixsixsix.powerampache2.data.remote.PingScheduler
import luci.sixsixsix.powerampache2.domain.common.Constants.TIMEOUT_CONNECTION_S
import luci.sixsixsix.powerampache2.domain.common.Constants.TIMEOUT_READ_S
import luci.sixsixsix.powerampache2.domain.common.Constants.TIMEOUT_WRITE_S
import luci.sixsixsix.powerampache2.domain.common.WeakContext
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.utils.AlarmScheduler
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider
import luci.sixsixsix.powerampache2.domain.utils.ImageLoaderProvider
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Provides application managed coroutine scope for usage in data layer.
     */
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        val okHttpClient = ampacheOkHttpClientBuilder(false)
//            .retryOnConnectionFailure(true)
//            .connectTimeout(TIMEOUT_CONNECTION_S, TimeUnit.SECONDS)
//            .readTimeout(TIMEOUT_READ_S, TimeUnit.SECONDS)
//            .writeTimeout(TIMEOUT_WRITE_S, TimeUnit.SECONDS)
//            .addInterceptor(interceptor)
//            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        interceptor: Interceptor,
        ampacheOkHttpClientBuilder: AmpacheOkHttpClientBuilder
    ) = ampacheOkHttpClientBuilder(false)
        .retryOnConnectionFailure(true)
        .connectTimeout(TIMEOUT_CONNECTION_S, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_READ_S, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_WRITE_S, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    @Provides
    fun provideAmpacheOkHttpClientBuilder(
        sharedPreferencesManager: SharedPreferencesManager,
        configProvider: ConfigProvider
    ): AmpacheOkHttpClientBuilder =
        AmpacheOkHttpClientBuilder(sharedPreferencesManager, configProvider)

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

    @Provides
    fun provideDataSourceFactory(
        @ApplicationContext context: Context,
        sharedPreferencesManager: SharedPreferencesManager,
        ampacheOkHttpClientBuilder: AmpacheOkHttpClientBuilder
    ) = if (sharedPreferencesManager.useOkHttpForExoPlayer) {
        OkHttpDataSource.Factory(
            ampacheOkHttpClientBuilder(addDefaultHeaderInterceptor = true)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()
        ).let { okHttpFactory ->
            DefaultDataSource.Factory(context, okHttpFactory)
        }
    } else {
        DefaultDataSource.Factory(context)
    }

    @Provides
    @Singleton
    fun provideImageLoaderProvider(
        @ApplicationContext context: Context,
        imageLoaderOkHttpClient: AmpacheOkHttpClientBuilder
    ): ImageLoaderProvider = ImageLoaderProviderImpl(context, imageLoaderOkHttpClient)
}
