package luci.sixsixsix.powerampache2.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.data.MusicRepositoryImpl
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.mapping.AmpacheDateMapper
import luci.sixsixsix.powerampache2.data.remote.AmpacheInterceptor
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
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
    abstract fun bindInterceptor(
        interceptor: AmpacheInterceptor
    ): Interceptor

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
