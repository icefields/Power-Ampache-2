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

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.data.AlbumsRepositoryImpl
import luci.sixsixsix.powerampache2.data.ArtistsRepositoryImpl
import luci.sixsixsix.powerampache2.data.ErrorHandlerImpl
import luci.sixsixsix.powerampache2.data.MusicRepositoryImpl
import luci.sixsixsix.powerampache2.data.PlaylistsRepositoryImpl
import luci.sixsixsix.powerampache2.data.SettingsRepositoryImpl
import luci.sixsixsix.powerampache2.data.ShareManagerImpl
import luci.sixsixsix.powerampache2.data.SharedPreferencesManagerImpl
import luci.sixsixsix.powerampache2.data.SongsRepositoryImpl
import luci.sixsixsix.powerampache2.data.local.StorageManagerImpl
import luci.sixsixsix.powerampache2.data.remote.AmpacheInterceptor
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.utils.ShareManager
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
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
    abstract fun bindArtistsRepository(
        artistsRepository: ArtistsRepositoryImpl
    ): ArtistsRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistsRepository(
        playlistsRepositoryImpl: PlaylistsRepositoryImpl
    ): PlaylistsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindInterceptor(
        interceptor: AmpacheInterceptor
    ): Interceptor

//    @Binds
//    @Singleton
//    abstract fun bindErrorHandler(
//        errorHandlerImpl: ErrorHandlerImpl
//    ): ErrorHandler

    @Binds
    @Singleton
    abstract fun bindStorageManager(
        storageManagerImpl: StorageManagerImpl
    ): StorageManager

    @Binds
    @Singleton
    abstract fun bindShareManager(
        shareManagerImpl: ShareManagerImpl
    ): ShareManager

    @Binds
    @Singleton
    abstract fun bindSharedPreferencesManager(
        sharedPreferencesManagerImpl: SharedPreferencesManagerImpl
    ): SharedPreferencesManager

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
