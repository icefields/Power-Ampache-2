package luci.sixsixsix.powerampache2.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_CONNECTION_S
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_READ_S
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_WRITE_S
import luci.sixsixsix.powerampache2.data.mapping.AmpacheDateMapper
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.MainNetwork.Companion.BASE_URL
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.exoplayer.MusicServiceConnection
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicServiceConnection(@ApplicationContext context: Context) = MusicServiceConnection(context)

//    @Singleton
//    @Provides
//    fun provideSwipeSongAdapter() = SwipeSongAdapter()

    @Singleton
    @Provides
    fun provideGlideInstance(@ApplicationContext context: Context) =
        Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
            )

//    @Provides
//    fun provideInterceptor(@ApplicationContext appContext: Context): Interceptor =
//        AssetsNetworkInterceptor(appContext)

    @Provides
    @Singleton
    fun provideRetrofit(/*interceptor: Interceptor*/): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_CONNECTION_S, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ_S, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE_S, TimeUnit.SECONDS)
            //.addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideAmpacheApi(retrofit: Retrofit): MainNetwork = retrofit.create(MainNetwork::class.java)

    @Provides
    fun provideDateMapper(): DateMapper = AmpacheDateMapper()
}
