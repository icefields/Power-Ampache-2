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
import luci.sixsixsix.powerampache2.data.AmpachePreferencesRepositoryImpl
import luci.sixsixsix.powerampache2.data.ArtistsRepositoryImpl
import luci.sixsixsix.powerampache2.data.MusicRepositoryImpl
import luci.sixsixsix.powerampache2.data.PlaylistsRepositoryImpl
import luci.sixsixsix.powerampache2.data.PluginRepositoryImpl
import luci.sixsixsix.powerampache2.data.SettingsRepositoryImpl
import luci.sixsixsix.powerampache2.data.SharedPreferencesManagerImpl
import luci.sixsixsix.powerampache2.data.SongsRepositoryImpl
import luci.sixsixsix.powerampache2.data.local.DbDataSourceImpl
import luci.sixsixsix.powerampache2.data.local.StorageManagerImpl
import luci.sixsixsix.powerampache2.data.local.datasource.AlbumsDbDataSourceImpl
import luci.sixsixsix.powerampache2.data.local.datasource.ArtistsDbDataSourceImpl
import luci.sixsixsix.powerampache2.data.local.datasource.PlaylistsDbDataSourceImpl
import luci.sixsixsix.powerampache2.data.local.datasource.SongsDbDataSourceImpl
import luci.sixsixsix.powerampache2.data.offlinemode.AlbumsOfflineDataSourceImpl
import luci.sixsixsix.powerampache2.data.offlinemode.ArtistsOfflineDataSourceImpl
import luci.sixsixsix.powerampache2.data.offlinemode.PlaylistsOfflineDataSourceImpl
import luci.sixsixsix.powerampache2.data.offlinemode.SongsOfflineDataSourceImpl
import luci.sixsixsix.powerampache2.data.plugins.ChromecastPluginDataSourceImpl
import luci.sixsixsix.powerampache2.data.plugins.InfoPluginDataSourceImpl
import luci.sixsixsix.powerampache2.data.plugins.LyricsPluginDataSourceImpl
import luci.sixsixsix.powerampache2.data.remote.AmpacheInterceptor
import luci.sixsixsix.powerampache2.data.remote.datasource.AlbumsRemoteDataSourceImpl
import luci.sixsixsix.powerampache2.data.remote.datasource.ArtistsRemoteDataSourceImpl
import luci.sixsixsix.powerampache2.data.remote.datasource.PlaylistsRemoteDataSourceImpl
import luci.sixsixsix.powerampache2.data.remote.datasource.SongsRemoteDataSourceImpl
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.AmpachePreferencesRepository
import luci.sixsixsix.powerampache2.domain.ArtistsRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.PluginRepository
import luci.sixsixsix.powerampache2.domain.SettingsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.datasource.AlbumsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsOfflineModeDataSource
import luci.sixsixsix.powerampache2.domain.datasource.ArtistsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.DbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.datasource.PlaylistsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsDbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsOfflineDataSource
import luci.sixsixsix.powerampache2.domain.datasource.SongsRemoteDataSource
import luci.sixsixsix.powerampache2.domain.plugin.chromecast.ChromecastPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.info.InfoPluginDataSource
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.LyricsPluginDataSource
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import okhttp3.Interceptor
import javax.inject.Qualifier
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
    abstract fun bindPluginsRepository(
        pluginRepositoryImpl: PluginRepositoryImpl
    ): PluginRepository

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

//    @Binds
//    @Singleton
//    abstract fun bindShareManager(
//        shareManagerImpl: luci.sixsixsix.powerampache2.common.ShareManagerImpl
//    ): ShareManager

    @Binds
    @Singleton
    abstract fun bindSharedPreferencesManager(
        sharedPreferencesManagerImpl: SharedPreferencesManagerImpl
    ): SharedPreferencesManager

    @Binds
    @Singleton
    abstract fun bindAmpachePreferencesRepository(
        ampachePreferencesRepositoryImpl: AmpachePreferencesRepositoryImpl
    ): AmpachePreferencesRepository

    @Binds
    @Singleton
    abstract fun dbDataSourceProvider(
        dbDataSourceImpl: DbDataSourceImpl
    ): DbDataSource

    @Binds
    @Singleton
    @RemoteDataSource
    abstract fun artistsRemoteDataSourceProvider(
        artistsRemoteDataSourceImpl: ArtistsRemoteDataSourceImpl
    ): ArtistsRemoteDataSource

    @Binds
    @Singleton
    @LocalDataSource
    abstract fun artistsDbDataSourceProvider(
        artistsDbDataSourceImpl: ArtistsDbDataSourceImpl
    ): ArtistsDbDataSource

    @Binds
    @Singleton
    @OfflineModeDataSource
    abstract fun artistsOfflineDataSourceProvider(
        artistsOfflineDataSourceImpl: ArtistsOfflineDataSourceImpl
    ): ArtistsOfflineModeDataSource

    @Binds
    @Singleton
    @LocalDataSource
    abstract fun albumsDbDataSourceProvider(
        albumsDbDataSourceImpl: AlbumsDbDataSourceImpl
    ): AlbumsDbDataSource

    @Binds
    @Singleton
    @OfflineModeDataSource
    abstract fun albumsOfflineDataSourceProvider(
        albumsOfflineDataSourceImpl: AlbumsOfflineDataSourceImpl
    ): AlbumsOfflineDataSource

    @Binds
    @Singleton
    @RemoteDataSource
    abstract fun albumsRemoteDataSourceProvider(
        albumsRemoteDataSourceImpl: AlbumsRemoteDataSourceImpl
    ): AlbumsRemoteDataSource

    @Binds
    @Singleton
    @LocalDataSource
    abstract fun songsDbDataSourceProvider(
        songsDbDataSourceImpl: SongsDbDataSourceImpl
    ): SongsDbDataSource

    @Binds
    @Singleton
    @OfflineModeDataSource
    abstract fun songsOfflineDataSourceProvider(
        songsOfflineDataSourceImpl: SongsOfflineDataSourceImpl
    ): SongsOfflineDataSource

    @Binds
    @Singleton
    @RemoteDataSource
    abstract fun songsRemoteDataSourceProvider(
        songsRemoteDataSourceImpl: SongsRemoteDataSourceImpl
    ): SongsRemoteDataSource

    @Binds
    @Singleton
    @LocalDataSource
    abstract fun playlistsDbDataSourceProvider(
        playlistsDbDataSourceImpl: PlaylistsDbDataSourceImpl
    ): PlaylistsDbDataSource

    @Binds
    @Singleton
    @OfflineModeDataSource
    abstract fun playlistsOfflineDataSourceProvider(
        playlistsOfflineDataSourceImpl: PlaylistsOfflineDataSourceImpl
    ): PlaylistsOfflineDataSource

    @Binds
    @Singleton
    @RemoteDataSource
    abstract fun playlistsRemoteDataSourceProvider(
        playlistsRemoteDataSourceImpl: PlaylistsRemoteDataSourceImpl
    ): PlaylistsRemoteDataSource

    @Binds
    @PluginDataSource
    abstract fun lyricsPluginDataSourceProvider(
        lyricsPluginDataSourceImpl: LyricsPluginDataSourceImpl
    ): LyricsPluginDataSource

    @Binds
    @PluginDataSource
    abstract fun infoPluginDataSourceProvider(
        infoPluginDataSourceImpl: InfoPluginDataSourceImpl
    ): InfoPluginDataSource

    @Binds
    @PluginDataSource
    abstract fun chromecastPluginDataSourceProvider(
        chromecastPluginDataSourceImpl: ChromecastPluginDataSourceImpl
    ): ChromecastPluginDataSource
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineModeDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PluginDataSource
