package luci.sixsixsix.powerampache2.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.data.AlbumsRepositoryImpl
import luci.sixsixsix.powerampache2.data.ErrorHandlerImpl
import luci.sixsixsix.powerampache2.data.MusicRepositoryImpl
import luci.sixsixsix.powerampache2.data.SongsRepositoryImpl
import luci.sixsixsix.powerampache2.data.remote.AmpacheInterceptor
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository

    @Binds
    @Singleton
    abstract fun bindAlbumsRepository(
        albumsRepositoryImpl: AlbumsRepositoryImpl
    ): AlbumsRepository

    @Binds
    @Singleton
    abstract fun bindSongsRepository(
        songsRepositoryImpl: SongsRepositoryImpl
    ): SongsRepository

    @Binds
    @Singleton
    abstract fun bindInterceptor(
        interceptor: AmpacheInterceptor
    ): Interceptor

    @Binds
    @Singleton
    abstract fun bindErrorHandler(
        errorHandlerImpl: ErrorHandlerImpl
    ): ErrorHandler

//    @Binds
//    @Singleton
//    abstract fun bindIntradayInfoParser(
//        intradayInfoParser: IntradayInfoParser
//    ): CSVParser<IntradayInfo>
//
//
//    @Binds
//    @Singleton
//    abstract fun bindCompanyListingsParser(
//        companyListingsParser: CompanyListingsParser
//    ): CSVParser<CompanyListing>

}
